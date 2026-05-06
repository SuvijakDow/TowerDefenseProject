package logic.tower;

import logic.enemy.Enemy;

import java.util.List;

public final class LightningWizardTower extends Tower {
    private static final int DAMAGE = 60;
    private static final double RANGE = 300.0;
    private static final int FIRE_COOLDOWN = 60;
    private static final int COST = 300;
    private static final String SPRITE = "Towers/Combat Towers/spr_tower_lightning_tower.png";
    private static final int LIGHTNING_DAMAGE = 5;
    private static final String LIGHTNING_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_lightning_tower_projectile.png";

    public LightningWizardTower() {
        super(DAMAGE, RANGE, FIRE_COOLDOWN, COST, SPRITE);
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, LIGHTNING_PROJECTILE_SPRITE);
    }

    public int getLightningDamage() {
        return LIGHTNING_DAMAGE;
    }
}
