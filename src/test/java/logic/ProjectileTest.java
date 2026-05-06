package logic;

import logic.enemy.Enemy;
import logic.enemy.SlimeEnemy;
import logic.tower.Projectile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectileTest {

    @Test
    void updateReturnsTrueWhenTargetIsNull() {
        Projectile projectile = new Projectile(0, 0, 10, 5, null, null);

        assertTrue(projectile.update());
    }

    @Test
    void updateMovesTowardTargetWithoutOvershoot() {
        Enemy target = new SlimeEnemy();
        target.setX(20);
        target.setY(0);

        Projectile projectile = new Projectile(0, 0, 12, 5, target, null);

        boolean shouldRemove = projectile.update();

        assertFalse(shouldRemove);
        assertEquals(12.0, projectile.getX());
        assertEquals(0.0, projectile.getY());
    }

    @Test
    void updateReturnsTrueWhenAlreadyWithinHitRadius() {
        Enemy target = new SlimeEnemy();
        target.setX(3);
        target.setY(0);

        Projectile projectile = new Projectile(0, 0, 12, 5, target, null);

        assertTrue(projectile.update());
        assertEquals(0.0, projectile.getX());
        assertEquals(0.0, projectile.getY());
    }

    @Test
    void updateSnapsToTargetAndRemovesOnHit() {
        Enemy target = new SlimeEnemy();
        target.setX(10);
        target.setY(0);

        Projectile projectile = new Projectile(0, 0, 100, 5, target, null);

        assertTrue(projectile.update());
        assertEquals(10.0, projectile.getX());
        assertEquals(0.0, projectile.getY());
    }

    @Test
    void constructorUsesDefaultSpriteWhenBlankOrNull() {
        Enemy target = new SlimeEnemy();
        Projectile withNull = new Projectile(0, 0, 1, 1, target, null);
        Projectile withBlank = new Projectile(0, 0, 1, 1, target, "");

        assertEquals(Projectile.DEFAULT_SPRITE, withNull.getSpriteName());
        assertEquals(Projectile.DEFAULT_SPRITE, withBlank.getSpriteName());
    }
}
