package Codes;

import java.io.Serializable;

// container that holds all game info we want to save 

public class SaveData implements Serializable {
    private static final long serialVersionUID = 2L;

    public int currentLevel;
    public int currentWave; 
    public int lives;
    public int playerX;
    public int playerY;

    public SaveData(int currentLevel, int currentWave, int lives, int playerX, int playerY) {
        this.currentLevel = currentLevel;
        this.currentWave = currentWave;
        this.lives = lives;
        this.playerX = playerX;
        this.playerY = playerY;
    }

    // Test
    @Override
    public String toString() {
        return "[SaveData] level = " + currentLevel + " | wave = " + currentWave + " | lives + " + lives + "| pos = (" + playerX + "," + playerY + ")";
    }
}