package logic.tower;

import logic.enemy.Enemy;

import java.util.List;

public class CrossbowTower extends Tower {
    private int level;

    public CrossbowTower() {
        super(15, 150.0, 18, 130, "Towers/Combat Towers/spr_tower_crossbow.png");
        this.level = 1;
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
