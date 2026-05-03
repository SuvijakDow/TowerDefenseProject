package logic.enemy;

import java.util.List;

import logic.interfaces.Damageable;
import logic.map.Waypoint;

public abstract class Enemy implements Damageable {
    protected int maxHealth;
    protected int currentHealth;
    protected double speed;
    protected int rewardMoney;
    protected double x;
    protected double y;
    protected int currentWaypointIndex;
    protected boolean isFlying;
    protected String spriteName;
    protected int currentFrame;
    protected int animTick;

    private static final int ANIM_TICK_THRESHOLD = 10;

    public Enemy(int maxHealth, double speed, int rewardMoney, boolean isFlying, String spriteName) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.speed = speed;
        this.rewardMoney = rewardMoney;
        this.isFlying = isFlying;
        this.spriteName = spriteName != null ? spriteName : "";
        this.currentWaypointIndex = 0;
        this.currentFrame = 0;
        this.animTick = 0;
    }

    /**
     * Advances walk animation and moves toward {@link #currentWaypointIndex} target when path exists.
     */
    public void update(List<Waypoint> waypoints) {
        animTick++;
        if (animTick > ANIM_TICK_THRESHOLD) {
            animTick = 0;
            currentFrame = (currentFrame + 1) % 4;
        }

        if (waypoints == null || waypoints.isEmpty()) {
            return;
        }
        if (currentWaypointIndex >= waypoints.size()) {
            return;
        }

        Waypoint target = waypoints.get(currentWaypointIndex);
        move(target);

        double dx = target.getX() - x;
        double dy = target.getY() - y;
        if (Math.sqrt(dx * dx + dy * dy) <= 0.1) {
            currentWaypointIndex++;
        }
    }

    public abstract void move(Waypoint target);

    @Override
    public void takeDamage(int amount) {
        this.currentHealth -= amount;
        if (this.currentHealth < 0) {
            this.currentHealth = 0;
        }
    }

    public boolean isDead() { return currentHealth <= 0; }

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

    public String getSpriteName() { return spriteName; }
    public void setSpriteName(String spriteName) { this.spriteName = spriteName != null ? spriteName : ""; }

    public int getCurrentFrame() { return currentFrame; }
    public void setCurrentFrame(int currentFrame) { this.currentFrame = currentFrame % 4; }

    public int getAnimTick() { return animTick; }
    public void setAnimTick(int animTick) { this.animTick = animTick; }
}
