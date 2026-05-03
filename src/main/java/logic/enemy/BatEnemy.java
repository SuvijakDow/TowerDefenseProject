package logic.enemy;

import logic.map.Waypoint;

public class BatEnemy extends Enemy {
    public BatEnemy() {
        super(50, 2.5, 15, true, "Enemies/spr_bat.png", 1); // Fast but weak - low damage
    }

    @Override
    public void move(Waypoint target) {
        moveTowards(target);
    }
}
