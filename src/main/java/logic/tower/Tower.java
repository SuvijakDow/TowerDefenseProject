package logic.tower;

import logic.enemy.Enemy;
import logic.interfaces.Upgradable;
import logic.map.GameMap;

import java.util.List;

/**
 * Base tower type for all combat towers.
 *
 * <p>A tower tracks combat stats, placement state, upgrade state, and controls
 * projectile spawning against in-range enemies.</p>
 */
public abstract class Tower implements Upgradable {
    /** Default cost to upgrade the tower. */
    protected static final int DEFAULT_UPGRADE_COST = 100;
    /** Default maximum upgrade level. */
    protected static final int DEFAULT_MAX_LEVEL = 3;
    /** Minimum allowed fire cooldown limit. */
    protected static final int MIN_FIRE_COOLDOWN = 10;
    /** Range increment applied per upgrade. */
    private static final double UPGRADE_RANGE_BONUS = 10.0;

    /** Base projectile damage. */
    protected int damage;
    /** Attack range in world units. */
    protected double range;
    /** Fire interval in game ticks. */
    protected int fireCooldown;
    /** Ticks until next shot; 0 = ready. */
    protected int currentCooldown;
    /** X coordinate in world space. */
    protected double x;
    /** Y coordinate in world space. */
    protected double y;
    /** Placement cost of the tower. */
    protected int cost;
    /** Sprite resource path for the tower. */
    protected String spriteName;
    /** Tile row where placed; {@code -1} if not grid-placed (e.g. legacy/tests). */
    protected int gridRow = -1;
    /** Tile column where placed; {@code -1} if not grid-placed. */
    protected int gridCol = -1;
    /** Current upgrade level. */
    protected int level = 1;
    /** Cost of the next upgrade. */
    protected int upgradeCost = DEFAULT_UPGRADE_COST;
    /** Maximum upgrade level. */
    protected int maxLevel = DEFAULT_MAX_LEVEL;

    /**
     * Constructs a tower with the given base stats and sprite.
     *
     * @param damage base projectile damage
     * @param range attack range in world units
     * @param fireCooldown cooldown between attacks in ticks
     * @param cost placement cost
     * @param spriteName sprite resource path
     */
    public Tower(int damage, double range, int fireCooldown, int cost, String spriteName) {
        this.damage = damage;
        this.range = range;
        this.fireCooldown = fireCooldown;
        this.currentCooldown = 0;
        this.cost = cost;
        this.spriteName = spriteName != null ? spriteName : "";
    }

    /**
     * Updates tower combat for one tick.
     *
     * <p>When ready, the tower fires one projectile at the closest living enemy
     * inside range.</p>
     *
     * @param enemies currently active enemies
     * @param activeProjectiles projectile output collection
     */
    public void update(List<Enemy> enemies, List<Projectile> activeProjectiles) {
        updateProjectileAttack(enemies, activeProjectiles, Projectile.DEFAULT_SPRITE);
    }

    /**
     * Shared projectile-attack implementation used by tower subclasses.
     *
     * @param enemies currently active enemies
     * @param activeProjectiles projectile output collection
     * @param projectileSprite sprite resource path for spawned projectiles
     */
    protected void updateProjectileAttack(
            List<Enemy> enemies,
            List<Projectile> activeProjectiles,
            String projectileSprite
    ) {
        tickCooldown();
        if (currentCooldown > 0 || activeProjectiles == null) {
            return;
        }

        Enemy target = findClosestEnemyInRange(enemies);
        if (target == null) {
            return;
        }

        double[] spawnPosition = getProjectileSpawnPosition();
        activeProjectiles.add(
                new Projectile(
                        spawnPosition[0],
                        spawnPosition[1],
                        Projectile.DEFAULT_SPEED,
                        damage,
                        target,
                        projectileSprite
                )
        );
        currentCooldown = fireCooldown;
    }

    /**
     * Finds the closest living enemy inside this tower's range.
     *
     * @param enemies currently active enemies
     * @return closest in-range living enemy, or {@code null} if none
     */
    protected Enemy findClosestEnemyInRange(List<Enemy> enemies) {
        if (enemies == null || enemies.isEmpty()) {
            return null;
        }

        Enemy closestEnemy = null;
        double closestDistance = Double.MAX_VALUE;
        for (Enemy enemy : enemies) {
            if (enemy.isDead()) {
                continue;
            }

            double distance = Math.hypot(enemy.getX() - x, enemy.getY() - y);
            if (distance <= range && distance < closestDistance) {
                closestDistance = distance;
                closestEnemy = enemy;
            }
        }
        return closestEnemy;
    }

    /**
     * Upgrades tower stats if the tower has not reached its max level.
     */
    @Override
    public void upgrade() {
        if (!canUpgrade()) {
            return;
        }

        level++;
        damage = Math.max(damage + 1, (int) Math.ceil(damage * 1.2));
        range += UPGRADE_RANGE_BONUS;
        fireCooldown = Math.max(MIN_FIRE_COOLDOWN, fireCooldown - 2);
        upgradeCost = Math.max(upgradeCost + 1, (int) Math.ceil(upgradeCost * 1.5));
    }

    /**
     * Decrements the fire cooldown if the tower is on cooldown.
     */
    private void tickCooldown() {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }

    /**
     * Checks whether a single enemy is currently in range.
     *
     * @param enemy enemy to evaluate
     * @return true when enemy is non-null and within range
     */
    protected boolean isEnemyInRange(Enemy enemy) {
        return enemy != null && Math.hypot(enemy.getX() - x, enemy.getY() - y) <= range;
    }

    /**
     * Computes the spawn position for projectiles based on tower coordinates.
     *
     * @return a double array containing [x, y] spawn coordinates
     */
    private double[] getProjectileSpawnPosition() {
        int tileSize = GameMap.PATH_TILE_PIXEL_SIZE;
        return new double[]{
                x + (tileSize * 0.5),
                y + (tileSize * 0.2)
        };
    }

    /**
     * Indicates whether this tower can still be upgraded.
     *
     * @return true when current level is below max level
     */
    public boolean canUpgrade() {
        return level < maxLevel;
    }

    /**
     * Stores the tile coordinates where this tower is placed.
     *
     * @param row grid row
     * @param col grid column
     */
    public void setPlacementTile(int row, int col) {
        this.gridRow = row;
        this.gridCol = col;
    }

    /**
     * Gets the base damage.
     *
     * @return the damage amount
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Gets the attack range.
     *
     * @return the range in world units
     */
    public double getRange() {
        return range;
    }

    /**
     * Gets the interval between shots.
     *
     * @return the fire cooldown in ticks
     */
    public int getFireCooldown() {
        return fireCooldown;
    }

    /**
     * Gets the remaining ticks until the next shot.
     *
     * @return the current cooldown
     */
    public int getCurrentCooldown() {
        return currentCooldown;
    }

    /** @deprecated Use {@link #getFireCooldown()} (tick-based). */
    @Deprecated
    public double getAttackCooldown() {
        return fireCooldown;
    }

    /**
     * Gets the X coordinate.
     *
     * @return the X coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the Y coordinate.
     *
     * @return the Y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the placement cost.
     *
     * @return the cost amount
     */
    public int getCost() {
        return cost;
    }

    /**
     * Gets the sprite resource path.
     *
     * @return the sprite name
     */
    public String getSpriteName() {
        return spriteName;
    }

    /**
     * Gets the grid row where placed.
     *
     * @return the row index
     */
    public int getGridRow() {
        return gridRow;
    }

    /**
     * Gets the grid column where placed.
     *
     * @return the column index
     */
    public int getGridCol() {
        return gridCol;
    }

    /**
     * Gets the current upgrade level.
     *
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets the cost to upgrade to the next level.
     *
     * @return the upgrade cost
     */
    public int getUpgradeCost() {
        return upgradeCost;
    }

    /**
     * Gets the maximum possible upgrade level.
     *
     * @return the max level
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Sets the base damage.
     *
     * @param damage the damage to set
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Sets the attack range.
     *
     * @param range the range to set
     */
    public void setRange(double range) {
        this.range = range;
    }

    /**
     * Sets the fire cooldown.
     *
     * @param fireCooldown the cooldown to set in ticks
     */
    public void setFireCooldown(int fireCooldown) {
        this.fireCooldown = fireCooldown;
    }

    /**
     * Sets the current cooldown state.
     *
     * @param currentCooldown the current cooldown in ticks
     */
    public void setCurrentCooldown(int currentCooldown) {
        this.currentCooldown = currentCooldown;
    }

    /** @deprecated Use {@link #setFireCooldown(int)}. */
    @Deprecated
    public void setAttackCooldown(double attackCooldown) {
        this.fireCooldown = (int) Math.round(attackCooldown);
    }

    /**
     * Sets the X coordinate.
     *
     * @param x the coordinate to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the Y coordinate.
     *
     * @param y the coordinate to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Sets the placement cost.
     *
     * @param cost the cost to set
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * Sets the sprite resource path.
     *
     * @param spriteName the path to set
     */
    public void setSpriteName(String spriteName) {
        this.spriteName = spriteName != null ? spriteName : "";
    }
}
