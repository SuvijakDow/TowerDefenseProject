package logic.tower;

import logic.enemy.Enemy;
import logic.interfaces.Skillable;
import logic.map.GameMap;

import java.util.List;

public class IceWizardTower extends Tower implements Skillable {
    private int level;
    private int iceDamage;
    private static final String ICE_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_ice_wizard_projectile.png";

    public IceWizardTower() {
        super(60, 150.0, 120, 150, "Towers/Combat Towers/spr_tower_ice_wizard.png");
        this.level = 1;
        this.iceDamage = 5;
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
        activeProjectiles.add(new Projectile(sx, sy, Projectile.DEFAULT_SPEED, damage, target, ICE_PROJECTILE_SPRITE));
        currentCooldown = fireCooldown;
    }


    @Override
    public void upgrade() {
        this.level++;
        this.damage += 10;
        this.iceDamage += 5;
    }

    @Override
    public void useActiveSkill(List<Enemy> targets) {
        for (Enemy target : targets) {
            if (isEnemyInRange(target)) {
                target.takeDamage(iceDamage * 3);
            }
        }
    }

    public int getLevel() {
        return level;
    }

    public int getIceDamage() {
        return iceDamage;
    }
}
