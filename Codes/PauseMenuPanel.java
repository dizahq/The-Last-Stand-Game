package Codes;

import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PauseMenuPanel extends OverlayPanel{
    private Consumer<String> switchPanel;
    private Game game;

    private JPanel container = getContainerPanel();
    private JLabel title = new JLabel("Game Paused");
    private JButton backToMainMenu = new JButton("Back to Main Menu");
    private JButton resume = new JButton("Resume");
    private JButton exit = new JButton("Exit");
    
    public PauseMenuPanel(int panelWidth, int panelHeight, Consumer<String> switchPanel, Game game){
        super(panelWidth, panelHeight);
        this.switchPanel = switchPanel;
        this.game = game;

        title.setFont(new Font("Arial", Font.BOLD, 35));

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

        container.add(title);
        container.add(backToMainMenu);
        container.add(resume);
        container.add(exit);
    }

    public void resume(){
        setVisible(false);
        game.resumeGameThread();
    }

    public void backToMainMenu(){
        // Savee game progress
        SaveData data = new SaveData(game.getCurrentLevel(), game.getLives(), game.getPlayerX(), game.getPlayerY());
        boolean saved = SaveManager.save(data);
        if (!saved) {
            System.err.println("[PauseMenuPanel] Warning: progress can't be saved.");
        }

        setVisible(false);
        game.stopGameThread();
        switchPanel.accept("mainMenu");
    }

    public void exitGame() {
        SaveData data = new SaveData(game.getCurrentLevel(), game.getLives(), game.getPlayerX(), game.getPlayerY());
        SaveManager.save(data);

        game.stopGameThread();
        System.exit(0);
    }
}
