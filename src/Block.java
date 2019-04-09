
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import javax.swing.JPanel;

public class Block extends JPanel {

    public int x, y;
    //private Image image;
    Random r;
    int asgn;
    

    public Block(int x, int y) {
        this.x = x;
        this.y = y;
        this.setSize(StaticContainer.BLOCK_W, StaticContainer.BLOCK_H);
        this.setBackground(getForeground());
        this.setOpaque(StaticContainer.OBJECT_BG_OPAQUE);
        r=new Random();
        asgn=r.nextInt(8);
    }

    
    public void move(int i) {
        if (y < StaticContainer.GAME_H) {
            y += i;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        Image img = null;

        // System.out.println(asgn);
        try {
            switch (asgn) {
                case 0:
                    img = ImageIO.read(new File(".\\src\\images\\barricade.png"));
                    g2d.drawImage(img, 0, 0, 70, 70, null);
                    this.setSize(70,70);
                    break;
                case 1:
                    img = ImageIO.read(new File(".\\src\\images\\barricade2.png"));
                    g2d.drawImage(img, 0, 0, 70, 60, null);
                    this.setSize(70,60);
                    break;
                case 2:
                    g2d.drawImage(new ImageIcon(".\\src\\images\\barricade3.png").getImage(), 0, 0, 80, 75, null);
                    this.setSize(80,75);
                    break;
                case 3:
                    g2d.drawImage(new ImageIcon(".\\src\\images\\cone.png").getImage(), 0, 0, 60, 70, null);
                    this.setSize(60,70);
                    break;
                case 4:
                    g2d.drawImage(new ImageIcon(".\\src\\images\\construction.png").getImage(), 0, 0, 60, 90, null);
                    this.setSize(60,90);
                    break;
                case 5:
                    g2d.drawImage(new ImageIcon(".\\src\\images\\drum.png").getImage(), 0, 0, 60, 80, null);
                    this.setSize(60,80);
                    break;
                case 6:
                    g2d.drawImage(new ImageIcon(".\\src\\images\\roadclosed.png").getImage(), 0, 0, 60, 90, null);
                    this.setSize(60,90);
                    break;
                case 7:
                    g2d.drawImage(new ImageIcon(".\\src\\images\\stop.png").getImage(), 0, 0, 55, 90, null);
                    this.setSize(55,90);
                    break;
            }
        }
        catch(Exception ex) {
            
        }
        //g2d.drawImage(new ImageIcon(".\\src\\images\\car.png").getImage(), 0, 0, 25, 25, null);
    }

}