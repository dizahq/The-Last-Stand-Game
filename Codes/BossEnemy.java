package Codes;

import java.util.ArrayList;
import java.util.List;

public class BossEnemy extends Enemy{
    private int maxHealth = 67;
    private int enemySpacing = 100;
    //enemy spawning conditions
    private int spawningRate = 10000;
    private long lastEnemySpawn;
    private boolean canSpawn = true;
    public BossEnemy(int panelWidth, int panelHeight){
        super(panelWidth/2, panelHeight/4, panelWidth, panelHeight);
        setSpeed(2);
        setHealth(67);
        width = width * 2;
        height = height * 2;
    }

    public List<Enemy> spawnEnemies(){
        if(canSpawn){
            lastEnemySpawn = System.currentTimeMillis();
            List<Enemy> spawnedEnemies = new ArrayList<>();

            for(int i = -enemySpacing; i <= enemySpacing; i += enemySpacing){
                for(int j = -enemySpacing; j <= enemySpacing; j+= enemySpacing){
                    if(i != 0 || j != 0){
                        spawnedEnemies.add(new BossMinionEnemies(this.getX()+i, this.getY()+j, panelWidth, panelHeight));
                    }
                }    
            }
            
            canSpawn = false;
            return spawnedEnemies;
        }

        if(!canSpawn && System.currentTimeMillis() - lastEnemySpawn > spawningRate){
            canSpawn = true;
        }

        return new ArrayList<>();
    }

    public int getMaxHealth() {
        return maxHealth;
    }
}
