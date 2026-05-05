package logic.tower;

import logic.enemy.Enemy;

import java.util.List;

public class ArcherTower extends Tower {
    private static final String ARCHER_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_archer_projectile.png";

    public ArcherTower() {
        super(30, 80.0, 60, 100, "Towers/Combat Towers/spr_tower_archer.png");
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, ARCHER_PROJECTILE_SPRITE);
    }
}
