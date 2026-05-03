package logic.enemy;

import logic.map.Waypoint;

public class GoblinEnemy extends Enemy {
    public GoblinEnemy() {
        super(90, 1.8, 18, false, "Enemies/spr_goblin.png");
    }

    @Override
    public void move(Waypoint target) {
        moveTowards(target);
    }
}
