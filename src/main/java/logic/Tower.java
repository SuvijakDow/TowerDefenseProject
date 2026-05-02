package logic;

import java.util.List;

// Abstract base for towers with shared combat stats and position.
// Subclasses define targeting and attack behavior via attack(List<Enemy>).
public abstract class Tower implements Upgradable {
    protected int damage;
    protected double range;
    protected int attackCooldown;
    protected double x;
    protected double y;

    // Creates a tower with initial combat stats and position.
    protected Tower(int damage, double range, int attackCooldown, double x, double y) {
        setDamage(damage);
        setRange(range);
        setAttackCooldown(attackCooldown);
        this.x = x;
        this.y = y;
    }

    // Returns attack damage.
    public int getDamage() {
        return damage;
    }

    // Sets attack damage.
    public void setDamage(int damage) {
        requireNonNegative(damage, "damage");
        this.damage = damage;
    }

    // Returns attack range.
    public double getRange() {
        return range;
    }

    // Sets attack range.
    public void setRange(double range) {
        requireNonNegative(range, "range");
        this.range = range;
    }

    // Returns attack cooldown in ticks.
    public int getAttackCooldown() {
        return attackCooldown;
    }

    // Sets attack cooldown in ticks.
    public void setAttackCooldown(int attackCooldown) {
        requireNonNegative(attackCooldown, "attackCooldown");
        this.attackCooldown = attackCooldown;
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

    // Applies the default upgrade policy: increases damage/range and reduces cooldown to at least one tick.
    @Override
    public void upgrade() {
        int bonusDamage = Math.max(1, (int) Math.ceil(damage * 0.20));
        damage += bonusDamage;
        range += 0.5;
        if (attackCooldown > 1) {
            attackCooldown -= 1;
        }
    }

    // Executes tower-specific attack logic against candidate enemies.
    public abstract void attack(List<Enemy> targets);

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
