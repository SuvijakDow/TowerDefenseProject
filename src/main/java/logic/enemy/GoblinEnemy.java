package logic.enemy;

import logic.map.Waypoint;

public class GoblinEnemy extends Enemy {
    public GoblinEnemy() {
        super(90, 1.8, 18, false, "Enemies/spr_goblin.png", 2); // Fast ground enemy - moderate damage
    }

    @Override
    public void move(Waypoint target) {
        moveTowards(target);
    }
}
