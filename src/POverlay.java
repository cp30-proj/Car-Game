
import acm.graphics.GImage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class POverlay extends TransparentPanel {
    
    int x,y;
    Random r;
    int d;
   
    
    //GImage road = new GImage("");
    
    public POverlay() {
        
        this.setSize(StaticContainer.GAME_W, StaticContainer.GAME_H);
        this.setForeground(getForeground());
        this.x = 0;
        y = 0;
        d = 0;
        this.setBackground(new Color(0,0,0,200));
    }

    
//    @Override
//    public void paint(Graphics g) {
//        super.paint(g);
//        Graphics2D g2d = (Graphics2D) g;
//       
//        //g2d.drawImage(new ImageIcon(".\\src\\images\\car.png").getImage(), 0, 0, 25, 25, null);
//    }
    
}