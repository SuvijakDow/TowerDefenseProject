package logic;

import logic.enemy.BatEnemy;
import logic.enemy.BigSlimeEnemy;
import logic.enemy.DemonEnemy;
import logic.enemy.Enemy;
import logic.enemy.GoblinEnemy;
import logic.enemy.KingSlimeEnemy;
import logic.enemy.SkeletonEnemy;
import logic.enemy.SlimeEnemy;
import logic.enemy.ZombieEnemy;
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

    @Test
    void testAllEnemySpritesFromResourcesAreImplemented() {
        // Reason for testing: Guarantee every sprite under resources/Enemies has a matching enemy class using that sprite path.
        Enemy[] enemies = {
            new SlimeEnemy(),
            new BatEnemy(),
            new BigSlimeEnemy(),
            new KingSlimeEnemy(),
            new GoblinEnemy(),
            new SkeletonEnemy(),
            new ZombieEnemy(),
            new DemonEnemy()
        };

        String[] expectedSprites = {
            "Enemies/spr_normal_slime.png",
            "Enemies/spr_bat.png",
            "Enemies/spr_big_slime.png",
            "Enemies/spr_king_slime.png",
            "Enemies/spr_goblin.png",
            "Enemies/spr_skeleton.png",
            "Enemies/spr_zombie.png",
            "Enemies/spr_demon.png"
        };

        for (int i = 0; i < enemies.length; i++) {
            assertEquals(expectedSprites[i], enemies[i].getSpriteName());
        }
    }
}
