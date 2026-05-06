package logic.tower;

import java.util.List;

import logic.enemy.Enemy;

public final class PoisonWizardTower extends Tower {
    private static final int DAMAGE = 60;
    private static final double RANGE = 150.0;
    private static final int FIRE_COOLDOWN = 120;
    private static final int COST = 150;
    private static final String SPRITE = "Towers/Combat Towers/spr_tower_poison_wizard.png";
    private static final int POISON_DAMAGE = 5;
    private static final String POISON_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_poison_wizard_projectile.png";

    public PoisonWizardTower() {
        super(DAMAGE, RANGE, FIRE_COOLDOWN, COST, SPRITE);
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, POISON_PROJECTILE_SPRITE);
    }

    public int getPoisonDamage() {
        return POISON_DAMAGE;
    }
}
