package Codes;

import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.JPanel;

public class MainPanel extends JPanel{
    private CardLayout cards = new CardLayout();
    
    private GameLayeredPane gameContainer = new GameLayeredPane(this::switchPanel);
    private MainMenuPanel mainMenu = new MainMenuPanel(this::switchPanel, gameContainer.getGame());
    
    public MainPanel(){
        setBackground(Color.RED);
        setLayout(cards);
        
        add(mainMenu, "mainMenu");
        add(gameContainer, "game");

        switchPanel("mainMenu");
    }
    public void switchPanel(String name){
        cards.show(this, name);
    }
}

