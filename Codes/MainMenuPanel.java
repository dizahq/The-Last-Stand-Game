package Codes;

import java.awt.Color;
import java.awt.Dimension;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenuPanel extends JPanel{
    private MainLayeredPane rootLayeredPane;
    private Consumer<String> switchPanel;
    private GamePanel game;

    private JLabel title = new JLabel("The Last Stand");
    private JButton newGameBtn = new JButton("New Game");
    private JButton continueBtn = new JButton("Continue");
    private JButton exitBtn = new JButton("Exit Game"); 
    
    public MainMenuPanel(MainLayeredPane rootLayeredPane, Consumer<String> switchPanel, GamePanel game){
        this.rootLayeredPane = rootLayeredPane;
        this.switchPanel = switchPanel;
        this.game = game;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        title.setAlignmentX(CENTER_ALIGNMENT);
        newGameBtn.setAlignmentX(CENTER_ALIGNMENT);
        continueBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);

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
    }

    public void newGame(){
        game.setCurrentLevel(0);
        game.setFocusable(true);
        switchPanel.accept("game");
    }
    public void continueGame(){
        // file read
        game.setFocusable(true);
        switchPanel.accept("game");
    }
    public void exitGame(){
        rootLayeredPane.getExitConfirm().setVisible(true);
    }
}
