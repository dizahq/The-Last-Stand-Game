package Codes;

import java.awt.Toolkit;
import javax.swing.JFrame;

public class TheLastStand extends JFrame{
    private int frameWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private int frameHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    
    MainLayeredPane mainLayeredPane;
    
    public TheLastStand(){
        setBounds(0, 0, frameWidth, frameHeight);
        setUndecorated(true);
        setLocationRelativeTo(null);
        
        mainLayeredPane = new MainLayeredPane(frameWidth, frameHeight);
        add(mainLayeredPane);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Tests:
        System.out.println("Your screen size is " + frameWidth + "x" + frameHeight);
    }
    
    public static void main(String[] args) {
        new TheLastStand();
    }
}
