package logic.enemy;

/**
 * Mid-tier skeleton enemy with balanced movement and durability.
 */
public final class SkeletonEnemy extends Enemy {
    private static final int MAX_HEALTH = 130;
    private static final double SPEED = 1.2;
    private static final int REWARD_MONEY = 16;
    private static final String SPRITE = "Enemies/spr_skeleton.png";
    private static final int BASE_DAMAGE = 3;

    /**
     * Creates a skeleton enemy instance.
     */
    public SkeletonEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
