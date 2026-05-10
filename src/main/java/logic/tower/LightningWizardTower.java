package logic.tower;

import logic.enemy.Enemy;

import java.util.List;

/**
 * Long-range wizard tower with high cost and strong projectile damage.
 */
public final class LightningWizardTower extends Tower {
    /** Base damage of the lightning wizard tower. */
    private static final int DAMAGE = 60;
    /** Attack range of the lightning wizard tower. */
    private static final double RANGE = 300.0;
    /** Fire cooldown of the lightning wizard tower in ticks. */
    private static final int FIRE_COOLDOWN = 60;
    /** Placement cost of the lightning wizard tower. */
    private static final int COST = 300;
    /** Asset path for the lightning wizard tower sprite. */
    private static final String SPRITE = "Towers/Combat Towers/spr_tower_lightning_tower.png";
    /** Asset path for the lightning projectile sprite. */
    private static final String LIGHTNING_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_lightning_tower_projectile.png";

    /**
     * Creates a lightning wizard tower with predefined base stats.
     */
    public LightningWizardTower() {
        super(DAMAGE, RANGE, FIRE_COOLDOWN, COST, SPRITE);
    }

    /**
     * Updates lightning wizard tower combat using lightning projectile visuals.
     */
    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, LIGHTNING_PROJECTILE_SPRITE);
    }
}
