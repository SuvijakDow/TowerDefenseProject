package logic;

import java.util.List;

// Splash-damage tower that damages enemies around a primary target.
public class BombTower extends Tower {
    private static final int DEFAULT_DAMAGE = 80;
    private static final double DEFAULT_RANGE = 7.0;
    private static final int DEFAULT_COOLDOWN = 3;
    private static final double DEFAULT_SPLASH_RADIUS = 2.0;

    private double splashRadius;

    // Creates a bomb tower with default stats.
    public BombTower(double x, double y) {
        this(DEFAULT_DAMAGE, DEFAULT_RANGE, DEFAULT_COOLDOWN, DEFAULT_SPLASH_RADIUS, x, y);
    }

    // Creates a bomb tower with custom stats.
    public BombTower(int damage, double range, int attackCooldown, double splashRadius, double x, double y) {
        super(damage, range, attackCooldown, x, y);
        setSplashRadius(splashRadius);
    }

    // Returns splash radius.
    public double getSplashRadius() {
        return splashRadius;
    }

    // Sets splash radius.
    public void setSplashRadius(double splashRadius) {
        if (splashRadius <= 0.0) {
            throw new IllegalArgumentException("splashRadius must be > 0.");
        }
        this.splashRadius = splashRadius;
    }

    // Attacks one in-range target, then deals splash damage around that target.
    @Override
    public void attack(List<Enemy> targets) {
        if (targets == null) {
            throw new IllegalArgumentException("targets must not be null.");
        }

        Enemy primaryTarget = null;
        for (Enemy enemy : targets) {
            if (enemy == null || !enemy.isAlive()) {
                continue;
            }
            if (distance(x, y, enemy.getX(), enemy.getY()) <= range) {
                primaryTarget = enemy;
                break;
            }
        }

        if (primaryTarget == null) {
            return;
        }

        for (Enemy enemy : targets) {
            if (enemy == null || !enemy.isAlive()) {
                continue;
            }
            if (distance(primaryTarget.getX(), primaryTarget.getY(), enemy.getX(), enemy.getY()) <= splashRadius) {
                enemy.takeDamage(damage);
            }
        }
    }

    // Calculates Euclidean distance between two points.
    private static double distance(double x1, double y1, double x2, double y2) {
        return Math.hypot(x1 - x2, y1 - y2);
    }
}
