package logic.enemy;

import application.SoundManager;
import logic.interfaces.Damageable;
import logic.map.Waypoint;

import java.util.List;

/**
 * Base enemy model used by all enemy variants in the game.
 *
 * <p>An enemy tracks combat stats, world position, path-following state,
 * sprite/animation state, and hit-flash state.</p>
 */
public abstract class Enemy implements Damageable {
    /** Number of frames in the walking animation. */
    private static final int FRAME_COUNT = 4;
    /** Number of ticks before advancing the animation frame. */
    private static final int ANIM_TICK_THRESHOLD = 10;
    /** Distance threshold to consider a waypoint reached. */
    private static final double WAYPOINT_REACHED_DISTANCE = 0.1;
    /** Assumed delta time per frame for hit-flash logic. */
    private static final double ASSUMED_FRAME_SECONDS = 0.016;
    /** Duration of the hit-flash visual effect in seconds. */
    private static final double HIT_FLASH_DURATION = 0.08;

    /** Maximum health points. */
    protected int maxHealth;
    /** Current health points. */
    protected int hp;
    /** Movement speed. */
    protected double speed;
    /** Money rewarded upon death. */
    protected int rewardMoney;
    /** Damage dealt to the base upon reaching the end. */
    protected int damage;
    /** X-coordinate in the game world. */
    protected double x;
    /** Y-coordinate in the game world. */
    protected double y;
    /** Index of the current target waypoint. */
    protected int currentWaypointIndex;
    /** Path to the sprite asset. */
    protected String spriteName;
    /** Current frame index of the animation. */
    protected int currentFrame;
    /** Tick counter for animation timing. */
    protected int animTick;
    /** Remaining time for the hit-flash effect. */
    protected double hitTimer;
    /** Flag indicating if the enemy was recently hit. */
    protected boolean isHit;

    /**
     * Creates an enemy with the provided combat and rendering stats.
     *
     * @param maxHealth maximum and initial health
     * @param speed movement speed per update tick
     * @param rewardMoney money granted when the enemy dies
     * @param spriteName resource path for the enemy sprite
     * @param damage base damage dealt when reaching the base
     */
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

    /**
     * Advances animation and hit-flash state, then moves toward the current waypoint.
     *
     * @param waypoints ordered route waypoints; ignored when null/empty
     */
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

    /**
     * Moves this enemy toward a target waypoint.
     *
     * @param target waypoint to move toward
     */
    public void move(Waypoint target) {
        moveTowards(target);
    }

    /**
     * Internal movement helper that applies speed without overshooting.
     *
     * @param target waypoint to move toward
     */
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

    /**
     * Applies incoming damage and starts a short hit-flash effect.
     *
     * @param amount damage amount; ignored when non-positive
     */
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

    /**
     * Indicates whether this enemy has no health remaining.
     *
     * @return true when health is zero or below
     */
    public boolean isDead() {
        return hp <= 0;
    }

    /**
     * Gets the maximum health.
     *
     * @return the max health
     */
    public int getMaxHealth() { return maxHealth; }

    /**
     * Sets the maximum health.
     *
     * @param maxHealth the new max health
     */
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    /**
     * Gets the current health.
     *
     * @return the current health
     */
    public int getHp() { return hp; }

    /**
     * Sets the current health.
     *
     * @param hp the new health
     */
    public void setHp(int hp) { this.hp = hp; }

    /**
     * Gets the current health (alias for getHp).
     *
     * @return the current health
     */
    public int getCurrentHealth() { return hp; }

    /**
     * Sets the current health (alias for setHp).
     *
     * @param currentHealth the new health
     */
    public void setCurrentHealth(int currentHealth) { this.hp = currentHealth; }

    /**
     * Gets the movement speed.
     *
     * @return the movement speed
     */
    public double getSpeed() { return speed; }

    /**
     * Sets the movement speed.
     *
     * @param speed the new speed
     */
    public void setSpeed(double speed) { this.speed = speed; }

    /**
     * Gets the money rewarded upon death.
     *
     * @return the reward money
     */
    public int getRewardMoney() { return rewardMoney; }

    /**
     * Sets the money rewarded upon death.
     *
     * @param rewardMoney the reward amount
     */
    public void setRewardMoney(int rewardMoney) { this.rewardMoney = rewardMoney; }

    /**
     * Gets the X coordinate.
     *
     * @return the X coordinate
     */
    public double getX() { return x; }

    /**
     * Sets the X coordinate.
     *
     * @param x the new X coordinate
     */
    public void setX(double x) { this.x = x; }

    /**
     * Gets the Y coordinate.
     *
     * @return the Y coordinate
     */
    public double getY() { return y; }

    /**
     * Sets the Y coordinate.
     *
     * @param y the new Y coordinate
     */
    public void setY(double y) { this.y = y; }

    /**
     * Gets the current waypoint index.
     *
     * @return the current waypoint index
     */
    public int getCurrentWaypointIndex() { return currentWaypointIndex; }

    /**
     * Sets the current waypoint index.
     *
     * @param currentWaypointIndex the new waypoint index
     */
    public void setCurrentWaypointIndex(int currentWaypointIndex) { this.currentWaypointIndex = currentWaypointIndex; }

    /**
     * Gets the sprite asset path.
     *
     * @return the sprite name
     */
    public String getSpriteName() { return spriteName; }

    /**
     * Sets the sprite asset path.
     *
     * @param spriteName the new sprite name
     */
    public void setSpriteName(String spriteName) { this.spriteName = spriteName != null ? spriteName : ""; }

    /**
     * Gets the current animation frame index.
     *
     * @return the current frame index
     */
    public int getCurrentFrame() { return currentFrame; }

    /**
     * Sets the current animation frame index, clamped to the total frame count.
     *
     * @param currentFrame the new frame index
     */
    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = Math.floorMod(currentFrame, FRAME_COUNT);
    }

    /**
     * Gets the animation tick counter.
     *
     * @return the animation tick
     */
    public int getAnimTick() { return animTick; }

    /**
     * Sets the animation tick counter.
     *
     * @param animTick the new tick value
     */
    public void setAnimTick(int animTick) { this.animTick = animTick; }

    /**
     * Gets the base damage dealt.
     *
     * @return the base damage
     */
    public int getDamage() { return damage; }

    /**
     * Sets the base damage dealt.
     *
     * @param damage the new damage amount
     */
    public void setDamage(int damage) { this.damage = damage; }

    /**
     * Checks if the enemy is currently in the hit-flash state.
     *
     * @return {@code true} if hit, {@code false} otherwise
     */
    public boolean isHit() { return isHit; }
}
