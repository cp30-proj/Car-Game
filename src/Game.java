
import acm.graphics.GImage;
import acm.graphics.GLabel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import com.fazecast.jSerialComm.*;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Game extends JFrame implements KeyListener, Runnable {

//    private MyList<Integer> highScore = new MyList<Integer>();
    private List<Block> blocks;
    private Player player;
    private int spawnTimer, spawnConst, difficulty, playerSpeed;
    private Background background;
    private GameOver gameOver;
    private POverlay pScreen;

    // Speed control limits
    private int[] speeds;
    private int[] minVals;
    private int[] maxVals;

    public int playerChoice = 0;
    public int scores = 0;
    //private int index;

    private Random r;

    // Player position limits
    public int playerXLeft;
    public int playerXRight;

    int maxSpawnX = StaticContainer.GAME_W - StaticContainer.BLOCK_W;

    GImage car = new GImage("");

    static int curConVal = 0;     //current controller Value
    static boolean isPortConnected = false;
    static boolean isPaused = false;
    static boolean isGameOver = false;
    static boolean isRestart = false;;
    static boolean isPressed = false;
    static boolean isStart = false;
    static boolean userStart = false;
    
    ////////////////////////////////////////////////////////////////////////////
    //////////////////////----- UCMI Connection -----///////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    UCMI ucmi = new UCMI();
    ControlConfig ctrlConfig;
    boolean onceInit = false;
    
    
    
    

    public Game() {
        init();
    }

    private void init() {
        if(!onceInit){
            ////////////////////////////////////////////////////////////////////////////
            //////////////////////----- UCMI Connection -----///////////////////////////
            ////////////////////////////////////////////////////////////////////////////
            ucmi.init();
            if(ucmi.isPortConnected){
                ucmi.ReqPlayer(1);
                System.out.println("port connected. 1st requests sent.");
            }
            ////////////////////////////////
            /// defining controls (default)
            ///////////////////////////////
            ctrlConfig = new ControlConfig(ucmi);
            ///////
            String[] genControlNames = new String[]{"Up","Down","Left","Right","\'SPACEBAR\'","Quit/Settings(\'E\')"}; //please insrt new controls at end, unless wiling to change indexing in code.
            double[]  genDirGrpNo = new double[]{1.1,1.2,1.3,1.4,2,3};
            ///////
            ctrlConfig.gameControls = new String[2][]; //player indexing starts at '1'
            for (int i = 1; i < ctrlConfig.gameControls.length; i++) {
                ctrlConfig.gameControls[i] =  genControlNames;
            }
            ////////
            ctrlConfig.directionalGroupNo = new double[2][]; //player indexing starts at '1'
            for (int i = 1; i < ctrlConfig.directionalGroupNo.length; i++) {
                ctrlConfig.directionalGroupNo[i] = genDirGrpNo;
            }
            ////////
            ctrlConfig.kybdControls = new int[2][]; //player indexing starts at '1'
            ctrlConfig.kybdControls[1] = new int[]{KeyEvent.VK_UP,KeyEvent.VK_DOWN,KeyEvent.VK_LEFT,KeyEvent.VK_RIGHT,KeyEvent.VK_SPACE,KeyEvent.VK_ESCAPE};    //P1 default
            ////////
            ctrlConfig.uartControls = new CM[2][]; //player indexing starts at '1'
            CM[] genUart = new CM[]{CM.LEFT_ANALOG_STICK_Y,CM.LEFT_ANALOG_STICK_Y,CM.LEFT_ANALOG_STICK_X,CM.LEFT_ANALOG_STICK_X,CM.A_FACE_BUTTON,CM.SELECT_BUTTON};
            for (int i = 1; i < ctrlConfig.uartControls.length; i++) {
                ctrlConfig.uartControls[i] = genUart;
            }
            ////////
            ctrlConfig.uartHolddownWaitCount = new int[ctrlConfig.gameControls.length][ctrlConfig.gameControls[1].length];
            for (int i = 1; i < ctrlConfig.uartHolddownWaitCount.length; i++) {
                for (int j = 0; j < ctrlConfig.uartHolddownWaitCount[i].length; j++) {
                    ctrlConfig.uartHolddownWaitCount[i][j] = 0;
                }
            }
            onceInit = true;
        }
        
        
        
        ///////////
        //highScore.createList();
        setTitle("Car Brick Game");
        getContentPane().setLayout(null);
        setSize(StaticContainer.GAME_W, StaticContainer.GAME_H);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0, 0, 0, 0));
        addKeyListener(this);

        r = new Random();
        spawnTimer = 0;
        difficulty = 5;
        playerSpeed = 30;
        spawnConst = 40;
        //index = 0;
        blocks = new ArrayList<>();
        player = new Player();
        background = new Background();
        gameOver = new GameOver();
        pScreen = new POverlay();
        blocks.add(new Block(r.nextInt(maxSpawnX), 0));
        
        isGameOver = false;
        isRestart = false;;
        isPressed = false;
        isStart = false;
        userStart = false;
        //drawFrame();
        //    ImageIcon image = new ImageIcon(".\\src\\images\\car.png");
        //   JLabel imageLabel = new JLabel(image); 
        //   add(imageLabel);
        //  imageLabel.setVisible(true);
        //   imageLabel.setBounds(0, 0, 200, 200);
        //System.out.println("zzzzz");

        speeds = new int[]{-20, -10, -5, 0, 5, 10, 20};
        minVals = new int[]{0, 146, 293, 440, 583, 730, 770};
        maxVals = new int[]{146, 293, 440, 583, 730, 770, 1023};
        playerXLeft = 100;
        playerXRight = 752; //player is 50 pixels, so wall starts at x=799 //actual drawing width is abt 47 pixels
        scores = 0;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(new ImageIcon(".\\src\\images\\COVER2.jpg").getImage(), 0, 0, 900, 600, null);

    }
    Text pause=new Text(StaticContainer.GAME_W/2, StaticContainer.GAME_H/2, "- PAUSED -");
    
    private void drawFrame() {
        getContentPane().removeAll();

        //this is drawn in order below, from front to back
        if (isPaused) {
            pause.setLocation(pause.x, pause.y);
            pause.setVisible(true);
            pause.setText("- PAUSED -");
            getContentPane().add(pause);
            
            pScreen.setLocation(pScreen.x, pScreen.y);
            getContentPane().add(pScreen);
        }

        try {//prevent internal error from too much blocks spawned (settings extreme)
            for (Block b : blocks) {
                b.move(difficulty);
                b.setLocation(b.x, b.y);
                getContentPane().add(b);
            }

            obstacleCounter();
            scText.setLocation(scText.x, scText.y);
            scText.setVisible(true);
            scText.setText("Score: " + scores);
            getContentPane().add(scText);

            player.setLocation(player.x, player.y + 50);
            getContentPane().add(player);

            background.setLocation(background.x, background.y);
            getContentPane().add(background);

            getContentPane().repaint();
            getContentPane().revalidate();
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println(ex.getMessage());
        }
    }
    Text scText = new Text(10, 10, "Score: 0");

    private void obstacleCounter() {
        for (int i = 0; i < blocks.size(); i++) {
            Block b = blocks.get(i);
            if (b.y >= /*player.y*/ StaticContainer.GAME_H) {
                //index = index +1;
                blocks.remove(i);
                scores = scores + 1;
                System.out.println("Score : " + scores);
            }
        }

    }

    private void spawn(int i) {
        //int ex = StaticContainer.GAME_W - StaticContainer.BLOCK_W;
        if (i == spawnConst) {
            Block b = new Block(0, 0);
            if(blocks.isEmpty()){
                b.x = r.nextInt(699 - (int) ((double) b.getWidth() * 1.5) - 200 - 20) + 100 + 5;//20 is allowance
            } else {
                b.x = r.nextInt(799 - (int) ((double) b.getWidth() * 1.5) - 100 - 20) + 100 + 5;//20 is allowance
            }
            b.y = 0 - (int) ((double) b.getHeight() * 1.5);
            b.setLocation(b.x, b.y);
            blocks.add(b);
        }
    }

    private boolean checkCollision() {
        for (Block b : blocks) {
            if (b.y + StaticContainer.BLOCK_H >= player.y
                    && b.y <= player.y + StaticContainer.PLAYER_H
                    && b.x >= player.x - StaticContainer.PLAYER_W
                    && b.x <= player.x + StaticContainer.PLAYER_W) {
                return true;
            }
        }
        return false;
    }

   // Options optWin = new Options();
    static Scanner sin = new Scanner(System.in);
    //**********IMPLEMENTED  
    @Override
    public void run() {
        

        while (true) {
            while(userStart==false){
                try {
                    Thread.sleep(300);
                    uartReadLoopIterate();
                } catch (InterruptedException ie) {
                }
            }
            isStart=true;
            //game loop
            while (true) {
                if (!isPaused) {//not paused
                    drawFrame();
                    spawn(spawnTimer);
                    if (++spawnTimer > spawnConst) {
                        spawnTimer = 0;
                    }
                    if (checkCollision()) {
                        if (StaticContainer.NO_COLLISIONS) {
                            //System.out.println("collision detected. Timestamp:" + LocalTime.now().toString());
                        } else {
                            break;//break free of game loop
                        }
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                        this.dispose();
                    }
                    /*if (isPortConnected) {
                        setSpeed(1023 - curConVal);
                        if (StaticContainer.PRINT_CURCONVAL) {
                            System.out.println("curConVal = " + curConVal);
                        }
                    }*/
                    if(lf){
                        moveLf();
                    }
                    if(rt){
                        moveRt();
                    }
                    uartReadLoopIterate();
                } else { //paused
                    try {
                        uartReadLoopIterate();
                        Thread.sleep(0);
                    } catch (InterruptedException ex) {
                    }//do nothing
                    //optWin.
                }
            }

            getContentPane().removeAll();
            gameOver.setLocation(gameOver.x, gameOver.y);
            getContentPane().add(gameOver);
            getContentPane().repaint();
            getContentPane().revalidate();
            
            isGameOver=true;
            while(isPressed==false){
                try {
                    uartReadLoopIterate();
                    Thread.sleep(0);
                } catch (InterruptedException ex) {
                }//do nothing
            }
            //pressed
            if(isRestart){
                init();
            } else {
                break;
            }
        }//continue display splash screen for a while

        //--end
        this.dispose();
        System.exit(0);
    }

    //first called when game is paused or resumed
    public void pauseResume() {
        if (isPaused) {
            drawFrame();
        }
    }

    
    boolean lf = false,rt = false;
    @Override
    public void keyPressed(KeyEvent e) {
        //vvvv Remember: Controls indexing Starts with [0]; players indexing starts with [1].
        //String[] genControlNames = new String[]{"Up","Down","Left","Right","Start","Select","Quit"}; <--from top-((as of 10:34 pc clk))]
        int id = e.getKeyCode();
        //System.out.println("id = " + id);
        //System.out.println("pressed: "+e.getKeyText(id));
        //System.out.println("kybdCtrls[1]:"+Arrays.toString(ctrlConfig.kybdControls[1]));
        //System.out.println("kybdCtrls[2]:"+Arrays.toString(ctrlConfig.kybdControls[2]));
        
        if(id == ctrlConfig.kybdControls[1][2]){//p1 left
            lf = true;
        }
        if(id == ctrlConfig.kybdControls[1][3]){//p1 right
            rt = true;
        }
        
    }
    

    @Override
    public void keyReleased(KeyEvent e) {
        //vvvv Remember: Controls indexing Starts with [0]; players indexing starts with [1].
        //String[] genControlNames = new String[]{"Up","Down","Left","Right","Start","Select","Quit"}; <--from top-((as of 10:34 pc clk))]
        int id = e.getKeyCode();
        
        if(id == ctrlConfig.kybdControls[1][2]){//p1 left
            lf = false;
        } 
        if(id == ctrlConfig.kybdControls[1][3]){//p1 right
            rt = false;
        } 
        if(id == ctrlConfig.kybdControls[1][5]){//any select (control settings)
            actQuitSettings();
        } 
        if(id == ctrlConfig.kybdControls[1][4]){//any start
            actStart();
        } 
    }
    
    private void actStart(){    //start or pause
        if (isStart == false){
            userStart=true;
        } else if (isGameOver == false) {
            isPaused = !isPaused;
            pauseResume();
        } else {
            isRestart=true;
            isPressed=true;
        }
    }
    
    private void actQuitSettings(){  //open control settings
        if (isGameOver == true) {
            isRestart=false;
            isPressed=true;
        } else if(ctrlConfig.jframe == null  || !ctrlConfig.jframe.isVisible()){
            ctrlConfig.openSettings(1, 0, 1, 2, 3, 4);
        }
    }
    
    private void moveRt() {
        //System.out.println("rt yo");
        if (player.x + playerSpeed <= playerXRight - playerSpeed) {
            player.x += playerSpeed;
        } else {
            player.x = playerXRight;
        }
    }

    private void moveLf() {
        //System.out.println("lf ye");
        if (player.x - playerSpeed >= playerXLeft) {
            player.x -= playerSpeed;
        } else {
            player.x = playerXLeft;
        }
    }
    
    private void moveRt(double percentSpd) {
        int speed = (int)(percentSpd/100*playerSpeed);
        if(speed<0){
            speed = 1;
        }
        //System.out.println("rt yo");
        if (player.x + speed <= playerXRight - speed) {
            player.x += speed;
        } else {
            player.x = playerXRight;
        }
    }

    private void moveLf(double percentSpd) {
        int speed = (int)(percentSpd/100*playerSpeed);
        if(speed<0){
            speed = 1;
        }
        //System.out.println("lf ye");
        if (player.x - speed >= playerXLeft) {
            player.x -= speed;
        } else {
            player.x = playerXLeft;
        }
    }    
    
    private void uartReadLoopIterate() {
        ////////////////////////////////////////////////////////////////////////////
        //////////////////////----- UCMI Connection -----///////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        //uart controls (PADDLE only)
        // ucmi version (both players)
        if (ucmi.isPortConnected && !ctrlConfig.disableCMInput){/////////////////BUG (seems like it)
            //---- [2],[3]    CAR CONTROLS
            //car control ("LEFT" and "RIGHT") ==> index [2] and [3]
            if(ctrlConfig.uartControls[1][2].isAnalogAxis()){
                int val1 = ucmi.p[1].readAnalogAxis(ctrlConfig.uartControls[1][2]);  //0 to 255
                val1 = val1 - 128;
                double percentage = val1;  //(val1/100)*100;      //i made maximum analog stick 100 (instead of 127)
                if(percentage > 0){
                    /*if(percentage>100){
                    percentage = 100;
                    }*/
                    moveRt(percentage);
                } else if (percentage < 0){
                    percentage = -percentage;
                    /*if(percentage>100){
                    percentage = 100;
                    }*/
                    moveLf(percentage);
                }
            } else {
                if(ucmi.p[1].readButton(ctrlConfig.uartControls[1][2])){
                    moveRt();
                }
                if(ucmi.p[1].readButton(ctrlConfig.uartControls[1][3])){
                    moveLf();
                }
                
            }
            
            
            //---- [4]-Start; [5] Quit/ConSett
            //any:  start   [NO repeat press on hold]
            if(ucmi.p[1].readButton(ctrlConfig.uartControls[1][4])){
                switch (ctrlConfig.uartHolddownWaitCount[1][4]){
                    case ControlConfig.UART_HOLD_DOWN_WAIT_TIME:
                        //actStart();   [NO repeat press on hold]
                        break;
                    case 0:
                        actStart();
                        ctrlConfig.uartHolddownWaitCount[1][4]++;
                        break;
                    default:
                        ctrlConfig.uartHolddownWaitCount[1][4]++;
                        break;
                }
            } else {ctrlConfig.uartHolddownWaitCount[1][4]=0;}
            
            
            //any: quit   [NO repeat press on hold]
            if(ucmi.p[1].readButton(ctrlConfig.uartControls[1][5])){
                switch (ctrlConfig.uartHolddownWaitCount[1][5]){
                    case ControlConfig.UART_HOLD_DOWN_WAIT_TIME:
                        //actQuit();   [NO repeat press on hold]
                        break;
                    case 0:
                        actQuitSettings();
                        ctrlConfig.uartHolddownWaitCount[1][5]++;
                        break;
                    default:
                        ctrlConfig.uartHolddownWaitCount[1][5]++;
                        break;
                }
            } else {ctrlConfig.uartHolddownWaitCount[1][5]=0;}
        }
    }
    
    private void uartLoopExitResetHoldDown() {
        //////////////////////// UCMI YEAH
        //since by this point is outside read loop
        ctrlConfig.uartHolddownWaitCount[1][4]=0;
        ctrlConfig.uartHolddownWaitCount[1][5]=0;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void setSpeed(int speed) {  //input 0 to 1023
        for (int i = 0; i < speeds.length; i++) {
            if (speed >= minVals[i] && speed < maxVals[i]) {
                playerSpeed = speeds[i];
                break;
            }
        }
        int newPlayerX = player.x + playerSpeed;
        if (newPlayerX >= playerXLeft && newPlayerX <= playerXRight) {
            player.x = newPlayerX;
        } else if (newPlayerX < playerXLeft) {
            player.x = playerXLeft;
        } else {
            player.x = playerXRight;
        }
    }

    private void add(GLabel scoreDisplay, int i, int i0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}