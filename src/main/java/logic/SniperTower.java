package logic;

import java.util.List;

// High-damage single-target tower.
public class SniperTower extends Tower {
    private static final int DEFAULT_DAMAGE = 140;
    private static final double DEFAULT_RANGE = 12.0;
    private static final int DEFAULT_COOLDOWN = 2;

    // Creates a sniper tower with default stats.
    public SniperTower(double x, double y) {
        super(DEFAULT_DAMAGE, DEFAULT_RANGE, DEFAULT_COOLDOWN, x, y);
    }

    // Creates a sniper tower with custom stats.
    public SniperTower(int damage, double range, int attackCooldown, double x, double y) {
        super(damage, range, attackCooldown, x, y);
    }

    // Attacks the first alive enemy found within range.
    @Override
    public void attack(List<Enemy> targets) {
        if (targets == null) {
            throw new IllegalArgumentException("targets must not be null.");
        }

        for (Enemy enemy : targets) {
            if (enemy == null || !enemy.isAlive()) {
                continue;
            }
            if (distanceTo(enemy) <= range) {
                enemy.takeDamage(damage);
                return;
            }
        }
    }

    // Calculates distance from this tower to an enemy.
    private double distanceTo(Enemy enemy) {
        return Math.hypot(enemy.getX() - x, enemy.getY() - y);
    }
}
