package Codes;

public abstract class Powerup extends GameObject {
    private int duration;
    public Powerup(int x, int y){
        super(x, y, 50, 50);
    }
    public abstract void applyEffect(Player player);
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
}
