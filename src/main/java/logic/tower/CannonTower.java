package logic.tower;

import java.util.List;

import logic.enemy.Enemy;

/**
 * Heavy tower with higher damage and range than the basic archer.
 */
public final class CannonTower extends Tower {
    /** Base damage of the cannon tower. */
    private static final int DAMAGE = 50;
    /** Attack range of the cannon tower. */
    private static final double RANGE = 120.0;
    /** Fire cooldown of the cannon tower in ticks. */
    private static final int FIRE_COOLDOWN = 48;
    /** Placement cost of the cannon tower. */
    private static final int COST = 120;
    /** Asset path for the cannon tower sprite. */
    private static final String SPRITE = "Towers/Combat Towers/spr_tower_cannon.png";
    /** Asset path for the cannon projectile sprite. */
    private static final String CANNON_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_cannon_projectile.png";

    /**
     * Creates a cannon tower with predefined base stats.
     */
    public CannonTower() {
        super(DAMAGE, RANGE, FIRE_COOLDOWN, COST, SPRITE);
    }

    /**
     * Updates cannon tower combat using cannon projectile visuals.
     */
    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, CANNON_PROJECTILE_SPRITE);
    }
}
