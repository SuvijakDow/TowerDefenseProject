package logic.enemy;

/**
 * Basic early-game slime enemy with balanced default stats.
 */
public final class SlimeEnemy extends Enemy {
    /** Base health of the normal slime enemy. */
    private static final int MAX_HEALTH = 100;
    /** Movement speed of the normal slime enemy. */
    private static final double SPEED = 1.0;
    /** Money rewarded when the normal slime enemy is killed. */
    private static final int REWARD_MONEY = 10;
    /** Asset path for the normal slime enemy sprite. */
    private static final String SPRITE = "Enemies/spr_normal_slime.png";
    /** Damage dealt to the base if the normal slime enemy reaches the end. */
    private static final int BASE_DAMAGE = 1;

    /**
     * Creates a standard slime enemy instance.
     */
    public SlimeEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
