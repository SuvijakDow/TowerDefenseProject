package logic.enemy;

/**
 * Tankier slime variant with higher health and lower speed.
 */
public final class BigSlimeEnemy extends Enemy {
    private static final int MAX_HEALTH = 180;
    private static final double SPEED = 0.8;
    private static final int REWARD_MONEY = 20;
    private static final String SPRITE = "Enemies/spr_big_slime.png";
    private static final int BASE_DAMAGE = 2;

    /**
     * Creates a big slime enemy instance.
     */
    public BigSlimeEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
