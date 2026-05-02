package logic;

// Standard enemy with straightforward forward movement.
public class BasicEnemy extends Enemy {
    private static final int DEFAULT_HEALTH = 100;
    private static final double DEFAULT_SPEED = 1.0;

    // Creates a basic enemy with default stats.
    public BasicEnemy(double x, double y) {
        super(DEFAULT_HEALTH, DEFAULT_SPEED, x, y);
    }

    // Creates a basic enemy with custom stats.
    public BasicEnemy(int health, double baseSpeed, double x, double y) {
        super(health, baseSpeed, x, y);
    }

    // Moves enemy forward along the x-axis.
    @Override
    public void move() {
        x += baseSpeed;
    }
}
