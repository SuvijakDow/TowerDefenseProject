package logic.enemy;

import logic.map.Waypoint;

public class SkeletonEnemy extends Enemy {
    public SkeletonEnemy() {
        super(130, 1.2, 16, false, "Enemies/spr_skeleton.png");
    }

    @Override
    public void move(Waypoint target) {
        moveTowards(target);
    }
}
