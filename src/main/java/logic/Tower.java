package logic;

import java.util.List;

public abstract class Tower implements Upgradable {
    protected int damage;
    protected double range;
    protected double attackCooldown;
    protected double currentCooldown;
    protected double x;
    protected double y;
    protected int cost;

    public Tower(int damage, double range, double attackCooldown, int cost) {
        this.damage = damage;
        this.range = range;
        this.attackCooldown = attackCooldown;
        this.currentCooldown = 0.0;
        this.cost = cost;
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
}
