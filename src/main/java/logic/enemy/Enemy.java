package logic.enemy;

import application.SoundManager;
import logic.interfaces.Damageable;
import logic.map.Waypoint;

import java.util.List;

public class Enemy implements Damageable {
    private static final int FRAME_COUNT = 4;
    private static final int ANIM_TICK_THRESHOLD = 10;
    private static final double WAYPOINT_REACHED_DISTANCE = 0.1;
    private static final double ASSUMED_FRAME_SECONDS = 0.016; // Keep legacy hit-flash timing.
    private static final double HIT_FLASH_DURATION = 0.08;

    protected int maxHealth;
    protected int hp;
    protected double speed;
    protected int rewardMoney;
    protected int damage;
    protected double x;
    protected double y;
    protected int currentWaypointIndex;
    protected String spriteName;
    protected int currentFrame;
    protected int animTick;
    protected double hitTimer;
    protected boolean isHit;

    public Enemy(int maxHealth, double speed, int rewardMoney, String spriteName, int damage) {
        this.maxHealth = maxHealth;
        this.hp = maxHealth;
        this.speed = speed;
        this.rewardMoney = rewardMoney;
        this.damage = damage;
        this.spriteName = spriteName != null ? spriteName : "";
        this.currentWaypointIndex = 0;
        this.currentFrame = 0;
        this.animTick = 0;
        this.hitTimer = 0.0;
        this.isHit = false;
    }

    public void update(List<Waypoint> waypoints) {
        advanceAnimation();
        updateHitFlashTimer();

        if (waypoints == null || waypoints.isEmpty() || currentWaypointIndex >= waypoints.size()) {
            return;
        }

        Waypoint target = waypoints.get(currentWaypointIndex);
        move(target);
        if (distanceTo(target.getX(), target.getY()) <= WAYPOINT_REACHED_DISTANCE) {
            currentWaypointIndex++;
        }
    }

    public void move(Waypoint target) {
        moveTowards(target);
    }

    protected void moveTowards(Waypoint target) {
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double distance = Math.hypot(dx, dy);
        if (distance <= 0) {
            return;
        }

        double moveX = (dx / distance) * speed;
        double moveY = (dy / distance) * speed;

        x = Math.abs(moveX) > Math.abs(dx) ? target.getX() : x + moveX;
        y = Math.abs(moveY) > Math.abs(dy) ? target.getY() : y + moveY;
    }

    @Override
    public void takeDamage(int amount) {
        if (amount <= 0) {
            return;
        }

        hp = Math.max(0, hp - amount);
        SoundManager.playEnemyIsAttackedSfx();
        isHit = true;
        hitTimer = HIT_FLASH_DURATION;
    }

    private void advanceAnimation() {
        animTick++;
        if (animTick <= ANIM_TICK_THRESHOLD) {
            return;
        }
        animTick = 0;
        currentFrame = (currentFrame + 1) % FRAME_COUNT;
    }

    private void updateHitFlashTimer() {
        if (!isHit) {
            return;
        }
        hitTimer -= ASSUMED_FRAME_SECONDS;
        if (hitTimer <= 0) {
            isHit = false;
            hitTimer = 0.0;
        }
    }

    private double distanceTo(double targetX, double targetY) {
        return Math.hypot(targetX - x, targetY - y);
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getCurrentHealth() { return hp; }
    public void setCurrentHealth(int currentHealth) { this.hp = currentHealth; }
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
    public String getSpriteName() { return spriteName; }
    public void setSpriteName(String spriteName) { this.spriteName = spriteName != null ? spriteName : ""; }
    public int getCurrentFrame() { return currentFrame; }
    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = Math.floorMod(currentFrame, FRAME_COUNT);
    }
    public int getAnimTick() { return animTick; }
    public void setAnimTick(int animTick) { this.animTick = animTick; }
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
    public boolean isHit() { return isHit; }
}
