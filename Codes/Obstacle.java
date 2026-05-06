package Codes;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

//Represents a static obstacle that the player must avoid.
public class Obstacle extends GameObject {
    private Image obstacle1; // Optional: If you want to use an image instead of a solid color  
    private Image obstacle2;
    private Image obstacle3;
    private Image obstacle4;
    private Image currentImage;
    private int type;
    
    // Constructor passing coordinates and dimensions to the GameObject parent.
    Obstacle(int x, int y, int width, int height, int panelWidth, int panelHeight, int type) {
        super(x, y, width, height);

        //image = new ImageIcon("Entities/Object/rock1.png").getImage();
        
        // Optional: Safety clamp to ensure wall doesn't exceed screen bounds 
        // using the dynamic frame sizes from your main class.
        int maxX = panelWidth;
        int maxY = panelHeight;
        if (this.x + this.width > maxX) this.width = maxX - this.x;
        if (this.y + this.height > maxY) this.height = maxY - this.y;


        obstacle1 = new ImageIcon("Entities/Obstacles/obs1.PNG").getImage();
        obstacle2 = new ImageIcon("Entities/Obstacles/obs2.PNG").getImage();
        obstacle3 = new ImageIcon("Entities/Obstacles/obs3.PNG").getImage();
        obstacle4 = new ImageIcon("Entities/Obstacles/obs4.PNG").getImage();

        // Pick image based on type
        switch (type) {
            case 1: currentImage = obstacle1; break;
            case 2: currentImage = obstacle2; break;
            case 3: currentImage = obstacle3; break;
            case 4: currentImage = obstacle4; break;
            default: currentImage = obstacle1; break;
        }

    }

    //Mainly used for collision detection, so we define a smaller hitbox that better matches the visual representation of the wall.
    @Override
    public Rectangle getBounds() {
        int collisionHeight = 20; // how tall the bottom collision strip is
        return new Rectangle(x, y + height - collisionHeight, width, collisionHeight);
    }

    // Renders the wall as a Red rectangle on the screen.
    @Override
    public void draw(Graphics g) {
       // 2. Draw the sprite if it loaded correctly
        if (currentImage != null && currentImage.getWidth(null) != -1) {
            // Draws the image scaled to the width and height of the GameObject
            g.drawImage(currentImage, x, y, width, height, null);
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