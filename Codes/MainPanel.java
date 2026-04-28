package Codes;

import java.awt.CardLayout;
import java.util.function.Consumer;

import javax.swing.JPanel;

public class MainPanel extends JPanel{
    private CardLayout cards = new CardLayout();
    private Consumer<String> switchPanel;
    
    private Game game;
    private MainMenuPanel mainMenu;
    
    public MainPanel(int panelWidth, int panelHeight, MainLayeredPane rootLayeredPane){
        switchPanel = this::switchPanel;

        setBounds(0, 0, panelWidth, panelHeight);
        setLayout(cards);
        
        game = new Game(panelWidth, panelHeight, rootLayeredPane);
        mainMenu = new MainMenuPanel(rootLayeredPane, switchPanel, game);

        add(mainMenu, "mainMenu");
        add(game, "game");

        switchPanel("mainMenu");
    }
    public void switchPanel(String name){
        cards.show(this, name);
    }

    public Consumer<String> getSwitchPanel() {
        return switchPanel;
    }
    public Game getGame() {
        return game;
    }
}

