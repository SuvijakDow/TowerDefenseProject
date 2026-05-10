package logic.enemy;

/**
 * High-threat demon enemy with strong stats and high reward.
 */
public final class DemonEnemy extends Enemy {
    /** Base health of the demon enemy. */
    private static final int MAX_HEALTH = 250;
    /** Movement speed of the demon enemy. */
    private static final double SPEED = 1.5;
    /** Money rewarded when the demon enemy is killed. */
    private static final int REWARD_MONEY = 35;
    /** Asset path for the demon enemy sprite. */
    private static final String SPRITE = "Enemies/spr_demon.png";
    /** Damage dealt to the base if the demon enemy reaches the end. */
    private static final int BASE_DAMAGE = 5;

    /**
     * Creates a demon enemy instance.
     */
    public DemonEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
