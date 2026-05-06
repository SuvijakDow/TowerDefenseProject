package logic.enemy;

public final class SlimeEnemy extends Enemy {
    private static final int MAX_HEALTH = 100;
    private static final double SPEED = 1.0;
    private static final int REWARD_MONEY = 10;
    private static final String SPRITE = "Enemies/spr_normal_slime.png";
    private static final int BASE_DAMAGE = 1;

    public SlimeEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
