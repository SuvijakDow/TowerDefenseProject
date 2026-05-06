package logic.enemy;

public final class GoblinEnemy extends Enemy {
    private static final int MAX_HEALTH = 90;
    private static final double SPEED = 1.8;
    private static final int REWARD_MONEY = 18;
    private static final String SPRITE = "Enemies/spr_goblin.png";
    private static final int BASE_DAMAGE = 2;

    public GoblinEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
