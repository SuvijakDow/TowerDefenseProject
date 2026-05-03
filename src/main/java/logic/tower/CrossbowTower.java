package logic.tower;

import logic.enemy.Enemy;

import java.util.List;

public class CrossbowTower extends Tower {
    private int level;

    public CrossbowTower() {
        super(15, 150.0, 0.3, 130);
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
        this.damage += 4;
        this.range += 12.0;
        this.attackCooldown = Math.max(0.05, this.attackCooldown - 0.05);
    }

    public int getLevel() {
        return level;
    }
}
