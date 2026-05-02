package logic;

// Contract for objects that can receive damage.
public interface Damageable {

    // Applies incoming damage to this object.
    void takeDamage(int amount);
}
