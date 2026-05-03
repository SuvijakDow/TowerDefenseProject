package logic.enemy;

import logic.map.Waypoint;

public class BigSlimeEnemy extends Enemy {
    public BigSlimeEnemy() {
        super(180, 0.8, 20, false, "Enemies/spr_big_slime.png");
    }

    @Override
    public void move(Waypoint target) {
        moveTowards(target);
    }
}
