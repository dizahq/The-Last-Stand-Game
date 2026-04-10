package TheLastStand;

import java.awt.Color;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MainMenuPanel extends JPanel{
    private Consumer<String> switchPanel;
    private GamePanel game;

    private JButton newGameBtn = new JButton("New Game");
    private JButton continueBtn = new JButton("Continue");
    private JButton exitBtn = new JButton("Exit Game"); 
    
    public MainMenuPanel(Consumer<String> switchPanel, GamePanel game){
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
    }

    public void newGame(){
        game.setCurrentLevel(0);
        switchPanel.accept("game");
    }
    public void continueGame(){
        // file read
        switchPanel.accept("game");
    }
}
