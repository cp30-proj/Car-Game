
import acm.graphics.GImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rjda2
 */
public class GameOver extends JPanel {
    
    int x,y;
    Random r;
    int d;
   
    
    //GImage road = new GImage("");
    
    public GameOver() {
        r = new Random();
        this.setSize(StaticContainer.GAME_W, StaticContainer.GAME_H);
        this.setForeground(getForeground());
        this.x = 0;
        y = 0;
        d = 0;
    }

    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
 
        g2d.drawImage(new ImageIcon(".\\src\\images\\gameover.jpg").getImage(), 0, 0, 900, 600, null);
        this.setSize(900,600);
    }
    
}