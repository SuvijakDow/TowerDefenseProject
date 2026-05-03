package logic.enemy;

import logic.map.Waypoint;

public class DemonEnemy extends Enemy {
    public DemonEnemy() {
        super(250, 1.5, 35, true, "Enemies/spr_demon.png", 5); // Strong flying enemy - high damage
    }

    @Override
    public void move(Waypoint target) {
        moveTowards(target);
    }
}
