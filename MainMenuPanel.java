package Codes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenuPanel extends JPanel{
    private MainLayeredPane rootLayeredPane;
    private Consumer<String> switchPanel;
    private Game game;

    private JLabel title = new JLabel("The Last Stand");
    private JButton newGameBtn = new JButton("New Game");
    private JButton continueBtn = new JButton("Continue");
    private JButton exitBtn = new JButton("Exit Game");  
    
    public MainMenuPanel(MainLayeredPane rootLayeredPane, Consumer<String> switchPanel, Game game){
        this.rootLayeredPane = rootLayeredPane;
        this.switchPanel = switchPanel;
        this.game = game;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        title.setAlignmentX(CENTER_ALIGNMENT);
        newGameBtn.setAlignmentX(CENTER_ALIGNMENT);
        continueBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);

        title.setFont(new Font("Arial", Font.BOLD, 35));
        newGameBtn.setPreferredSize(new Dimension(200, 50));
        continueBtn.setPreferredSize(new Dimension(200, 50));
        exitBtn.setPreferredSize(new Dimension(200, 50));
        

        newGameBtn.addActionListener(e -> {
            newGame();
        });
        continueBtn.addActionListener(e -> {
            continueGame();
        });
        exitBtn.addActionListener(e -> {
            exitGame();
        });

        add(Box.createVerticalGlue());
        add(title);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(newGameBtn);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(continueBtn);
        add(Box.createRigidArea(new Dimension(0, 25)));
        add(exitBtn);
        add(Box.createVerticalGlue());

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
    public void exitGame(){
        rootLayeredPane.getExitConfirm().setVisible(true);
    }
}
