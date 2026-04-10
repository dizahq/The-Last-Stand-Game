package TheLastStand;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JPanel;

public class GamePanel extends JPanel{
    private GameLayeredPane gameContainer;

    private int panelWidth = TheLastStand.getFrameWidth();
    private int panelHeight = TheLastStand.getFrameHeight();

    private int currentLevel;
    
    private JButton pauseBtn = new JButton("Pause");

    public GamePanel(GameLayeredPane gameContainer){
        this.gameContainer = gameContainer;

        setBounds(0, 0, panelWidth, panelHeight);
        setBackground(Color.BLUE);

        pauseBtn.addActionListener(e -> {
            // freeze thread
            getGameContainer().getPauseMenu().setVisible(true);
        });

        add(pauseBtn);
    }
    public GameLayeredPane getGameContainer() {
        return gameContainer;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }
}
