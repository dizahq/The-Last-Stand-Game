package Codes;
import java.awt.Graphics;


//It handles the common properties like position, size, and collision logic.
abstract class GameObject {
    // Protected variables so child classes (Player, Wall) can access them directly
    protected int x, y, width, height; 

    // Constructor to set the initial position and size of the object
    GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    //Checks if this object's rectangle overlaps with another GameObject's rectangle. 
    public boolean intersects(GameObject other) {
        // Logic: If any of these conditions are true, the objects are NOT colliding.
        return x < other.x + other.width &&   // Is this object's left side to the left of other's right?
               x + width > other.x &&         // Is this object's right side to the right of other's left?
               y < other.y + other.height &&  // Is this object's top above other's bottom?
               y + height > other.y;          // Is this object's bottom below other's top?
    }

    //Defines how the specific object should be painted on screen.
    public abstract void draw(Graphics g);
}