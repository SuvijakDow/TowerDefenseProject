package logic.interfaces;

/** Represents an entity that can receive damage. */
public interface Damageable {
    /**
     * Applies damage to the entity.
     *
     * @param amount the amount of damage to take
     */
    void takeDamage(int amount);
}
