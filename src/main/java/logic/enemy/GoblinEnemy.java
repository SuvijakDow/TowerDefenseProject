package logic.enemy;

/**
 * Mobile goblin enemy with moderate health and reward.
 */
public final class GoblinEnemy extends Enemy {
    /** Base health of the goblin enemy. */
    private static final int MAX_HEALTH = 90;
    /** Movement speed of the goblin enemy. */
    private static final double SPEED = 1.8;
    /** Money rewarded when the goblin enemy is killed. */
    private static final int REWARD_MONEY = 18;
    /** Asset path for the goblin enemy sprite. */
    private static final String SPRITE = "Enemies/spr_goblin.png";
    /** Damage dealt to the base if the goblin enemy reaches the end. */
    private static final int BASE_DAMAGE = 2;

    /**
     * Creates a goblin enemy instance.
     */
    public GoblinEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
