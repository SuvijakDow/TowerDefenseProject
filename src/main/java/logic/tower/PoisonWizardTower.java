package logic.tower;

import java.util.List;

import logic.enemy.Enemy;
import logic.interfaces.Skillable;

public class PoisonWizardTower extends Tower implements Skillable {
    private int level;
    private int poisonDamage;

    public PoisonWizardTower() {
        super(30, 150.0, 120, 150, "");
        this.level = 1;
        this.poisonDamage = 5;
    }

    /** Instant hit-scan damage (no projectile). */
    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
        if (currentCooldown > 0) {
            return;
        }
        for (Enemy enemy : enemies) {
            if (!enemy.isDead() && isEnemyInRange(enemy)) {
                enemy.takeDamage(damage);
                currentCooldown = fireCooldown;
                break;
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
