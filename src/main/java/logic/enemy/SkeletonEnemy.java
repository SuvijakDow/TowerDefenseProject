package logic.enemy;

/**
 * Mid-tier skeleton enemy with balanced movement and durability.
 */
public final class SkeletonEnemy extends Enemy {
    /** Base health of the skeleton enemy. */
    private static final int MAX_HEALTH = 130;
    /** Movement speed of the skeleton enemy. */
    private static final double SPEED = 1.2;
    /** Money rewarded when the skeleton enemy is killed. */
    private static final int REWARD_MONEY = 16;
    /** Asset path for the skeleton enemy sprite. */
    private static final String SPRITE = "Enemies/spr_skeleton.png";
    /** Damage dealt to the base if the skeleton enemy reaches the end. */
    private static final int BASE_DAMAGE = 3;

    /**
     * Creates a skeleton enemy instance.
     */
    public SkeletonEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
