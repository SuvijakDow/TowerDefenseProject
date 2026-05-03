package logic.enemy;

import logic.map.Waypoint;

public class KingSlimeEnemy extends Enemy {
    public KingSlimeEnemy() {
        super(320, 0.6, 45, false, "Enemies/spr_king_slime.png", 50); // Boss enemy - very high damage
    }

    

    @Override
    public void move(Waypoint target) {
        moveTowards(target);
    }
}
