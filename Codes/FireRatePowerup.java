package Codes;

import java.awt.Color;
import java.awt.Graphics;

public class FireRatePowerup extends Powerup{
    public FireRatePowerup(int x, int y){
        super(x, y);
        setDuration(5000);
    }
    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, width, height);
    }
    @Override
    public void applyEffect(Player player) {
        player.setFireRate(200);
    }
}
