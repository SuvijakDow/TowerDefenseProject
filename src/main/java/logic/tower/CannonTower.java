package logic.tower;

import java.util.List;

import logic.enemy.Enemy;

public class CannonTower extends Tower {
    private static final String CANNON_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_cannon_projectile.png";

    public CannonTower() {
        super(50, 120.0, 48, 120, "Towers/Combat Towers/spr_tower_cannon.png");
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, CANNON_PROJECTILE_SPRITE);
    }
}
