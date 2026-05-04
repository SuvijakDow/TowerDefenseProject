package logic.tower;

import java.util.List;

import logic.enemy.Enemy;
import logic.interfaces.Skillable;

public class PoisonWizardTower extends Tower implements Skillable {
    private int level;
    private int poisonDamage;
    private static final String POISON_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_poison_wizard_projectile.png";

    public PoisonWizardTower() {
        super(60, 150.0, 120, 150, "Towers/Combat Towers/spr_tower_poison_wizard.png");
        this.level = 1;
        this.poisonDamage = 5;
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, POISON_PROJECTILE_SPRITE);
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
