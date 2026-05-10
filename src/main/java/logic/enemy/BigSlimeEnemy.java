package logic.enemy;

/**
 * Tankier slime variant with higher health and lower speed.
 */
public final class BigSlimeEnemy extends Enemy {
    /** Base health of the big slime enemy. */
    private static final int MAX_HEALTH = 180;
    /** Movement speed of the big slime enemy. */
    private static final double SPEED = 0.8;
    /** Money rewarded when the big slime enemy is killed. */
    private static final int REWARD_MONEY = 20;
    /** Asset path for the big slime enemy sprite. */
    private static final String SPRITE = "Enemies/spr_big_slime.png";
    /** Damage dealt to the base if the big slime enemy reaches the end. */
    private static final int BASE_DAMAGE = 2;

    /**
     * Creates a big slime enemy instance.
     */
    public BigSlimeEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
