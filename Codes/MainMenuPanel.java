package Codes;

import java.awt.Color;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MainMenuPanel extends JPanel{
    private Consumer<String> switchPanel;
    private Game game;

    private JButton newGameBtn = new JButton("New Game");
    private JButton continueBtn = new JButton("Continue");
    private JButton exitBtn = new JButton("Exit Game"); 
    
    public MainMenuPanel(Consumer<String> switchPanel, Game game){
        this.switchPanel = switchPanel;
        this.game = game;
        
        newGameBtn.addActionListener(e -> {
            newGame();
        });
        continueBtn.addActionListener(e -> {
            continueGame();
        });
        exitBtn.addActionListener(e -> {
            System.exit(0);
        });

        add(newGameBtn);
        add(continueBtn);
        add(exitBtn);

        setBackground(Color.GREEN);
        refreshButtons();

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                refreshButtons();
            }
        });
    }

    public void refreshButtons() {
        continueBtn.setEnabled(SaveManager.hasSave());
    }

    public void newGame(){
        game.setCurrentLevel(0);
        game.setLives(4);
        switchPanel.accept("game");
        game.startGameThread();
    }
    public void continueGame(){
        SaveData data = SaveManager.load();
        // File read
        if (data != null) {
            // Restore game state from the save file
            game.setCurrentLevel(data.currentLevel);
            game.setLives(data.lives);
            game.setPlayerPosition(data.playerX, data.playerY);
            System.out.println("[MainMenuPanel] Continuing from: " + data);
        } else {
            // Save file wass corrupt/missing
            System.err.println("[MainMenuPanel] No valid save found, startingnew.");
            game.setCurrentLevel(0);
            game.setLives(4);
        }

        switchPanel.accept("game");
        game.startGameThread();
    }
}
