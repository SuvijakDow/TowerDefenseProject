package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Boss enemy that can split into smaller enemies on death.
public class BossEnemy extends Enemy {
    private static final int DEFAULT_HEALTH = 800;
    private static final double DEFAULT_SPEED = 0.7;
    private static final int CHILD_COUNT = 3;

    // Creates a boss enemy with default boss stats.
    public BossEnemy(double x, double y) {
        super(DEFAULT_HEALTH, DEFAULT_SPEED, x, y);
    }

    // Creates a boss enemy with custom stats.
    public BossEnemy(int health, double baseSpeed, double x, double y) {
        super(health, baseSpeed, x, y);
    }

    // Moves enemy forward along the x-axis.
    @Override
    public void move() {
        x += baseSpeed;
    }

    // Spawns 3 basic enemies at the current position when the boss is dead.
    public List<Enemy> spawnChildrenOnDeath() {
        if (isAlive()) {
            return Collections.emptyList();
        }

        List<Enemy> children = new ArrayList<>(CHILD_COUNT);
        for (int i = 0; i < CHILD_COUNT; i++) {
            children.add(new BasicEnemy(x, y));
        }
        return children;
    }
}
