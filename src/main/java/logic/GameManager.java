package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Central game controller for enemy/tower updates and core economy/base state.
public class GameManager {
    private static final int DEFAULT_PLAYER_MONEY = 500;
    private static final int DEFAULT_BASE_HEALTH = 100;
    private static final double DEFAULT_MAP_END_X = 800.0;
    private static final int DEFAULT_KILL_REWARD = 25;

    private final List<Enemy> activeEnemies;
    private final List<Tower> activeTowers;
    private int playerMoney;
    private int baseHealth;
    private final double MAP_END_X;
    private final int killReward;

    // Creates a game manager with default economy, base health, and map end.
    public GameManager() {
        this(DEFAULT_PLAYER_MONEY, DEFAULT_BASE_HEALTH, DEFAULT_MAP_END_X, DEFAULT_KILL_REWARD);
    }

    // Creates a game manager with custom money, base health, and map end.
    public GameManager(int playerMoney, int baseHealth, double mapEndX) {
        this(playerMoney, baseHealth, mapEndX, DEFAULT_KILL_REWARD);
    }

    // Creates a game manager with fully custom initial values.
    public GameManager(int playerMoney, int baseHealth, double mapEndX, int killReward) {
        requireNonNegative(playerMoney, "playerMoney");
        requireNonNegative(baseHealth, "baseHealth");
        requirePositive(mapEndX, "mapEndX");
        requireNonNegative(killReward, "killReward");

        this.activeEnemies = new ArrayList<>();
        this.activeTowers = new ArrayList<>();
        this.playerMoney = playerMoney;
        this.baseHealth = baseHealth;
        this.MAP_END_X = mapEndX;
        this.killReward = killReward;
    }

    // Spawns and tracks a new active enemy.
    public void spawnEnemy(Enemy e) {
        if (e == null) {
            throw new IllegalArgumentException("Enemy must not be null.");
        }
        activeEnemies.add(e);
    }

    // Places a tower by spending money; throws when funds are insufficient.
    public void placeTower(Tower t, int cost) {
        if (t == null) {
            throw new IllegalArgumentException("Tower must not be null.");
        }
        requireNonNegative(cost, "cost");
        if (playerMoney < cost) {
            throw new IllegalArgumentException("Insufficient money to place tower.");
        }
        playerMoney -= cost;
        activeTowers.add(t);
    }

    // Runs one game tick: move enemies, attack, process base leaks, then process kills and rewards.
    public void update() {
        List<Enemy> enemiesToAdd = new ArrayList<>();
        Set<Enemy> enemiesToRemove = new HashSet<>();

        for (Enemy enemy : activeEnemies) {
            enemy.move();
        }

        for (Tower tower : activeTowers) {
            tower.attack(activeEnemies);
        }

        for (Enemy enemy : activeEnemies) {
            if (enemy.getX() >= MAP_END_X) {
                baseHealth = Math.max(0, baseHealth - 1);
                enemiesToRemove.add(enemy);
            }
        }

        for (Enemy enemy : activeEnemies) {
            if (!enemy.isAlive()) {
                if (enemy instanceof BossEnemy bossEnemy) {
                    enemiesToAdd.addAll(bossEnemy.spawnChildrenOnDeath());
                }
                playerMoney += killReward;
                enemiesToRemove.add(enemy);
            }
        }

        if (!enemiesToRemove.isEmpty()) {
            activeEnemies.removeAll(enemiesToRemove);
        }
        if (!enemiesToAdd.isEmpty()) {
            activeEnemies.addAll(enemiesToAdd);
        }
    }

    // Returns active enemies as a read-only view.
    public List<Enemy> getActiveEnemies() {
        return Collections.unmodifiableList(activeEnemies);
    }

    // Returns active towers as a read-only view.
    public List<Tower> getActiveTowers() {
        return Collections.unmodifiableList(activeTowers);
    }

    // Returns current player money.
    public int getPlayerMoney() {
        return playerMoney;
    }

    // Returns current base health.
    public int getBaseHealth() {
        return baseHealth;
    }

    // Returns the x-position that represents the base endpoint.
    public double getMapEndX() {
        return MAP_END_X;
    }

    // Returns true when the base has no health left.
    public boolean isGameOver() {
        return baseHealth <= 0;
    }

    // Validates non-negative integer values.
    private static void requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " must be >= 0.");
        }
    }

    // Validates positive floating-point values.
    private static void requirePositive(double value, String fieldName) {
        if (value <= 0.0) {
            throw new IllegalArgumentException(fieldName + " must be > 0.");
        }
    }
}
