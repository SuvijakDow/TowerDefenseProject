package logic.enemy;

/**
 * Slow and sturdy zombie enemy that deals moderate base damage.
 */
public final class ZombieEnemy extends Enemy {
    /** Base health of the zombie enemy. */
    private static final int MAX_HEALTH = 170;
    /** Movement speed of the zombie enemy. */
    private static final double SPEED = 0.9;
    /** Money rewarded when the zombie enemy is killed. */
    private static final int REWARD_MONEY = 22;
    /** Asset path for the zombie enemy sprite. */
    private static final String SPRITE = "Enemies/spr_zombie.png";
    /** Damage dealt to the base if the zombie enemy reaches the end. */
    private static final int BASE_DAMAGE = 4;

    /**
     * Creates a zombie enemy instance.
     */
    public ZombieEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
