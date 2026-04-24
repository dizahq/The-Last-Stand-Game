package Codes;

import java.util.function.Consumer;

import javax.swing.JLayeredPane;

public class MainLayeredPane extends JLayeredPane{
    private int panelWidth, panelHeight;
    private Consumer<String> switchPanel;
    private Game game;
    
    private MainPanel main;
    private PauseMenuPanel pauseMenu;
    private ExitConfirmPanel exitConfirm;

    public MainLayeredPane(int panelWidth, int panelHeight){
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        main = new MainPanel(panelWidth, panelHeight, this);
        switchPanel = main.getSwitchPanel();
        game = main.getGame();
        pauseMenu = new PauseMenuPanel(panelWidth, panelHeight, switchPanel, game);
        exitConfirm = new ExitConfirmPanel(panelWidth, panelHeight);

        add(main, JLayeredPane.DEFAULT_LAYER);
        add(pauseMenu, JLayeredPane.MODAL_LAYER);
        add(exitConfirm, JLayeredPane.MODAL_LAYER);
    }

    public PauseMenuPanel getPauseMenu() {
        return pauseMenu;
    }
    public ExitConfirmPanel getExitConfirm() {
        return exitConfirm;
    }
}
