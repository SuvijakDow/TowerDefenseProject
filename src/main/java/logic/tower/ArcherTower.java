package logic.tower;

public class ArcherTower extends Tower {
    private int level;

    public ArcherTower() {
        super(20, 150.0, 60, 100, "Towers/Combat Towers/spr_tower_archer.png");
        this.level = 1;
    }

    @Override
    public void upgrade() {
        this.level++;
        this.damage += 5;
        this.range += 10.0;
        this.fireCooldown = Math.max(1, this.fireCooldown - 2);
    }

    public int getLevel() {
        return level;
    }
}
