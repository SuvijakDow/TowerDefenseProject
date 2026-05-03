package logic.tower;

import logic.enemy.Enemy;
import logic.interfaces.Skillable;

import java.util.List;

public class LightingWizardTower extends Tower implements Skillable {
    private int level;
    private int poisonDamage;

    public LightingWizardTower() {
        super(30, 150.0, 120, 150, "Towers/Combat Towers/spr_tower_lightning_tower.png");
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
