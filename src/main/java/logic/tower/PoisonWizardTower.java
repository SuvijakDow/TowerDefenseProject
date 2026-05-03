package logic.tower;

import java.util.List;

import logic.enemy.Enemy;
import logic.interfaces.Skillable;
import logic.map.GameMap;

public class PoisonWizardTower extends Tower implements Skillable {
    private int level;
    private int poisonDamage;
    private static final String POISON_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_poison_wizard_projectile.png";

    public PoisonWizardTower() {
        super(60, 150.0, 120, 150, "Towers/Combat Towers/spr_tower_poison_wizard.png");
        this.level = 1;
        this.poisonDamage = 5;
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
        activeProjectiles.add(new Projectile(sx, sy, Projectile.DEFAULT_SPEED, damage, target, POISON_PROJECTILE_SPRITE));
        currentCooldown = fireCooldown;
    }

    @Override
    public void upgrade() {
        this.level++;
        this.damage += 10;
        this.poisonDamage += 5;
    }

    @Override
    public void useActiveSkill(List<Enemy> targets) {
        for (Enemy target : targets) {
            if (isEnemyInRange(target)) {
                target.takeDamage(poisonDamage * 3);
            }
        }
    }

    public int getLevel() {
        return level;
    }

    public int getPoisonDamage() {
        return poisonDamage;
    }
}
