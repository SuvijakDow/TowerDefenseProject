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
    protected static final int DEFAULT_UPGRADE_COST = 100;
    protected static final int DEFAULT_MAX_LEVEL = 3;
    protected static final int MIN_FIRE_COOLDOWN = 10;
    private static final double UPGRADE_RANGE_BONUS = 10.0;

    protected int damage;
    protected double range;
    /** Fire interval in game ticks. */
    protected int fireCooldown;
    /** Ticks until next shot; 0 = ready. */
    protected int currentCooldown;
    protected double x;
    protected double y;
    protected int cost;
    protected String spriteName;
    /** Tile row where placed; {@code -1} if not grid-placed (e.g. legacy/tests). */
    protected int gridRow = -1;
    /** Tile column where placed; {@code -1} if not grid-placed. */
    protected int gridCol = -1;
    protected int level = 1;
    protected int upgradeCost = DEFAULT_UPGRADE_COST;
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

    public int getDamage() {
        return damage;
    }

    public double getRange() {
        return range;
    }

    public int getFireCooldown() {
        return fireCooldown;
    }

    public int getCurrentCooldown() {
        return currentCooldown;
    }

    /** @deprecated Use {@link #getFireCooldown()} (tick-based). */
    @Deprecated
    public double getAttackCooldown() {
        return fireCooldown;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getCost() {
        return cost;
    }

    public String getSpriteName() {
        return spriteName;
    }

    public int getGridRow() {
        return gridRow;
    }

    public int getGridCol() {
        return gridCol;
    }

    public int getLevel() {
        return level;
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void setFireCooldown(int fireCooldown) {
        this.fireCooldown = fireCooldown;
    }

    public void setCurrentCooldown(int currentCooldown) {
        this.currentCooldown = currentCooldown;
    }

    /** @deprecated Use {@link #setFireCooldown(int)}. */
    @Deprecated
    public void setAttackCooldown(double attackCooldown) {
        this.fireCooldown = (int) Math.round(attackCooldown);
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setSpriteName(String spriteName) {
        this.spriteName = spriteName != null ? spriteName : "";
    }
}
