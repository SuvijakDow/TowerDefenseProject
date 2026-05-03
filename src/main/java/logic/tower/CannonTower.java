package logic.tower;

import java.util.List;

import logic.enemy.Enemy;

public class CannonTower extends Tower {
    private int level;

    public CannonTower() {
        super(30, 120.0, 0.8, 120);
        this.level = 1;
    }

    @Override
    public void attack(List<Enemy> enemies) {
        if (currentCooldown <= 0) {
            for (Enemy enemy : enemies) {
                if (isEnemyInRange(enemy)) {
                    enemy.takeDamage(damage);
                    currentCooldown = attackCooldown;
                    break; // Attack one target at a time
                }
            }
        }
    }

    @Override
    public void upgrade() {
        this.level++;
        this.damage += 6;
        this.range += 8.0;
        this.attackCooldown = Math.max(0.05, this.attackCooldown - 0.03);
    }

    public int getLevel() {
        return level;
    }
}
