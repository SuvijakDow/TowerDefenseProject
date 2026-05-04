package logic.tower;

import java.util.List;

import logic.enemy.Enemy;

public class CannonTower extends Tower {
    private int level;
    private static final String CANNON_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_cannon_projectile.png";

    public CannonTower() {
        super(50, 120.0, 48, 120, "Towers/Combat Towers/spr_tower_cannon.png");
        this.level = 1;
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, CANNON_PROJECTILE_SPRITE);
    }


    @Override
    public void upgrade() {
        this.level++;
        this.damage += 6;
        this.range += 8.0;
        this.fireCooldown = Math.max(3, this.fireCooldown - 2);
    }

    public int getLevel() {
        return level;
    }
}
