package logic.tower;

import logic.enemy.Enemy;

import java.util.List;

public class LightningWizardTower extends Tower {
    private int lightningDamage;
    private static final String LIGHTNING_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_lightning_tower_projectile.png";

    public LightningWizardTower() {
        super(60, 300.0, 60, 300, "Towers/Combat Towers/spr_tower_lightning_tower.png");
        this.lightningDamage = 5;
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, LIGHTNING_PROJECTILE_SPRITE);
    }

    public int getLightningDamage() {
        return lightningDamage;
    }
}
