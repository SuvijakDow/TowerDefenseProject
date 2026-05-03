package logic.tower;

import logic.enemy.Enemy;
import logic.interfaces.Skillable;

import java.util.List;

public class IceWizardTower extends Tower implements Skillable {
    private int level;
    private int poisonDamage;

    public IceWizardTower() {
        super(30, 150.0, 120, 150, "Towers/Combat Towers/spr_tower_ice_wizard.png");
        this.level = 1;
        this.poisonDamage = 5;
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
