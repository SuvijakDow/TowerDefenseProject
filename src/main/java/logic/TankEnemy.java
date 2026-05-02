package logic;

// Durable enemy with high health and low speed.
public class TankEnemy extends Enemy {
    private static final int DEFAULT_HEALTH = 300;
    private static final double DEFAULT_SPEED = 0.5;

    // Creates a tank enemy with default tank stats.
    public TankEnemy(double x, double y) {
        super(DEFAULT_HEALTH, DEFAULT_SPEED, x, y);
    }

    // Creates a tank enemy with custom stats.
    public TankEnemy(int health, double baseSpeed, double x, double y) {
        super(health, baseSpeed, x, y);
    }

    // Moves enemy forward along the x-axis.
    @Override
    public void move() {
        x += baseSpeed;
    }
}
