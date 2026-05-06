package logic.enemy;

public final class DemonEnemy extends Enemy {
    private static final int MAX_HEALTH = 250;
    private static final double SPEED = 1.5;
    private static final int REWARD_MONEY = 35;
    private static final boolean IS_FLYING = true;
    private static final String SPRITE = "Enemies/spr_demon.png";
    private static final int BASE_DAMAGE = 5;

    public DemonEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, IS_FLYING, SPRITE, BASE_DAMAGE);
    }
}
