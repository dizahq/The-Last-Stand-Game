package Codes;

import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PauseMenuPanel extends OverlayPanel{
    private Consumer<String> switchPanel;
    private GamePanel game;

    private JPanel container;
    private JLabel title = new JLabel("Game Paused");
    private JButton backToMainMenuBtn = new JButton("Back to Main Menu");
    private JButton resumeBtn = new JButton("Resume");
    
    public PauseMenuPanel(int panelWidth, int panelHeight, Consumer<String> switchPanel, GamePanel game){
        super(panelWidth, panelHeight);
        this.switchPanel = switchPanel;
        this.game = game;
        container = getContainerPanel();

        // Parameters: Name, Style, Size
        title.setFont(new Font("Arial", Font.BOLD, 24));
        backToMainMenuBtn.setPreferredSize(new Dimension(150, 30));
        resumeBtn.setPreferredSize(new Dimension(150, 30));

        backToMainMenuBtn.addActionListener(e -> {
            backToMainMenu();
        });
        resumeBtn.addActionListener(e -> {
            resume();
        });

        container.add(title);
        container.add(backToMainMenuBtn);
        container.add(resumeBtn);
    }

    public void backToMainMenu(){
        // write file (save progress)
        setVisible(false);
        switchPanel.accept("mainMenu");
    }
    public void resume(){
        //resume thread
        game.setFocusable(true);
        setVisible(false);
    }
}
