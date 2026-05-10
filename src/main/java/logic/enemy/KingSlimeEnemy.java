package logic.enemy;

/**
 * Boss-like slime enemy with very high health and heavy base damage.
 */
public final class KingSlimeEnemy extends Enemy {
    /** Base health of the king slime enemy. */
    private static final int MAX_HEALTH = 320;
    /** Movement speed of the king slime enemy. */
    private static final double SPEED = 2.0;
    /** Money rewarded when the king slime enemy is killed. */
    private static final int REWARD_MONEY = 45;
    /** Asset path for the king slime enemy sprite. */
    private static final String SPRITE = "Enemies/spr_king_slime.png";
    /** Damage dealt to the base if the king slime enemy reaches the end. */
    private static final int BASE_DAMAGE = 50;

    /**
     * Creates a king slime enemy instance.
     */
    public KingSlimeEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
