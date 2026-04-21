package Codes;

import java.util.function.Consumer;
import javax.swing.JLayeredPane;

public class GameLayeredPane extends JLayeredPane{
    private Consumer<String> switchPanel;

    private Game game;
    private PauseMenuPanel pauseMenu;

    public GameLayeredPane(Consumer<String> switchPanel){
        this.switchPanel = switchPanel;

        game = new Game(this);
        pauseMenu = new PauseMenuPanel(switchPanel, this);

        add(game, JLayeredPane.DEFAULT_LAYER);
        add(pauseMenu, JLayeredPane.MODAL_LAYER);
    }

    public Consumer<String> getSwitchPanel() {
        return switchPanel;
    }
    public Game getGame() {
        return game;
    }
    public PauseMenuPanel getPauseMenu() {
        return pauseMenu;
    }
}
