package logic.tower;

import java.util.List;

import logic.enemy.Enemy;
import logic.interfaces.Upgradable;
import logic.map.GameMap;

public abstract class Tower implements Upgradable {
    protected int damage;
    protected double range;
    /** Fire interval in game ticks. */
    protected int fireCooldown;
    /** Ticks until next shot; 0 = ready. */
    protected int currentCooldown;
    protected double x;
    protected double y;
    protected int cost;
    protected String spriteName;
    /** Tile row where placed; {@code -1} if not grid-placed (e.g. legacy/tests). */
    protected int gridRow = -1;
    /** Tile column where placed; {@code -1} if not grid-placed. */
    protected int gridCol = -1;

    public Tower(int damage, double range, int fireCooldown, int cost, String spriteName) {
        this.damage = damage;
        this.range = range;
        this.fireCooldown = fireCooldown;
        this.currentCooldown = 0;
        this.cost = cost;
        this.spriteName = spriteName != null ? spriteName : "";
    }

    /**
     * Tick-based combat: when ready, fires a {@link Projectile} at the closest in-range enemy.
     * Projectile towers use this implementation; override for other behaviour (e.g. wizard).
     */
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
        if (currentCooldown > 0) {
            return;
        }
        Enemy target = findClosestEnemyInRange(enemies);
        if (target == null) {
            return;
        }
        int T = GameMap.PATH_TILE_PIXEL_SIZE;
        double sx = x + T / 2.0;
        double sy = y + T;
        activeProjectiles.add(new Projectile(sx, sy, Projectile.DEFAULT_SPEED, damage, target,
                Projectile.DEFAULT_SPRITE));
        currentCooldown = fireCooldown;
    }

    protected Enemy findClosestEnemyInRange(List<Enemy> enemies) {
        if (enemies == null || enemies.isEmpty()) {
            return null;
        }
        Enemy best = null;
        double bestDist = Double.MAX_VALUE;
        for (Enemy e : enemies) {
            if (e.isDead()) {
                continue;
            }
            double dx = e.getX() - x;
            double dy = e.getY() - y;
            double d = Math.sqrt(dx * dx + dy * dy);
            if (d <= range && d < bestDist) {
                bestDist = d;
                best = e;
            }
        }
        return best;
    }

    protected boolean isEnemyInRange(Enemy enemy) {
        double dx = enemy.getX() - this.x;
        double dy = enemy.getY() - this.y;
        return Math.sqrt(dx * dx + dy * dy) <= this.range;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public int getFireCooldown() {
        return fireCooldown;
    }

    public void setFireCooldown(int fireCooldown) {
        this.fireCooldown = fireCooldown;
    }

    public int getCurrentCooldown() {
        return currentCooldown;
    }

    public void setCurrentCooldown(int currentCooldown) {
        this.currentCooldown = currentCooldown;
    }

    /** @deprecated Use {@link #getFireCooldown()} (tick-based). */
    @Deprecated
    public double getAttackCooldown() {
        return fireCooldown;
    }

    /** @deprecated Use {@link #setFireCooldown(int)}. */
    @Deprecated
    public void setAttackCooldown(double attackCooldown) {
        this.fireCooldown = (int) Math.round(attackCooldown);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getSpriteName() {
        return spriteName;
    }

    public void setSpriteName(String spriteName) {
        this.spriteName = spriteName != null ? spriteName : "";
    }

    public int getGridRow() {
        return gridRow;
    }

    public int getGridCol() {
        return gridCol;
    }

    public void setPlacementTile(int row, int col) {
        this.gridRow = row;
        this.gridCol = col;
    }
}
