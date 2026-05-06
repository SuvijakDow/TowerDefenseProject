package logic.tower;

import java.util.List;

import logic.enemy.Enemy;

/**
 * Heavy tower with higher damage and range than the basic archer.
 */
public final class CannonTower extends Tower {
    private static final int DAMAGE = 50;
    private static final double RANGE = 120.0;
    private static final int FIRE_COOLDOWN = 48;
    private static final int COST = 120;
    private static final String SPRITE = "Towers/Combat Towers/spr_tower_cannon.png";
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
