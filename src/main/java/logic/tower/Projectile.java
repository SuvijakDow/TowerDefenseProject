package logic.tower;

import logic.enemy.Enemy;

public final class Projectile {
    public static final String DEFAULT_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_archer_projectile.png";
    public static final double DEFAULT_SPEED = 12.0;
    private static final double HIT_RADIUS = 5.0;

    private double x;
    private double y;
    private final double speed;
    private final int damage;
    private final Enemy target;
    private final String spriteName;

    public Projectile(double x, double y, double speed, int damage, Enemy target, String spriteName) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.damage = damage;
        this.target = target;
        this.spriteName = (spriteName != null && !spriteName.isEmpty()) ? spriteName : DEFAULT_SPRITE;
    }

    /**
     * Moves toward {@link #target}. Returns {@code true} if this projectile should be removed
     * (impact or invalid target).
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

    private boolean isWithinHitRadius() {
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        return Math.hypot(dx, dy) < HIT_RADIUS;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getDamage() {
        return damage;
    }

    public Enemy getTarget() {
        return target;
    }

    public String getSpriteName() {
        return spriteName;
    }
}
