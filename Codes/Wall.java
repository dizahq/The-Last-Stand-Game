package Codes;
import java.awt.Color;
import java.awt.Graphics;

public class Wall extends GameObject {
    Wall(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }
    
}
