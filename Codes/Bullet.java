package Codes;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Bullet extends Entity{
    private Direction direction; //direction of where the bullet must travel
    private int speed;

    //Static array for bullet images are only loaded from disk onces 
    private static Image[] bulletImages;

    public Bullet(int x, int y, Direction direction){
        super(x, y, 35, 35);
        this.direction = direction;
        this.speed = 10;

        // Load images once, observes the order based on the Direction enum
        if (bulletImages == null) {
            bulletImages = new Image[]{
                new ImageIcon("Entities/Object/ammo/arrow_up.png").getImage(),
                new ImageIcon("Entities/Object/ammo/arrow_upRight.png").getImage(),
                new ImageIcon("Entities/Object/ammo/arrow_right.png").getImage(),
                new ImageIcon("Entities/Object/ammo/arrow_downRight.png").getImage(),
                new ImageIcon("Entities/Object/ammo/arrow_down.png").getImage(),
                new ImageIcon("Entities/Object/ammo/arrow_downLeft.png").getImage(),
                new ImageIcon("Entities/Object/ammo/arrow_left.png").getImage(),
                new ImageIcon("Entities/Object/ammo/arrow_upLeft.png").getImage()
            };
        }

        //Maps directly to the correct image in the array (ordinal returns the enum position)
        currentImage = bulletImages[direction.ordinal()];
    }

    //Moves the bullet each game tick based on its direction
    public void update(){
        switch (direction) {
            case NORTH:
                this.y -= speed;
                break;
            case NORTHEAST:
                this.x += speed;
                this.y -= speed;
                break;
            case EAST:
                this.x += speed;
                break;
            case SOUTHEAST:
                this.x += speed;
                this.y += speed;
                break;
            case SOUTH:
                this.y += speed;
                break;
            case SOUTHWEST:
                this.x -= speed;
                this.y += speed;
                break;
            case WEST:
                this.x -= speed;
                break;
            case NORTHWEST:
                this.x -= speed;
                this.y -= speed;
                break;
            default:
                break;
        }
    }

    @Override
    public void draw(Graphics g){
         // Draw sprite if loaded, otherwise fall back to a plain oval
        if (currentImage != null && currentImage.getWidth(null) != -1){
            g.drawImage(currentImage, x, y, width, height, null);
        }else{
            g.fillOval(x, y, width, height);
        }
    }
}
