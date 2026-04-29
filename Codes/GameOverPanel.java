package Codes;

import java.awt.Dimension;
import java.awt.Font;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameOverPanel extends OverlayPanel {
    private JPanel container;
    private JLabel title = new JLabel("Game Over!");
    private JButton restartBtn = new JButton("Restart");
    private JButton mainMenuBtn = new JButton("Main Menu");

    private Game game;
    private Consumer<String> switchPanel;

    public GameOverPanel(int panelWidth, int panelHeight, Consumer<String> switchPanel, Game game) {
        super(panelWidth, panelHeight);
        this.switchPanel = switchPanel;
        this.game = game;

        container = getContainerPanel();

        title.setFont(new Font("Arial", Font.BOLD, 35));

        restartBtn.setPreferredSize(new Dimension(200, 50));
        mainMenuBtn.setPreferredSize(new Dimension(200, 50));

        restartBtn.addActionListener(e -> restart());

        mainMenuBtn.addActionListener(e -> goToMainMenu());

        container.add(title);
        container.add(restartBtn);
        container.add(mainMenuBtn);
    }

    public void restart() {
        SaveManager.deleteSave();
        game.resetGame();
        setVisible(false);
        switchPanel.accept("game");
        game.startGameThread();

        // test
        System.out.println("[GameOverPanel] Restarting game.");
    }

    private void goToMainMenu() {
        SaveManager.deleteSave();
        setVisible(false);
        game.stopGameThread();
        switchPanel.accept("mainMenu");

        // test
        System.out.println("[GameOverPanel] Returning to main menu.");
    }
}