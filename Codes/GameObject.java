package Codes;
import java.awt.Graphics;
import java.awt.Rectangle;


//It handles the common properties like position, size, and collision logic.
abstract class GameObject {
    // Protected variables so child classes can access them directly
    protected int x, y, width, height; 

    // Constructor to set the initial position and size of the object
    GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    //Returns the "Hitbox" rectangle
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    //Compares Hitboxes, not image sizes
    public boolean intersects(GameObject other) {
        return this.getBounds().intersects(other.getBounds());
    }

    //Defines how the specific object should be painted on screen.
    public abstract void draw(Graphics g);

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
}