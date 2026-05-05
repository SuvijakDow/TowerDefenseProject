package logic.tower;

import logic.enemy.Enemy;

import java.util.List;

public class CrossbowTower extends Tower {
    private static final String CROSSBOW_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_crossbow_projectile.png";

    public CrossbowTower() {
        super(35, 150.0, 40, 130, "Towers/Combat Towers/spr_tower_crossbow.png");
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, CROSSBOW_PROJECTILE_SPRITE);
    }
}
