package logic;

public abstract class Enemy implements Damageable {
    protected int maxHealth;
    protected int currentHealth;
    protected double speed;
    protected int rewardMoney;
    protected double x;
    protected double y;
    protected int currentWaypointIndex;
    protected boolean isFlying;

    public Enemy(int maxHealth, double speed, int rewardMoney, boolean isFlying) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.speed = speed;
        this.rewardMoney = rewardMoney;
        this.isFlying = isFlying;
        this.currentWaypointIndex = 0;
    }

    public abstract void move(Waypoint target);

    @Override
    public void takeDamage(int amount) {
        this.currentHealth -= amount;
        if (this.currentHealth < 0) {
            this.currentHealth = 0;
        }
    }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public int getCurrentHealth() { return currentHealth; }
    public void setCurrentHealth(int currentHealth) { this.currentHealth = currentHealth; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public int getRewardMoney() { return rewardMoney; }
    public void setRewardMoney(int rewardMoney) { this.rewardMoney = rewardMoney; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public int getCurrentWaypointIndex() { return currentWaypointIndex; }
    public void setCurrentWaypointIndex(int currentWaypointIndex) { this.currentWaypointIndex = currentWaypointIndex; }
    public boolean isFlying() { return isFlying; }
    public void setFlying(boolean flying) { this.isFlying = flying; }
}
