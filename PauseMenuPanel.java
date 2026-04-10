package TheLastStand;

import java.awt.Color;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;

public class PauseMenuPanel extends JPanel{
    private Consumer<String> switchPanel;

    private JButton backToMainMenu = new JButton("Back to Main Menu");
    private JButton play = new JButton("Play");
    
    public PauseMenuPanel(Consumer<String> switchPanel){
        this.switchPanel = switchPanel;

        int width = 418;
        int height = 216;
        int x = TheLastStand.getFrameWidth()/2 - width/2;
        int y = TheLastStand.getFrameHeight()/2 - height/2;

        setBackground(Color.GRAY);
        setBounds(x, y, width, height);
        setVisible(false);

        backToMainMenu.addActionListener(e -> {
            backToMainMenu();
        });
        play.addActionListener(e -> {
            play();
        });

        add(backToMainMenu);
        add(play);
    }

    public void backToMainMenu(){
        // write file (save progress)
        setVisible(false);
        switchPanel.accept("mainMenu");
    }
    public void play(){
        //resume thread
        setVisible(false);
    }
}
