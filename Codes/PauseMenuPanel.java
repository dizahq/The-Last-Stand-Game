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
    private GameLayeredPane gameContainer;

    JLabel title = new JLabel("Game Paused");
    private JButton backToMainMenu = new JButton("Back to Main Menu");
    private JButton resume = new JButton("Resume");
    private JButton exit = new JButton("Exit");
    
    public PauseMenuPanel(Consumer<String> switchPanel, GameLayeredPane gameContainer){
        this.switchPanel = switchPanel;
        this.gameContainer = gameContainer;

        title = new JLabel("Game Paused");
        title.setFont(new Font("Arial", Font.BOLD, 35));

        int width = TheLastStand.getFrameWidth()/5;
        int height = TheLastStand.getFrameHeight()/4;
        int x = (TheLastStand.getFrameWidth() - width) / 2;
        int y = (TheLastStand.getFrameHeight() - height) / 2;

        setBackground(Color.GRAY);
        setBounds(x, y, width, height);
        setVisible(false);

        backToMainMenu.setPreferredSize(new Dimension(200, 50));
        resume.setPreferredSize(new Dimension(200, 50));
        exit.setPreferredSize(new Dimension(200, 50));

        backToMainMenu.addActionListener(e -> {
            backToMainMenu();
        });
        resume.addActionListener(e -> {
            resume();
        });
        exit.addActionListener(e ->{
            exitGame();
        });

        add(title);
        add(backToMainMenu);
        add(resume);
        add(exit);
    }

    public void resume(){
        setVisible(false);
        gameContainer.getGame().resumeGameThread();
    }

    public void backToMainMenu(){
        // write file (save progress)
        setVisible(false);
        gameContainer.getGame().stopGameThread();
        switchPanel.accept("mainMenu");
    }

    public void exitGame() {
        gameContainer.getGame().stopGameThread();
        System.exit(0);
    }
}
