package logic;

import java.util.List;

public class ArcherTower extends Tower {
    private int level;

    public ArcherTower() {
        super(15, 100.0, 0.5);
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
        this.damage += 5;
        this.range += 10.0;
        this.attackCooldown = Math.max(0.1, this.attackCooldown - 0.05);
    }

    public int getLevel() {
        return level;
    }
}
