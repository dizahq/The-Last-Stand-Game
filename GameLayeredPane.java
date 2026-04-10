package TheLastStand;

import java.util.function.Consumer;

import javax.swing.JLayeredPane;

public class GameLayeredPane extends JLayeredPane{
    private Consumer<String> switchPanel;

    private GamePanel game = new GamePanel(this);
    private PauseMenuPanel pauseMenu;

    public GameLayeredPane(Consumer<String> switchPanel){
        this.switchPanel = switchPanel;

        pauseMenu = new PauseMenuPanel(switchPanel);

        add(game, JLayeredPane.DEFAULT_LAYER);
        add(pauseMenu, JLayeredPane.MODAL_LAYER);
    }

    public Consumer<String> getSwitchPanel() {
        return switchPanel;
    }
    public GamePanel getGame() {
        return game;
    }
    public PauseMenuPanel getPauseMenu() {
        return pauseMenu;
    }
}
