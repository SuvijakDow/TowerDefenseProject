package logic.enemy;

/**
 * Fast and fragile bat enemy used for quick path pressure.
 */
public final class BatEnemy extends Enemy {
    /** Base health of the bat enemy. */
    private static final int MAX_HEALTH = 50;
    /** Movement speed of the bat enemy. */
    private static final double SPEED = 2.5;
    /** Money rewarded when the bat enemy is killed. */
    private static final int REWARD_MONEY = 15;
    /** Asset path for the bat enemy sprite. */
    private static final String SPRITE = "Enemies/spr_bat.png";
    /** Damage dealt to the base if the bat enemy reaches the end. */
    private static final int BASE_DAMAGE = 1;

    /**
     * Creates a bat enemy instance.
     */
    public BatEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
