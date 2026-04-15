package Codes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PauseMenuPanel extends JPanel{
    private Consumer<String> switchPanel;
    JLabel title = new JLabel("Game Paused");
    private JButton backToMainMenu = new JButton("Back to Main Menu");
    private JButton resume = new JButton("Resume");
    private JButton exit = new JButton("Exit");
    
    public PauseMenuPanel(Consumer<String> switchPanel){
        this.switchPanel = switchPanel;

        title = new JLabel("Game Paused");
        // Parameters: Name, Style, Size
        title.setFont(new Font("Arial", Font.BOLD, 24));

        int width = TheLastStand.getFrameWidth()/4;
        int height = TheLastStand.getFrameHeight()/3;
        int x = (TheLastStand.getFrameWidth() - width) / 2;
        int y = (TheLastStand.getFrameHeight() - height) / 2;

        setBackground(Color.GRAY);
        setBounds(x, y, width, height);
        setVisible(false);

        backToMainMenu.setPreferredSize(new Dimension(150, 30));
        resume.setPreferredSize(new Dimension(150, 30));
        exit.setPreferredSize(new Dimension(150, 30));

        backToMainMenu.addActionListener(e -> {
            backToMainMenu();
        });
        resume.addActionListener(e -> {
            resume();
        });
        exit.addActionListener(e ->{
            System.exit(0);
        });

        add(title);
        add(backToMainMenu);
        add(resume);
        add(exit);
    }

    public void backToMainMenu(){
        // write file (save progress)
        setVisible(false);
        switchPanel.accept("mainMenu");
    }
    public void resume(){
        //resume thread
        setVisible(false);
    }
}
