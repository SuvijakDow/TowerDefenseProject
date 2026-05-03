package logic.tower;

import java.util.List;

import logic.enemy.Enemy;
import logic.map.GameMap;

public class CannonTower extends Tower {
    private int level;
    private static final String CANNON_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_cannon_projectile.png";

    public CannonTower() {
        super(30, 120.0, 48, 120, "Towers/Combat Towers/spr_tower_cannon.png");
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
        activeProjectiles.add(new Projectile(sx, sy, Projectile.DEFAULT_SPEED, damage, target, CANNON_PROJECTILE_SPRITE));
        currentCooldown = fireCooldown;
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
