package Codes;

import java.awt.Graphics;
import java.awt.Image;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;

public class Bullet extends Entity{
    private Direction direction; //direction of where the bullet must travel
    private int speed;
    private int diagonal;

    //Static array for bullet images are only loaded from disk onces 
    private static Image[] bulletImages;

    public Bullet(int x, int y, Direction direction){
        super(x, y, 35, 35);
        this.direction = direction;
        this.speed = 10;
        this.diagonal = (int) (speed / Math.sqrt(2));

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

        currentImage = bulletImages[direction.ordinal()];
    }

    //Moves the bullet each game tick based on its direction
    public void update(List<Enemy> enemies, List<Bullet> bulletsToRemove, List<Enemy> enemiesToRemove, Powerup activePowerup, Game game){
        switch (direction) {
            case NORTH:
                this.y -= speed;
                break;
            case NORTHEAST:
                this.x += diagonal;
                this.y -= diagonal;
                break;
            case EAST:
                this.x += speed;
                break;
            case SOUTHEAST:
                this.x += diagonal;
                this.y += diagonal;
                break;
            case SOUTH:
                this.y += speed;
                break;
            case SOUTHWEST:
                this.x -= diagonal;
                this.y += diagonal;
                break;
            case WEST:
                this.x -= speed;
                break;
            case NORTHWEST:
                this.x -= diagonal;
                this.y -= diagonal;
                break;
            default:
            break;
        }

        // Bullet vs enemy collision
        Random powerupRandom = new Random(); // For random powerup drops
        for (Enemy enemy : enemies) {
            if (!enemiesToRemove.contains(enemy) && this.getBounds().intersects(enemy.getBounds())) {
                enemy.deductHealth();
                bulletsToRemove.add(this);
                if(enemy.getHealth() == 0){
                    enemiesToRemove.add(enemy);
                }
                System.out.println("[Game] Enemy hit by bullet!");
                // Powerup drop
                if(powerupRandom.nextInt(10) == 0 && activePowerup == null){
                    int powerup = powerupRandom.nextInt(3)+1;
                    switch (powerup) {
                        case 1:
                            game.setActivePowerup(new FireRatePowerup(enemy.getX(), enemy.getY()));
                            break;
                        case 2:
                            game.setActivePowerup(new MovementSpeedPowerup(enemy.getX(), enemy.getY()));
                            break;
                        case 3:
                            game.setActivePowerup(new HealPowerUp(enemy.getX(), enemy.getY()));
                            break;
                        default:
                            break;
                    }
                }
                break;
            }
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
