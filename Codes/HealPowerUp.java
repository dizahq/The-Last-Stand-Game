package Codes;

import java.awt.Color;
import java.awt.Graphics;

public class HealPowerUp extends Powerup{
    public HealPowerUp(int x, int y){
        super(x, y);
        setDuration(0);
    }
    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillOval(x, y, width, height);
    }
    @Override
    public void applyEffect(Player player) {
        if(player.getCurrentLives() < player.getMaxLives()){
            player.addLife();
        }
        
    }
}
