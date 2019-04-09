
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;

import javax.swing.JPanel;

public class Player extends JPanel {

    public int x, y;
    int asgn;
    public Player() {
        this.setSize(StaticContainer.PLAYER_W, StaticContainer.PLAYER_H);
        this.setForeground(getForeground());
        this.setOpaque(StaticContainer.OBJECT_BG_OPAQUE);
        this.x = 145;
        y = 420;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        //System.out.println(asgn);
 
        g2d.drawImage(new ImageIcon(".\\src\\images\\car3.png").getImage(), 0, 0, 50, 80, null);
        this.setSize(50,80);
    }
}