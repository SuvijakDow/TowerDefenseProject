package logic.tower;

import logic.enemy.Enemy;

import java.util.List;

public final class IceWizardTower extends Tower {
    private static final int DAMAGE = 60;
    private static final double RANGE = 150.0;
    private static final int FIRE_COOLDOWN = 120;
    private static final int COST = 150;
    private static final String SPRITE = "Towers/Combat Towers/spr_tower_ice_wizard.png";
    private static final String ICE_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_ice_wizard_projectile.png";

    public IceWizardTower() {
        super(DAMAGE, RANGE, FIRE_COOLDOWN, COST, SPRITE);
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, ICE_PROJECTILE_SPRITE);
    }
}
