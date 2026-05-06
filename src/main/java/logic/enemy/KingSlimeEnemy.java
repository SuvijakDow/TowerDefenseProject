package logic.enemy;

/**
 * Boss-like slime enemy with very high health and heavy base damage.
 */
public final class KingSlimeEnemy extends Enemy {
    private static final int MAX_HEALTH = 320;
    private static final double SPEED = 2.0;
    private static final int REWARD_MONEY = 45;
    private static final String SPRITE = "Enemies/spr_king_slime.png";
    private static final int BASE_DAMAGE = 50;

    /**
     * Creates a king slime enemy instance.
     */
    public KingSlimeEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
