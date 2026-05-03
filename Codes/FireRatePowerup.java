package Codes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class FireRatePowerup extends Powerup{
    private Image icon;

    public FireRatePowerup(int x, int y){
        super(x, y);
        setDuration(5000);

        icon = new ImageIcon("Entities/Object/PowerUps/atkspeed.png").getImage();
    }
    @Override
    public void draw(Graphics g) {
        if (icon != null && icon.getWidth(null) != 1){
            g.drawImage(icon, x, y, width, height, null);
        }else{
            //if image fails to load
            g.setColor(Color.RED);
            g.fillOval(x, y, width, height);
        }
    }
    @Override
    public void applyEffect(Player player) {
        player.setFireRate(200);
    }
}
