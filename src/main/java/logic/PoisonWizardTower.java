package logic;

import java.util.List;

public class PoisonWizardTower extends Tower implements Skillable {
    private int level;
    private int poisonDamage;

    public PoisonWizardTower() {
        super(30, 150.0, 2.0, 150);
        this.level = 1;
        this.poisonDamage = 5;
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
        this.damage += 10;
        this.poisonDamage += 5;
    }

    @Override
    public void useActiveSkill(List<Enemy> targets) {
        for (Enemy target : targets) {
            if (isEnemyInRange(target)) {
                target.takeDamage(poisonDamage * 3);
            }
        }
    }

    public int getLevel() {
        return level;
    }

    public int getPoisonDamage() {
        return poisonDamage;
    }
}
