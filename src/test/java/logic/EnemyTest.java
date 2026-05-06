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
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {

    @Test
    void moveDoesNotOvershootTarget() {
        Enemy slime = new SlimeEnemy();
        slime.setX(0);
        slime.setY(0);
        slime.setSpeed(50.0);

        Waypoint target = new Waypoint(10.0, 0.0);
        slime.move(target);

        assertEquals(10.0, slime.getX());
        assertEquals(0.0, slime.getY());
    }

    @Test
    void batMovesDiagonallyTowardsWaypoint() {
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
    void updateFollowsWaypointsAndStopsAtPathEnd() {
        Enemy slime = new SlimeEnemy();
        slime.setX(0);
        slime.setY(0);
        slime.setSpeed(5.0);

        List<Waypoint> waypoints = List.of(
                new Waypoint(5.0, 0.0),
                new Waypoint(10.0, 0.0)
        );

        slime.update(waypoints);
        assertEquals(5.0, slime.getX());
        assertEquals(1, slime.getCurrentWaypointIndex());

        slime.update(waypoints);
        assertEquals(10.0, slime.getX());
        assertEquals(2, slime.getCurrentWaypointIndex());

        slime.update(waypoints);
        assertEquals(10.0, slime.getX());
        assertEquals(2, slime.getCurrentWaypointIndex());
    }

    @Test
    void takeDamageIgnoresNonPositiveAndClampsToZero() {
        Enemy slime = new SlimeEnemy();
        slime.setCurrentHealth(100);

        slime.takeDamage(0);
        slime.takeDamage(-5);
        assertEquals(100, slime.getCurrentHealth());

        slime.takeDamage(40);
        assertEquals(60, slime.getCurrentHealth());

        slime.takeDamage(100);
        assertEquals(0, slime.getCurrentHealth());
        assertTrue(slime.isDead());
    }

    @Test
    void takeDamageMarksEnemyAsHitThenHitFlashResets() {
        Enemy slime = new SlimeEnemy();
        slime.setCurrentHealth(100);
        slime.takeDamage(1);

        assertTrue(slime.isHit());

        for (int i = 0; i < 6; i++) {
            slime.update(null);
        }

        assertFalse(slime.isHit());
    }

    @Test
    void updateAdvancesAnimationFrames() {
        Enemy slime = new SlimeEnemy();
        assertEquals(0, slime.getCurrentFrame());

        for (int i = 0; i < 11; i++) {
            slime.update(List.of());
        }

        assertEquals(1, slime.getCurrentFrame());
        assertEquals(0, slime.getAnimTick());
    }

    @Test
    void setCurrentFrameNormalizesNegativeValues() {
        Enemy slime = new SlimeEnemy();

        slime.setCurrentFrame(-1);
        assertEquals(3, slime.getCurrentFrame());

        slime.setCurrentFrame(4);
        assertEquals(0, slime.getCurrentFrame());
    }

    @Test
    void enemyVariantsHaveExpectedStatsAndSprites() {
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

        int[] expectedMaxHealth = {100, 50, 180, 320, 90, 130, 170, 250};
        double[] expectedSpeed = {1.0, 2.5, 0.8, 2.0, 1.8, 1.2, 0.9, 1.5};
        int[] expectedRewardMoney = {10, 15, 20, 45, 18, 16, 22, 35};
        int[] expectedDamage = {1, 1, 2, 50, 2, 3, 4, 5};
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
            assertEquals(expectedMaxHealth[i], enemies[i].getMaxHealth());
            assertEquals(expectedMaxHealth[i], enemies[i].getCurrentHealth());
            assertEquals(expectedSpeed[i], enemies[i].getSpeed());
            assertEquals(expectedRewardMoney[i], enemies[i].getRewardMoney());
            assertEquals(expectedDamage[i], enemies[i].getDamage());
            assertEquals(expectedSprites[i], enemies[i].getSpriteName());
        }
    }
}
