package logic.tower;

import logic.enemy.Enemy;

import java.util.List;

public final class CrossbowTower extends Tower {
    private static final int DAMAGE = 35;
    private static final double RANGE = 150.0;
    private static final int FIRE_COOLDOWN = 40;
    private static final int COST = 130;
    private static final String SPRITE = "Towers/Combat Towers/spr_tower_crossbow.png";
    private static final String CROSSBOW_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_crossbow_projectile.png";

    public CrossbowTower() {
        super(DAMAGE, RANGE, FIRE_COOLDOWN, COST, SPRITE);
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, CROSSBOW_PROJECTILE_SPRITE);
    }
}
