package logic.tower;

import java.util.List;

import logic.enemy.Enemy;
import logic.interfaces.Upgradable;

public abstract class Tower implements Upgradable {
    protected int damage;
    protected double range;
    protected double attackCooldown;
    protected double currentCooldown;
    protected double x;
    protected double y;
    protected int cost;
    protected String spriteName;
    /** Tile row where placed; {@code -1} if not grid-placed (e.g. legacy/tests). */
    protected int gridRow = -1;
    /** Tile column where placed; {@code -1} if not grid-placed. */
    protected int gridCol = -1;

    public Tower(int damage, double range, double attackCooldown, int cost, String spriteName) {
        this.damage = damage;
        this.range = range;
        this.attackCooldown = attackCooldown;
        this.currentCooldown = 0.0;
        this.cost = cost;
        this.spriteName = spriteName != null ? spriteName : "";
    }

    public abstract void attack(List<Enemy> enemies);

    public void updateCooldown(double deltaTime) {
        if (currentCooldown > 0) {
            currentCooldown -= deltaTime;
        }
    }

    protected boolean isEnemyInRange(Enemy enemy) {
        double dx = enemy.getX() - this.x;
        double dy = enemy.getY() - this.y;
        return Math.sqrt(dx * dx + dy * dy) <= this.range;
    }

    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
    public double getRange() { return range; }
    public void setRange(double range) { this.range = range; }
    public double getAttackCooldown() { return attackCooldown; }
    public void setAttackCooldown(double attackCooldown) { this.attackCooldown = attackCooldown; }
    public double getCurrentCooldown() { return currentCooldown; }
    public void setCurrentCooldown(double currentCooldown) { this.currentCooldown = currentCooldown; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public String getSpriteName() { return spriteName; }
    public void setSpriteName(String spriteName) { this.spriteName = spriteName != null ? spriteName : ""; }

    public int getGridRow() { return gridRow; }
    public int getGridCol() { return gridCol; }

    public void setPlacementTile(int row, int col) {
        this.gridRow = row;
        this.gridCol = col;
    }
}
