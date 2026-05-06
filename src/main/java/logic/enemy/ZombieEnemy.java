package logic.enemy;

public final class ZombieEnemy extends Enemy {
    private static final int MAX_HEALTH = 170;
    private static final double SPEED = 0.9;
    private static final int REWARD_MONEY = 22;
    private static final String SPRITE = "Enemies/spr_zombie.png";
    private static final int BASE_DAMAGE = 4;

    public ZombieEnemy() {
        super(MAX_HEALTH, SPEED, REWARD_MONEY, SPRITE, BASE_DAMAGE);
    }
}
