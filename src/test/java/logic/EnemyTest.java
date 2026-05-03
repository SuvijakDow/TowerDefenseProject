package logic;

import logic.enemy.BatEnemy;
import logic.enemy.Enemy;
import logic.enemy.SlimeEnemy;
import logic.map.Waypoint;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {

    @Test
    void testSlimeEnemyMove() {
        // Reason for testing: Verify the movement of a ground enemy (SlimeEnemy) to ensure its X, Y coordinates update towards the target (Waypoint) based on its speed without overshooting.
        Enemy slime = new SlimeEnemy();
        slime.setX(0);
        slime.setY(0);
        slime.setSpeed(5.0);
        
        Waypoint target = new Waypoint(10, 0);
        slime.move(target);
        
        assertEquals(5.0, slime.getX());
        assertEquals(0.0, slime.getY());
    }

    @Test
    void testBatEnemyMove() {
        // Reason for testing: Verify the flight of the BatEnemy to ensure it moves accurately diagonally towards its destination using Pythagorean theorem calculations.
        Enemy bat = new BatEnemy();
        bat.setX(0);
        bat.setY(0);
        bat.setSpeed(5.0);
        
        Waypoint target = new Waypoint(3, 4);
        bat.move(target); 
        
        assertEquals(3.0, bat.getX());
        assertEquals(4.0, bat.getY());
    }

    @Test
    void testTakeDamage() {
        // Reason for testing: Verify the enemy's damage receiving system to ensure currentHealth is reduced correctly and prevents negative health bugs when damage exceeds remaining health.
        Enemy slime = new SlimeEnemy();
        slime.setMaxHealth(100);
        slime.setCurrentHealth(100);
        
        slime.takeDamage(40);
        assertEquals(60, slime.getCurrentHealth());
        
        slime.takeDamage(100);
        assertEquals(0, slime.getCurrentHealth());
        assertTrue(slime.isDead());
    }
}
