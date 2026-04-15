package Codes;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

//Represents a static obstacle that the player must avoid.
public class Obstacle extends GameObject {
    private Image image; // Optional: If you want to use an image instead of a solid color  
    
    // Constructor passing coordinates and dimensions to the GameObject parent.
    Obstacle(int x, int y, int width, int height) {
        super(x, y, width, height);

        image = new ImageIcon("C:\\Users\\dizah\\Documents\\GitHub\\The-Last-Stand-Game\\Entities\\rock1.png").getImage();
        
        // Optional: Safety clamp to ensure wall doesn't exceed screen bounds 
        // using the dynamic frame sizes from your main class.
        int maxX = TheLastStand.getFrameWidth();
        int maxY = TheLastStand.getFrameHeight();
        if (this.x + this.width > maxX) this.width = maxX - this.x;
        if (this.y + this.height > maxY) this.height = maxY - this.y;
    }

    // Renders the wall as a Red rectangle on the screen.
    @Override
    public void draw(Graphics g) {
       // 2. Draw the sprite if it loaded correctly
        if (image != null && image.getWidth(null) != -1) {
            // Draws the image scaled to the width and height of the GameObject
            g.drawImage(image, x, y, width, height, null);
        } else {
            // Fallback: If the image is missing, draw the red box so the game doesn't break
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
            
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
        }
    }
}