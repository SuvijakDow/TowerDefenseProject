package logic;

import logic.enemy.Enemy;
import logic.enemy.SlimeEnemy;
import logic.tower.ArcherTower;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ArcherTowerTest {

    @Test
    void testArcherTowerAttack() {
        // Reason for testing: Verify target filtering logic to ensure the tower only attacks enemies within its designated range and does not harm enemies out of range.
        ArcherTower tower = new ArcherTower();
        tower.setX(0);
        tower.setY(0);
        tower.setRange(100);
        tower.setDamage(20);
        tower.setCurrentCooldown(0); 
        
        Enemy inRange = new SlimeEnemy();
        inRange.setX(50);
        inRange.setY(0);
        inRange.setCurrentHealth(100);
        
        Enemy outOfRange = new SlimeEnemy();
        outOfRange.setX(150);
        outOfRange.setY(0);
        outOfRange.setCurrentHealth(100);
        
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(outOfRange); 
        enemies.add(inRange);
        
        tower.attack(enemies);
        
        assertEquals(100, outOfRange.getCurrentHealth()); 
        assertEquals(80, inRange.getCurrentHealth()); 
        assertEquals(tower.getAttackCooldown(), tower.getCurrentCooldown()); 
    }
}
