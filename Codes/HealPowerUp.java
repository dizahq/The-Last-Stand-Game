package Codes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class HealPowerUp extends Powerup{
    private Image icon;

    public HealPowerUp(int x, int y){
        super(x, y);
        setDuration(0);

        icon = new ImageIcon("Entities/Object/PowerUps/heal.png").getImage();
    }
    @Override
    public void draw(Graphics g) {
        if (icon != null && icon.getWidth(null) != 1){
            g.drawImage(icon, x, y, width, height, null);
        }else{
            //if image fails to load
            g.setColor(Color.GREEN);
            g.fillOval(x, y, width, height);
        }
        
    }
    @Override
    public void applyEffect(Player player) {
        if(player.getCurrentLives() < player.getMaxLives()){
            player.addLife();
        }
        
    }
}
