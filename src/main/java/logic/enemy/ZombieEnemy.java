package logic.enemy;

import logic.map.Waypoint;

public class ZombieEnemy extends Enemy {
    public ZombieEnemy() {
        super(170, 0.9, 22, false, "Enemies/spr_zombie.png", 4); // Tanky slow enemy - high damage
    }

    @Override
    public void move(Waypoint target) {
        moveTowards(target);
    }
}
