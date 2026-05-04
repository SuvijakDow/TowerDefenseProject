package logic.tower;

import logic.enemy.Enemy;

import java.util.List;

public class ArcherTower extends Tower {
    private int level;
    private static final String ARCHER_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_archer_projectile.png";

    public ArcherTower() {
        super(30, 80.0, 60, 100, "Towers/Combat Towers/spr_tower_archer.png");
        this.level = 1;
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, ARCHER_PROJECTILE_SPRITE);
    }

    @Override
    public void upgrade() {
        this.level++;
        this.damage += 5;
        this.range += 10.0;
        this.fireCooldown = Math.max(1, this.fireCooldown - 2);
    }

    public int getLevel() {
        return level;
    }
}
