package logic.enemy;

public final class KingSlimeEnemy extends Enemy {
    private static final int MAX_HEALTH = 320;
    private static final double SPEED = 2.0;
    private static final int REWARD_MONEY = 45;
    private static final boolean IS_FLYING = false;
    private static final String SPRITE = "Enemies/spr_king_slime.png";
    private static final int BASE_DAMAGE = 50;

    public KingSlimeEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, IS_FLYING, SPRITE, BASE_DAMAGE);
    }
}
