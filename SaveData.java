package Codes;

import java.io.Serializable;

// container that holds all game info we want to save 

public class SaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    public int currentLevel;
    public int lives;
    public int playerX;
    public int playerY;

    public SaveData(int currentLevel, int lives, int playerX, int playerY) {
        this.currentLevel = currentLevel;
        this.lives = lives;
        this.playerX = playerX;
        this.playerY = playerY;
    }

    // Test
    @Override
    public String toString() {
        return "[SaveData] level = " + currentLevel + " | lives = " + lives + " | pos = (" + playerX + "," + playerY + ")";
    }
}