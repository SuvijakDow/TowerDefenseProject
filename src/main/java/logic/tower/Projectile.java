package logic.tower;

import logic.enemy.Enemy;

/**
 * Projectile fired by towers toward a specific enemy target.
 */
public final class Projectile {
    /** Default projectile sprite path. */
    public static final String DEFAULT_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_archer_projectile.png";
    /** Default projectile movement speed. */
    public static final double DEFAULT_SPEED = 12.0;
    /** Collision radius for determining if a projectile has hit its target. */
    private static final double HIT_RADIUS = 5.0;

    /** The X coordinate in world space. */
    private double x;
    /** The Y coordinate in world space. */
    private double y;
    /** Movement speed of the projectile. */
    private final double speed;
    /** Damage applied to the target upon hit. */
    private final int damage;
    /** Target enemy being tracked. */
    private final Enemy target;
    /** Sprite resource path. */
    private final String spriteName;

    /**
     * Creates a projectile.
     *
     * @param x initial x position
     * @param y initial y position
     * @param speed movement speed per update
     * @param damage damage applied on hit
     * @param target target enemy
     * @param spriteName projectile sprite resource path; defaults when blank/null
     */
    public Projectile(double x, double y, double speed, int damage, Enemy target, String spriteName) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.damage = damage;
        this.target = target;
        this.spriteName = (spriteName != null && !spriteName.isEmpty()) ? spriteName : DEFAULT_SPRITE;
    }

    /**
     * Moves toward {@link #target}.
     *
     * @return {@code true} if this projectile should be removed (hit or invalid target)
     */
    public boolean update() {
        if (target == null) {
            return true;
        }
        if (isWithinHitRadius()) {
            return true;
        }

        moveTowardTarget();
        return isWithinHitRadius();
    }

    /**
     * Computes the distance to the target and moves the projectile along that vector.
     */
    private void moveTowardTarget() {
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double distance = Math.hypot(dx, dy);
        if (distance <= 0) {
            return;
        }

        double nextDeltaX = (dx / distance) * speed;
        double nextDeltaY = (dy / distance) * speed;
        x = Math.abs(nextDeltaX) > Math.abs(dx) ? target.getX() : x + nextDeltaX;
        y = Math.abs(nextDeltaY) > Math.abs(dy) ? target.getY() : y + nextDeltaY;
    }

    /**
     * Checks if the projectile is close enough to the target to apply damage.
     *
     * @return {@code true} if within hit radius, {@code false} otherwise
     */
    private boolean isWithinHitRadius() {
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        return Math.hypot(dx, dy) < HIT_RADIUS;
    }

    /**
     * Gets the X coordinate.
     *
     * @return the X coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the Y coordinate.
     *
     * @return the Y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the damage value.
     *
     * @return the damage amount
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Gets the target enemy.
     *
     * @return the target enemy
     */
    public Enemy getTarget() {
        return target;
    }

    /**
     * Gets the sprite resource path.
     *
     * @return the sprite name
     */
    public String getSpriteName() {
        return spriteName;
    }
}
