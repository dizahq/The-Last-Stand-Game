package Codes;

import java.awt.Toolkit;
import javax.swing.JFrame;

public class TheLastStand extends JFrame{
    private static int frameWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static int frameHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    
    public TheLastStand(){
        setBounds(0, 0, frameWidth, frameHeight);
        setUndecorated(true);
        setLocationRelativeTo(null);
        
        add(new MainPanel());
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Testss:
        System.out.println("Your screen size is " + frameWidth + "x" + frameHeight);
    }

    public static int getFrameWidth() {
        return frameWidth;
    }
    public static int getFrameHeight() {
        return frameHeight;
    }
    
    public static void main(String[] args) {
        new TheLastStand();
    }
}
