package logic.tower;

import logic.enemy.Enemy;

import java.util.List;

/**
 * Basic single-target ranged tower with low cost and moderate stats.
 */
public final class ArcherTower extends Tower {
    /** Base damage of the archer tower. */
    private static final int DAMAGE = 30;
    /** Attack range of the archer tower. */
    private static final double RANGE = 80.0;
    /** Fire cooldown of the archer tower in ticks. */
    private static final int FIRE_COOLDOWN = 60;
    /** Placement cost of the archer tower. */
    private static final int COST = 100;
    /** Asset path for the archer tower sprite. */
    private static final String SPRITE = "Towers/Combat Towers/spr_tower_archer.png";
    /** Asset path for the archer projectile sprite. */
    private static final String ARCHER_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_archer_projectile.png";

    /**
     * Creates an archer tower with predefined base stats.
     */
    public ArcherTower() {
        super(DAMAGE, RANGE, FIRE_COOLDOWN, COST, SPRITE);
    }

    /**
     * Updates archer tower combat using archer projectile visuals.
     */
    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, ARCHER_PROJECTILE_SPRITE);
    }
}
