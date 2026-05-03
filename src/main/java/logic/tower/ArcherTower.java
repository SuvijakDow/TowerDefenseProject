package logic.tower;

import logic.enemy.Enemy;
import logic.map.GameMap;

import java.util.List;

public class ArcherTower extends Tower {
    private int level;
    private static final String ARCHER_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_archer_projectile.png";

    public ArcherTower() {
        super(20, 150.0, 60, 100, "Towers/Combat Towers/spr_tower_archer.png");
        this.level = 1;
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
        if (currentCooldown > 0) {
            return;
        }
        Enemy target = findClosestEnemyInRange(enemies);
        if (target == null) {
            return;
        }
        int T = GameMap.PATH_TILE_PIXEL_SIZE;
        double sx = x + T / 2.0;
        double sy = y + T;
        activeProjectiles.add(new Projectile(sx, sy, Projectile.DEFAULT_SPEED, damage, target, ARCHER_PROJECTILE_SPRITE));
        currentCooldown = fireCooldown;
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
