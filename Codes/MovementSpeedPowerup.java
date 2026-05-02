package Codes;

import java.awt.Color;
import java.awt.Graphics;

public class MovementSpeedPowerup extends Powerup{
    public MovementSpeedPowerup(int x, int y){
        super(x, y);
        setDuration(10000);
    }
    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillOval(x, y, width, height);
    }
    @Override
    public void applyEffect(Player player) {
        player.setSpeed(5);
    }
}
