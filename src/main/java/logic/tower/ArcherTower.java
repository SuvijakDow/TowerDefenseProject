package logic.tower;

import logic.enemy.Enemy;

import java.util.List;

/**
 * Basic single-target ranged tower with low cost and moderate stats.
 */
public final class ArcherTower extends Tower {
    private static final int DAMAGE = 30;
    private static final double RANGE = 80.0;
    private static final int FIRE_COOLDOWN = 60;
    private static final int COST = 100;
    private static final String SPRITE = "Towers/Combat Towers/spr_tower_archer.png";
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
