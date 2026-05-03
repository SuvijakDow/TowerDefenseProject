package logic.enemy;

import logic.map.Waypoint;

public class GhostEnemy extends Enemy {
    public GhostEnemy() {
        super(80, 2.2, 24, true, "Enemies/spr_ghost.png");
    }

    @Override
    public void move(Waypoint target) {
        moveTowards(target);
    }
}
