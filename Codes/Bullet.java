package Codes;

import java.awt.Graphics;

public class Bullet extends GameObject{
    private Direction direction;
    private int speed;
    public Bullet(int x, int y, Direction direction){
        super(x, y, 10, 10);
        this.direction = direction;
        this.speed = -10;
    }
    public void update(){
        switch (direction) {
            case NORTH:
                this.y += speed;
                break;
            case NORTHEAST:
                this.x += speed;
                this.y += speed;
                break;
            case EAST:
                this.x += speed;
                break;
            case SOUTHEAST:
                this.x += speed;
                this.y -= speed;
                break;
            case SOUTH:
                this.y -= speed;
                break;
            case SOUTHWEST:
                this.x -= speed;
                this.y -= speed;
                break;
            case WEST:
                this.x -= speed;
                break;
            case NORTHWEST:
                this.x -= speed;
                this.y += speed;
                break;
            default:
                break;
        }
    }
    @Override
    public void draw(Graphics g){
        g.fillOval(x, y, width, height);
    }
}
