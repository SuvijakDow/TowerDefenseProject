package logic.tower;

import logic.enemy.Enemy;

import java.util.List;

public class CrossbowTower extends Tower {
    private int level;
    private static final String CROSSBOW_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_crossbow_projectile.png";

    public CrossbowTower() {
        super(35, 150.0, 40, 130, "Towers/Combat Towers/spr_tower_crossbow.png");
        this.level = 1;
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, CROSSBOW_PROJECTILE_SPRITE);
    }


    @Override
    public void upgrade() {
        this.level++;
        this.damage += 4;
        this.range += 12.0;
        this.fireCooldown = Math.max(3, this.fireCooldown - 3);
    }

    public int getLevel() {
        return level;
    }
}
