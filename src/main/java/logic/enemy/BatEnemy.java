package logic.enemy;

/**
 * Fast and fragile bat enemy used for quick path pressure.
 */
public final class BatEnemy extends Enemy {
    private static final int MAX_HEALTH = 50;
    private static final double SPEED = 2.5;
    private static final int REWARD_MONEY = 15;
    private static final String SPRITE = "Enemies/spr_bat.png";
    private static final int BASE_DAMAGE = 1;

    /**
     * Creates a bat enemy instance.
     */
    public BatEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
