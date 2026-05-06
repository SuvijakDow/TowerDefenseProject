package logic.tower;

import java.util.List;

import logic.enemy.Enemy;

public class PoisonWizardTower extends Tower {
    private int poisonDamage;
    private static final String POISON_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_poison_wizard_projectile.png";

    public PoisonWizardTower() {
        super(60, 150.0, 120, 150, "Towers/Combat Towers/spr_tower_poison_wizard.png");
        this.poisonDamage = 5;
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, POISON_PROJECTILE_SPRITE);
    }

    public int getPoisonDamage() {
        return poisonDamage;
    }
}
