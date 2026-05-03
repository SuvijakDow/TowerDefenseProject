package logic.tower;

import logic.enemy.Enemy;

public class Projectile {
    public static final String DEFAULT_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_archer_projectile.png";
    private static final double HIT_RADIUS = 5.0;
    public static final double DEFAULT_SPEED = 12.0;

    private double x;
    private double y;
    private double speed;
    private int damage;
    private Enemy target;
    private String spriteName;

    public Projectile(double x, double y, double speed, int damage, Enemy target, String spriteName) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.damage = damage;
        this.target = target;
        this.spriteName = spriteName != null && !spriteName.isEmpty() ? spriteName : DEFAULT_SPRITE;
    }

    /**
     * Moves toward {@link #target}. Returns {@code true} if this projectile should be removed
     * (impact or invalid target).
     */
    public boolean update() {
        if (target == null || target.isDead()) {
            return true;
        }
        double tx = target.getX();
        double ty = target.getY();
        double dx = tx - x;
        double dy = ty - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < HIT_RADIUS) {
            return true;
        }
        if (dist > 0) {
            double nx = (dx / dist) * speed;
            double ny = (dy / dist) * speed;
            if (Math.abs(nx) > Math.abs(dx)) {
                x = tx;
            } else {
                x += nx;
            }
            if (Math.abs(ny) > Math.abs(dy)) {
                y = ty;
            } else {
                y += ny;
            }
        }
        dx = target.getX() - x;
        dy = target.getY() - y;
        dist = Math.sqrt(dx * dx + dy * dy);
        return dist < HIT_RADIUS;
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
