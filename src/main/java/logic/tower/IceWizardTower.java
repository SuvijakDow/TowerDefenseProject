package logic.tower;

import logic.enemy.Enemy;
import logic.interfaces.Skillable;

import java.util.List;

public class IceWizardTower extends Tower implements Skillable {
    private int iceDamage;
    private static final String ICE_PROJECTILE_SPRITE = "Towers/Combat Towers Projectiles/spr_tower_ice_wizard_projectile.png";

    public IceWizardTower() {
        super(60, 150.0, 120, 150, "Towers/Combat Towers/spr_tower_ice_wizard.png");
        this.iceDamage = 5;
    }

    @Override
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, ICE_PROJECTILE_SPRITE);
    }

    @Override
    public void useActiveSkill(List<Enemy> targets) {
        for (Enemy target : targets) {
            if (isEnemyInRange(target)) {
                target.takeDamage(iceDamage * 3);
            }
        }
    }

    public int getIceDamage() {
        return iceDamage;
    }
}
