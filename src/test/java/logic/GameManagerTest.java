package logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {

    @Test
    void testPlaceTower() {
        // Reason for testing: Verify the tower building economy system to ensure building a tower deducts the correct cost when funds are sufficient, and rejects the build if funds are insufficient.
        GameMap map = new GameMap(new int[0][0]);
        GameManager manager = new GameManager(map);
        manager.setPlayerMoney(150);
        
        Tower archer = new ArcherTower(); 
        boolean success = manager.placeTower(archer);
        
        assertTrue(success);
        assertEquals(50, manager.getPlayerMoney());
        assertEquals(1, manager.getActiveTowers().size());
        
        Tower wizard = new PoisonWizardTower(); 
        boolean fail = manager.placeTower(wizard);
        
        assertFalse(fail);
        assertEquals(50, manager.getPlayerMoney());
        assertEquals(1, manager.getActiveTowers().size());
    }

    @Test
    void testUpdateCoreLogic() {
        // Reason for testing: Simulate a full core Game Loop tick to verify the integrated systems: enemies reaching the base (reduces base health), towers attacking (enemies die), and the player earning rewards.
        int[][] grid = {{1, 1}}; 
        GameMap map = new GameMap(grid);
        map.generateWaypointsFromGrid(10); 
        
        GameManager manager = new GameManager(map);
        manager.setBaseHealth(10);
        manager.setPlayerMoney(0);
        
        Enemy slime = new SlimeEnemy();
        slime.setMaxHealth(10);
        slime.setCurrentHealth(10);
        slime.setSpeed(100); 
        slime.setRewardMoney(25);
        
        manager.spawnEnemy(slime);
        
        // Tick 1: The enemy is at the start, index updates to 1
        manager.update();
        assertEquals(1, manager.getActiveEnemies().size());
        assertEquals(1, slime.getCurrentWaypointIndex());
        
        // Tick 2: The enemy reaches waypoint 1 (end of path), is removed, and base health decreases
        manager.update();
        assertEquals(0, manager.getActiveEnemies().size());
        assertEquals(9, manager.getBaseHealth()); 
        
        // Test tower attack logic
        Enemy weakSlime = new SlimeEnemy();
        weakSlime.setCurrentHealth(10);
        weakSlime.setSpeed(0); 
        weakSlime.setRewardMoney(50);
        manager.spawnEnemy(weakSlime);
        
        Tower archer = new ArcherTower();
        archer.setDamage(15); 
        archer.setRange(100);
        archer.setX(5);
        archer.setY(5);
        archer.setCurrentCooldown(0);
        manager.getActiveTowers().add(archer); 
        
        // Tick 3: Tower kills the enemy and the player receives money
        manager.update();
        assertEquals(0, manager.getActiveEnemies().size()); 
        assertEquals(50, manager.getPlayerMoney()); 
    }
}
