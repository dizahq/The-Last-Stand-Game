package Codes;

import java.util.function.Consumer;
import javax.swing.JLayeredPane;

public class MainLayeredPane extends JLayeredPane{
    private Consumer<String> switchPanel;
    private Game game;
    
    private MainPanel main;
    private PauseMenuPanel pauseMenu;
    private ExitConfirmPanel exitConfirm;
    private GameOverPanel gameOver;

    public MainLayeredPane(int panelWidth, int panelHeight){
        main = new MainPanel(panelWidth, panelHeight, this);
        switchPanel = main.getSwitchPanel();
        game = main.getGame();

        pauseMenu = new PauseMenuPanel(panelWidth, panelHeight, switchPanel, game);
        exitConfirm = new ExitConfirmPanel(panelWidth, panelHeight);
        gameOver = new GameOverPanel(panelWidth, panelHeight, switchPanel, game);

        add(main, JLayeredPane.DEFAULT_LAYER);
        add(pauseMenu, JLayeredPane.MODAL_LAYER);
        add(exitConfirm, JLayeredPane.MODAL_LAYER);
        add(gameOver, JLayeredPane.MODAL_LAYER);
    }

    public PauseMenuPanel getPauseMenu() {
        return pauseMenu;
    }
    public ExitConfirmPanel getExitConfirm() {
        return exitConfirm;
    }
    public GameOverPanel getGameOver() {
        return gameOver;
    }
}
