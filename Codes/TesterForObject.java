package Codes;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

//The main entry point and "Game Engine" for the application.
public class TesterForObject extends JFrame {
    private Player player;
    private Wall obstacle;

    TesterForObject() {
        // --- Window Setup ---
        setSize(500, 500);               // Sets the window dimensions to 500x500 pixels
        setLocationRelativeTo(null);     // Centers the window on the screen
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Ensures the program stops when the window is closed

        // --- Game Rendering (The Canvas) ---
        // Create an anonymous JPanel subclass to handle custom drawing
        JPanel panel = new JPanel(){
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g); // Clears the screen before drawing
                
                // Draw all game entities
                player.draw(g);   // Renders the blue player
                obstacle.draw(g); // Renders the red wall
            }
        };
    
        // --- Object Initialization ---
        // Create the obstacle first so it can be passed to the player logic if needed
        obstacle = new Wall(200, 200, 100, 100);
        // Create the player at coordinates (300, 100)
        player = new Player(300, 100, obstacle, panel);

        // --- Input Handling ---
        // Listen for key presses directly on the JFrame
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Pass the keyboard event and the obstacle to the player's movement logic
                player.handleKeyPress(e, obstacle);
                
                // CRITICAL: Request the panel to redraw itself after the player moves
                // Without this, the player would move in the code but not on the screen
                panel.repaint();
            }
        });

        // Add the custom panel to the frame and make it visible
        add(panel);
        setVisible(true);
    }

    //Main method to launch the application.
    public static void main(String[] args) {
        // Instantiate the tester to start the game
        new TesterForObject();
    }
}