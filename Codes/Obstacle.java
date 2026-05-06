package Codes;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

//Represents a static obstacle that the player must avoid.
public class Obstacle extends GameObject {
    private Image obstacle1; // Optional: If you want to use an image instead of a solid color  
    
    // Constructor passing coordinates and dimensions to the GameObject parent.
    Obstacle(int x, int y, int width, int height, int panelWidth, int panelHeight) {
        super(x, y, width, height);

        //image = new ImageIcon("Entities/Object/rock1.png").getImage();
        
        // Optional: Safety clamp to ensure wall doesn't exceed screen bounds 
        // using the dynamic frame sizes from your main class.
        int maxX = panelWidth;
        int maxY = panelHeight;
        if (this.x + this.width > maxX) this.width = maxX - this.x;
        if (this.y + this.height > maxY) this.height = maxY - this.y;


        obstacle1 = new ImageIcon("Entities/Obstacles/obs1.PNG").getImage();

    }

    //Mainly used for collision detection, so we define a smaller hitbox that better matches the visual representation of the wall.
    @Override
    public Rectangle getBounds() {
        // Tight oval-ish box that hugs the visible rock, ignoring transparent padding
        return new Rectangle(x, y, width, height);
    }

    // Renders the wall as a Red rectangle on the screen.
    @Override
    public void draw(Graphics g) {
       // 2. Draw the sprite if it loaded correctly
        if (obstacle1 != null && obstacle1.getWidth(null) != -1) {
            // Draws the image scaled to the width and height of the GameObject
            g.drawImage(obstacle1, x, y, width, height, null);
        } else {
            // Fallback: If the image is missing, draw the red box so the game doesn't break
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
            
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
        }

        Rectangle b = getBounds();
g.setColor(new java.awt.Color(255, 0, 0, 140));
g.drawRect(b.x, b.y, b.width, b.height);
    }
}