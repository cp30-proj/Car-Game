import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Text extends TransparentPanel {

    public int x, y;
    Random r;
    int asgn;
    String text="";
    JLabel label = null;
    JLabel buffer = null;

    public Text(int x, int y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.setBackground(new Color(255,255,255,100));
        r = new Random();
        asgn = r.nextInt(8);

        label = new JLabel(text);
        label.setVisible(true);
        label.setLocation(10, 10);
        label.setSize(50, 20);
        this.setSize(label.getWidth()+5, label.getHeight());
        buffer=label;
        this.add(buffer);

        //score = new JLabel(""+scoreValue);
        //score.setVisible(true);
        //score.setSize(50,50);
        //score.setLocation(10, 60);
        //this.add(score);
    }

    public void setText(String text) {
        this.text=text;
        label.setText(text);
    }

    public JLabel getLabel(){
        return label;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        this.remove(buffer);
        buffer=label;
//        label = new JLabel(text);
//        label.setVisible(true);
//        label.setBackground(new Color(200, 0, 0, 200));
//        label.setLocation(50, 50);
//        label.setSize(50, 50);
//        label.setVisible(true);
        this.add(buffer);
    }

}