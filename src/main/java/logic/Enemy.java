package logic;

// Abstract base for enemy units with shared stats and position.
// Subclasses define movement via move().
public abstract class Enemy implements Damageable {
    protected int health;
    protected double baseSpeed;
    protected double x;
    protected double y;

    // Creates an enemy with initial health, speed, and position.
    protected Enemy(int health, double baseSpeed, double x, double y) {
        setHealth(health);
        setBaseSpeed(baseSpeed);
        this.x = x;
        this.y = y;
    }

    // Returns current health.
    public int getHealth() {
        return health;
    }

    // Sets current health.
    public void setHealth(int health) {
        requireNonNegative(health, "health");
        this.health = health;
    }

    // Returns base movement speed.
    public double getBaseSpeed() {
        return baseSpeed;
    }

    // Sets base movement speed.
    public void setBaseSpeed(double baseSpeed) {
        requireNonNegative(baseSpeed, "baseSpeed");
        this.baseSpeed = baseSpeed;
    }

    // Returns x position.
    public double getX() {
        return x;
    }

    // Sets x position.
    public void setX(double x) {
        this.x = x;
    }

    // Returns y position.
    public double getY() {
        return y;
    }

    // Sets y position.
    public void setY(double y) {
        this.y = y;
    }

    // Returns true when health is above zero.
    public boolean isAlive() {
        return health > 0;
    }

    // Applies damage and clamps health to a minimum of zero.
    @Override
    public void takeDamage(int amount) {
        requireNonNegative(amount, "amount");
        health = Math.max(0, health - amount);
    }

    // Updates position using enemy-specific movement logic.
    public abstract void move();

    // Validates non-negative integer values.
    private static void requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " must be >= 0.");
        }
    }

    // Validates non-negative floating-point values.
    private static void requireNonNegative(double value, String fieldName) {
        if (value < 0.0) {
            throw new IllegalArgumentException(fieldName + " must be >= 0.");
        }
    }
}
