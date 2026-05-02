package Codes;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public abstract class Entity extends GameObject{

    protected int movementSpeed;
    protected int damage;
    protected int fireRate;
    
    protected Image currentImage;
    protected int frameIndex = 0;
    protected int animationTick = 0;

    public Entity(int xPosition, int yPosition, int width, int height){
        super (xPosition, yPosition, width, height);
    }

    //Avoids manual loading ot sprites to the memory
    protected Image[] loadStrip(String prefix, int count){
        Image[] frames = new Image[count];

        for (int i = 0; i< count; i++){
            frames[i] = new ImageIcon(prefix + (i + 1) + ".png").getImage();
        }
        return frames;
    }

    protected void updateAnimation(Image[] frames, int speed){
        animationTick++;

        if (animationTick >= speed){
            animationTick = 0;
            frameIndex = (frameIndex + 1) % frames.length;
        }
        currentImage = frames [frameIndex];
    }

    @Override
    public abstract void draw(Graphics g);
    
}
