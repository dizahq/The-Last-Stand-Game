package Codes;
import java.awt.Color;
import java.awt.Graphics;

//Represents a static obstacle that the player must avoid.
public class Wall extends GameObject {
    
    // Constructor passing coordinates and dimensions to the GameObject parent
    Wall(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    //Renders the wall as a Red rectangle on the screen.
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }
}