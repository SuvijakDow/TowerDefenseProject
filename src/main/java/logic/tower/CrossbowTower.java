package logic.tower;

import logic.enemy.Enemy;
import logic.map.GameMap;

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
        double sy = y + T - (T * 0.8); // Offset upwards from tower head
        activeProjectiles.add(new Projectile(sx, sy, Projectile.DEFAULT_SPEED, damage, target, CROSSBOW_PROJECTILE_SPRITE));
        currentCooldown = fireCooldown;
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
