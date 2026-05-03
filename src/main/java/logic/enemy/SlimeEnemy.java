package logic.enemy;

import logic.map.Waypoint;

public class SlimeEnemy extends Enemy {
    public SlimeEnemy() {
        super(100, 1.0, 10, false, "Enemies/spr_normal_slime.png", 1); // Weak enemy - low damage
    }

    @Override
    public void move(Waypoint target) {
        moveTowards(target);
    }
}
