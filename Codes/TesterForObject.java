package Codes;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TesterForObject extends JFrame {
    private Player player;
    private Wall obstacle;

    TesterForObject() {
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(){
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                player.draw(g);
                obstacle.draw(g);
            }
        };
    

        player = new Player(300, 100, obstacle, panel);
        
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // We send the event to the player class to handle it
                player.handleKeyPress(e, obstacle);
                panel.repaint();
            }
        });
        
        obstacle = new Wall(200, 200, 100, 100);
    

        add(panel);
        setFocusable(true);
        requestFocus();
        setVisible(true);
    }

    public static void main(String[] args) {
        new TesterForObject();
    }
}