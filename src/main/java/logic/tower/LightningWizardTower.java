package logic.tower;

import logic.enemy.Enemy;
import logic.interfaces.Skillable;

import java.util.List;

public class LightningWizardTower extends Tower implements Skillable {
    private int level;
    private int lightningDamage;
    private static final String LIGHTNING_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_lightning_tower_projectile.png";

    public LightningWizardTower() {
        super(60, 300.0, 60, 300, "Towers/Combat Towers/spr_tower_lightning_tower.png");
        this.level = 1;
        this.lightningDamage = 5;
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, LIGHTNING_PROJECTILE_SPRITE);
    }


    @Override
    public void upgrade() {
        this.level++;
        this.damage += 10;
        this.lightningDamage += 5;
    }

    @Override
    public void useActiveSkill(List<Enemy> targets) {
        for (Enemy target : targets) {
            if (isEnemyInRange(target)) {
                target.takeDamage(lightningDamage * 3);
            }
        }
    }

    public int getLevel() {
        return level;
    }

    public int getLightningDamage() {
        return lightningDamage;
    }
}
