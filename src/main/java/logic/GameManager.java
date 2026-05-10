package logic;

import application.GameView;
import application.SoundManager;
import javafx.scene.paint.Color;
import logic.enemy.BatEnemy;
import logic.enemy.BigSlimeEnemy;
import logic.enemy.DemonEnemy;
import logic.enemy.Enemy;
import logic.enemy.GoblinEnemy;
import logic.enemy.KingSlimeEnemy;
import logic.enemy.SkeletonEnemy;
import logic.enemy.SlimeEnemy;
import logic.enemy.ZombieEnemy;
import logic.map.GameMap;
import logic.map.Waypoint;
import logic.tower.ArcherTower;
import logic.tower.CannonTower;
import logic.tower.CrossbowTower;
import logic.tower.IceWizardTower;
import logic.tower.LightningWizardTower;
import logic.tower.PoisonWizardTower;
import logic.tower.Projectile;
import logic.tower.Tower;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Central gameplay coordinator for combat, economy, spawning, and win/lose state.
 *
 * <p>The manager owns active entities and advances the entire game loop in { #update(double)}.</p>
 */
public class GameManager {
    /** Available tower types that can be selected for placement. */
    public enum TowerType { ARCHER, CANNON, CROSSBOW, ICE_WIZARD, LIGHTNING_WIZARD, POISON_WIZARD }

    /** Initial money given to the player at the start of a match. */
    private static final int INITIAL_PLAYER_MONEY = 500;
    /** Initial health of the player's base. */
    private static final int INITIAL_BASE_HEALTH = 100;
    /** The tower type selected by default. */
    private static final TowerType DEFAULT_SELECTED_TOWER = TowerType.ARCHER;
    /** The total duration of the match in seconds. */
    private static final double GAME_DURATION_SECONDS = 180.0;
    /** Maximum number of floating damage texts on screen. */
    private static final int MAX_DAMAGE_TEXTS = 50;
    /** Fallback default tower range if not available. */
    private static final double DEFAULT_TOWER_RANGE = 100.0;

    /** Minimum remaining time to be considered the early phase. */
    private static final double PHASE_EARLY_MIN_TIME = 120.0;
    /** Minimum remaining time to be considered the mid phase. */
    private static final double PHASE_MID_MIN_TIME = 60.0;
    /** Spawning interval during the early phase. */
    private static final double PHASE_EARLY_INTERVAL = 1.2;
    /** Spawning interval during the mid phase. */
    private static final double PHASE_MID_INTERVAL = 0.8;
    /** Spawning interval during the late phase. */
    private static final double PHASE_LATE_INTERVAL = 0.5;

    /** Pool of enemies that can spawn during the early phase. */
    private static final List<Supplier<Enemy>> EARLY_ENEMIES = List.of(
            SlimeEnemy::new,
            GoblinEnemy::new,
            SkeletonEnemy::new
    );
    /** Pool of enemies that can spawn during the mid phase. */
    private static final List<Supplier<Enemy>> MID_ENEMIES = List.of(
            BigSlimeEnemy::new,
            DemonEnemy::new,
            ZombieEnemy::new
    );
    /** Pool of enemies that can spawn during the late phase. */
    private static final List<Supplier<Enemy>> LATE_ENEMIES = List.of(
            DemonEnemy::new,
            KingSlimeEnemy::new,
            BatEnemy::new
    );

    /** Factory mapping for creating towers based on their type. */
    private static final Map<TowerType, Supplier<Tower>> TOWER_FACTORIES = createTowerFactories();

    /** Random number generator for spawning and other events. */
    private final Random random = new Random();

    /** The current active map. */
    private GameMap currentMap;
    /** The list of currently alive enemies on the map. */
    private final List<Enemy> activeEnemies;
    /** The list of towers currently placed on the map. */
    private final List<Tower> activeTowers;
    /** The list of active projectiles fired by towers. */
    private final List<Projectile> activeProjectiles;
    /** The list of active floating damage texts. */
    private final List<DamageText> activeDamageTexts;

    /** The current amount of money the player has. */
    private int playerMoney;
    /** The current health of the player's base. */
    private int baseHealth;
    /** Flag indicating if the game has ended in defeat. */
    private boolean isGameOver;
    /** The currently selected tower type in the shop. */
    private TowerType selectedTowerType;
    /** The game view component for UI interactions. */
    private final GameView gameView;

    /** Time remaining in seconds before victory. */
    private double timeRemaining = GAME_DURATION_SECONDS;
    /** Flag indicating if the player has won the game. */
    private boolean isVictory = false;
    /** Cooldown timer for spawning the next enemy. */
    private double spawnCooldown = 0.0;

    /**
     * Creates a game manager for a map and optional view binding.
     *
     * @param map current game map
     * @param gameView view callback target, may be null for tests
     */
    public GameManager(GameMap map, GameView gameView) {
        this.currentMap = map;
        this.gameView = gameView;
        this.activeEnemies = new ArrayList<>();
        this.activeTowers = new ArrayList<>();
        this.activeProjectiles = new ArrayList<>();
        this.activeDamageTexts = new ArrayList<>();
        this.playerMoney = INITIAL_PLAYER_MONEY;
        this.baseHealth = INITIAL_BASE_HEALTH;
        this.isGameOver = false;
        this.selectedTowerType = DEFAULT_SELECTED_TOWER;
    }

    /**
     * Places a pre-created tower when the player can afford it.
     *
     * <p>This is a legacy/test-oriented API and does not validate map tiles.</p>
     *
     *  tower tower instance to add
     *  true when placement succeeds
     */
    public boolean placeTower(Tower tower) {
        if (tower == null || playerMoney < tower.getCost()) {
            return false;
        }
        playerMoney -= tower.getCost();
        activeTowers.add(tower);
        return true;
    }

    /**
     * Places a selected tower at tile {@code (row, col)} if buildable, affordable,
     * and tile not already occupied by another placed tower.
     */
    public boolean placeTower(int row, int col) {
        if (!isPlacementRequestValid(row, col)) {
            return false;
        }

        Tower tower = createTowerFromType(selectedTowerType);
        if (tower == null) {
            return false;
        }
        if (playerMoney < tower.getCost()) {
            System.out.println("Not enough money! Need: " + tower.getCost() + ", Have: " + playerMoney);
            return false;
        }

        placeTowerAtTile(tower, row, col);
        playerMoney = Math.max(0, playerMoney - tower.getCost());
        activeTowers.add(tower);
        System.out.println("Placed " + selectedTowerType + " tower at (" + row + "," + col + "). Money remaining: " + playerMoney);
        return true;
    }

    /**
     * Spawns an enemy at the first path waypoint.
     *
     *  enemy enemy to spawn
     */
    public void spawnEnemy(Enemy enemy) {
        if (enemy == null || currentMap == null || currentMap.getPathWaypoints().isEmpty()) {
            return;
        }

        Waypoint start = currentMap.getPathWaypoints().getFirst();
        enemy.setX(start.getX());
        enemy.setY(start.getY());
        enemy.setCurrentWaypointIndex(0);
        activeEnemies.add(enemy);
    }

    /**
     * Advances one game-loop tick.
     *
     *  deltaTime elapsed time in seconds
     */
    public void update(double deltaTime) {
        if (isGameOver || isVictory) {
            return;
        }

        updateTimer(deltaTime);
        if (checkAndHandleVictory()) {
            return;
        }

        handleSpawning(deltaTime);
        List<Waypoint> waypoints = currentMap != null ? currentMap.getPathWaypoints() : List.of();
        updateEnemies(waypoints);
        if (isGameOver) {
            return;
        }

        updateTowers();
        updateProjectiles();
        updateDamageTexts(deltaTime);
        collectBountiesFromDeadEnemies();
    }

    /**
     * Decreases the remaining game time.
     *
     * @param deltaTime the elapsed time in seconds
     */
    private void updateTimer(double deltaTime) {
        if (timeRemaining <= 0) {
            return;
        }
        timeRemaining = Math.max(0.0, timeRemaining - deltaTime);
    }

    /**
     * Updates enemy positions and checks if they have reached the base.
     *
     * @param waypoints the path waypoints
     */
    private void updateEnemies(List<Waypoint> waypoints) {
        Iterator<Enemy> enemyIterator = activeEnemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update(waypoints);

            if (enemy.getCurrentWaypointIndex() >= waypoints.size()) {
                handleEnemyReachedBase(enemy, enemyIterator);
            }
        }
    }

    /**
     * Instructs towers to acquire targets and fire projectiles.
     */
    private void updateTowers() {
        for (Tower tower : activeTowers) {
            tower.update(activeEnemies, activeProjectiles);
        }
    }

    /**
     * Moves projectiles and applies damage when they hit their targets.
     */
    private void updateProjectiles() {
        Iterator<Projectile> projectileIterator = activeProjectiles.iterator();
        while (projectileIterator.hasNext()) {
            Projectile projectile = projectileIterator.next();
            if (!projectile.update()) {
                continue;
            }

            Enemy target = projectile.getTarget();
            if (target != null && !target.isDead()) {
                target.takeDamage(projectile.getDamage());
                createDamageText(
                        String.valueOf(projectile.getDamage()),
                        target.getX(),
                        target.getY(),
                        Color.WHITE
                );
            }
            projectileIterator.remove();
        }
    }

    /**
     * Animates floating damage texts and removes expired ones.
     *
     * @param deltaTime the elapsed time in seconds
     */
    private void updateDamageTexts(double deltaTime) {
        activeDamageTexts.removeIf(damageText -> damageText.update(deltaTime));
    }

    /**
     * Spawns new enemies periodically based on the current game phase.
     *
     * @param deltaTime the elapsed time in seconds
     */
    private void handleSpawning(double deltaTime) {
        if (timeRemaining <= 0) {
            return;
        }

        spawnCooldown -= deltaTime;
        if (spawnCooldown > 0) {
            return;
        }

        SpawnPlan spawnPlan = selectSpawnPlan();
        spawnEnemy(selectRandomEnemy(spawnPlan.enemyPool()));
        spawnCooldown = spawnPlan.spawnInterval();
    }

    /**
     * Applies damage to the base when an enemy completes the path.
     *
     * @param enemy the enemy that reached the base
     * @param enemyIterator iterator to safely remove the enemy
     */
    private void handleEnemyReachedBase(Enemy enemy, Iterator<Enemy> enemyIterator) {
        baseHealth = Math.max(0, baseHealth - enemy.getDamage());
        SoundManager.playCastleIsAttackedSfx();
        System.out.println("Enemy reached base! Base health: " + baseHealth);

        if (gameView != null) {
            gameView.playCastleHitEffect();
        }

        enemyIterator.remove();
        if (baseHealth <= 0) {
            isGameOver = true;
            System.out.println("Game Over! Base destroyed.");
        }
    }

    /**
     * Checks if victory conditions are met (time is up and no enemies remain).
     *
     * @return {@code true} if victory is achieved, {@code false} otherwise
     */
    private boolean checkAndHandleVictory() {
        if (timeRemaining > 0 || !activeEnemies.isEmpty() || baseHealth <= 0) {
            return false;
        }
        isVictory = true;
        System.out.println("Victory! You survived the siege!");
        return true;
    }

    /**
     * Awards money to the player for defeated enemies and removes them from the game.
     */
    private void collectBountiesFromDeadEnemies() {
        Iterator<Enemy> enemyIterator = activeEnemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (!enemy.isDead()) {
                continue;
            }
            playerMoney += enemy.getRewardMoney();
            System.out.println("Enemy defeated! Earned: " + enemy.getRewardMoney() + ", Total money: " + playerMoney);
            enemyIterator.remove();
        }
    }

    /**
     * Creates a new tower instance from a selected type.
     *
     * @param type tower type
     * @return new tower instance, or null when type is null/unsupported
     */
    public Tower createTowerFromType(TowerType type) {
        if (type == null) {
            return null;
        }
        Supplier<Tower> towerFactory = TOWER_FACTORIES.get(type);
        return towerFactory != null ? towerFactory.get() : null;
    }

    /**
     * Selects the appropriate spawn plan depending on the remaining game time.
     *
     * @return the selected {@link SpawnPlan}
     */
    private SpawnPlan selectSpawnPlan() {
        if (timeRemaining > PHASE_EARLY_MIN_TIME) {
            return new SpawnPlan(PHASE_EARLY_INTERVAL, EARLY_ENEMIES);
        }
        if (timeRemaining > PHASE_MID_MIN_TIME) {
            return new SpawnPlan(PHASE_MID_INTERVAL, MID_ENEMIES);
        }
        return new SpawnPlan(PHASE_LATE_INTERVAL, LATE_ENEMIES);
    }

    /**
     * Instantiates a random enemy from a provided pool.
     *
     * @param enemyPool the list of enemy factories
     * @return a new {@link Enemy} instance
     */
    private Enemy selectRandomEnemy(List<Supplier<Enemy>> enemyPool) {
        if (enemyPool.isEmpty()) {
            return null;
        }
        Supplier<Enemy> enemyFactory = enemyPool.get(random.nextInt(enemyPool.size()));
        return enemyFactory.get();
    }

    /**
     * Checks whether the currently selected tower can be placed at the tile.
     *
     * @param row tile row
     * @param col tile column
     * @return true when placement is valid and affordable
     */
    public boolean canPlaceTower(int row, int col) {
        if (!isPlacementRequestValid(row, col)) {
            return false;
        }

        Tower tower = createTowerFromType(selectedTowerType);
        return tower != null && playerMoney >= tower.getCost();
    }

    /**
     * Attempts to upgrade a tower owned by the player.
     *
     * @param tower tower to upgrade
     * @return true when upgrade succeeds
     */
    public boolean tryUpgradeTower(Tower tower) {
        if (tower == null || isGameOver) {
            return false;
        }
        if (!activeTowers.contains(tower) || !tower.canUpgrade()) {
            return false;
        }
        int cost = tower.getUpgradeCost();
        if (playerMoney < cost) {
            return false;
        }

        playerMoney = Math.max(0, playerMoney - cost);
        tower.upgrade();
        return true;
    }

    /**
     * Resets runtime state to initial defaults for a fresh run.
     */
    public void resetGameState() {
        activeEnemies.clear();
        activeTowers.clear();
        activeProjectiles.clear();
        activeDamageTexts.clear();

        baseHealth = INITIAL_BASE_HEALTH;
        playerMoney = INITIAL_PLAYER_MONEY;
        isGameOver = false;
        isVictory = false;
        timeRemaining = GAME_DURATION_SECONDS;
        spawnCooldown = 0.0;
        selectedTowerType = DEFAULT_SELECTED_TOWER;

        if (currentMap != null) {
            currentMap.getDecorations().clear();
        }
    }

    /**
     * Adds floating combat text if the on-screen cap has not been reached.
     *
     * @param text displayed text
     * @param x world x-coordinate
     * @param y world y-coordinate
     * @param color text color
     */
    public void createDamageText(String text, double x, double y, Color color) {
        if (activeDamageTexts.size() >= MAX_DAMAGE_TEXTS) {
            return;
        }
        activeDamageTexts.add(new DamageText(text, x, y, color));
    }

    /**
     * Creates the map of tower types to their factory functions.
     *
     * @return the configured map
     */
    private static Map<TowerType, Supplier<Tower>> createTowerFactories() {
        EnumMap<TowerType, Supplier<Tower>> factories = new EnumMap<>(TowerType.class);
        factories.put(TowerType.ARCHER, ArcherTower::new);
        factories.put(TowerType.CANNON, CannonTower::new);
        factories.put(TowerType.CROSSBOW, CrossbowTower::new);
        factories.put(TowerType.ICE_WIZARD, IceWizardTower::new);
        factories.put(TowerType.LIGHTNING_WIZARD, LightningWizardTower::new);
        factories.put(TowerType.POISON_WIZARD, PoisonWizardTower::new);
        return Map.copyOf(factories);
    }

    /**
     * Sets the physical coordinates of a tower based on its grid position.
     *
     * @param tower the tower to position
     * @param row the grid row
     * @param col the grid column
     */
    private static void placeTowerAtTile(Tower tower, int row, int col) {
        int tileSize = GameMap.PATH_TILE_PIXEL_SIZE;
        tower.setX(col * tileSize + tileSize / 2.0);
        tower.setY(row * tileSize + tileSize / 2.0);
        tower.setPlacementTile(row, col);
    }

    /**
     * Configuration record for spawning enemies during a specific phase.
     *
     * @param spawnInterval the time between spawns
     * @param enemyPool the list of possible enemies
     */
    private record SpawnPlan(double spawnInterval, List<Supplier<Enemy>> enemyPool) {
    }

    /**
     * Gets the current game map.
     *
     * @return the game map
     */
    public GameMap getCurrentMap() {
        return currentMap;
    }

    /**
     * Gets the remaining base health.
     *
     * @return the base health
     */
    public int getBaseHealth() {
        return baseHealth;
    }

    /**
     * Gets the currently selected tower type for placement.
     *
     * @return the selected tower type
     */
    public TowerType getSelectedTowerType() {
        return selectedTowerType;
    }

    /**
     * Gets the firing range for a specific tower type.
     *
     * @param type the tower type
     * @return the range in pixels
     */
    public double getTowerRange(TowerType type) {
        if (type == null) {
            return 0.0;
        }
        Tower tower = createTowerFromType(type);
        return tower != null ? tower.getRange() : DEFAULT_TOWER_RANGE;
    }

    /**
     * Gets the list of active enemies.
     *
     * @return the list of enemies
     */
    public List<Enemy> getActiveEnemies() {
        return activeEnemies;
    }

    /**
     * Gets the list of placed towers.
     *
     * @return the list of towers
     */
    public List<Tower> getActiveTowers() {
        return activeTowers;
    }

    /**
     * Gets the list of in-flight projectiles.
     *
     * @return the list of projectiles
     */
    public List<Projectile> getActiveProjectiles() {
        return activeProjectiles;
    }

    /**
     * Gets the player's current money balance.
     *
     * @return the amount of money
     */
    public int getPlayerMoney() {
        return playerMoney;
    }

    /**
     * Gets the remaining time before victory.
     *
     * @return the time in seconds
     */
    public double getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Gets the remaining time formatted as MM:SS.
     *
     * @return the formatted time string
     */
    public String getFormattedTime() {
        int minutes = (int) (timeRemaining / 60);
        int seconds = (int) (timeRemaining % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Gets the list of active floating damage texts.
     *
     * @return the list of damage texts
     */
    public List<DamageText> getActiveDamageTexts() {
        return activeDamageTexts;
    }

    /**
     * Checks if the game is over (base destroyed).
     *
     * @return {@code true} if game over, {@code false} otherwise
     */
    public boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Checks if the player has won.
     *
     * @return {@code true} if victory, {@code false} otherwise
     */
    public boolean isVictory() {
        return isVictory;
    }

    /**
     * Validates if the player is allowed to place a tower at the given grid coordinates.
     *
     * @param row the grid row
     * @param col the grid column
     * @return {@code true} if valid, {@code false} otherwise
     */
    private boolean isPlacementRequestValid(int row, int col) {
        if (currentMap == null || isGameOver || selectedTowerType == null) {
            return false;
        }
        if (!currentMap.isBuildable(row, col, currentMap.getDecorations())) {
            return false;
        }
        return !isTileOccupiedByTower(row, col);
    }

    /**
     * Checks if a tower already exists at the given grid coordinates.
     *
     * @param row the grid row
     * @param col the grid column
     * @return {@code true} if occupied, {@code false} otherwise
     */
    private boolean isTileOccupiedByTower(int row, int col) {
        for (Tower tower : activeTowers) {
            if (tower.getGridRow() == row && tower.getGridCol() == col) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the current game map.
     *
     * @param currentMap the map to set
     */
    public void setCurrentMap(GameMap currentMap) {
        this.currentMap = currentMap;
    }

    /**
     * Sets the player's money balance.
     *
     * @param playerMoney the amount to set
     */
    public void setPlayerMoney(int playerMoney) {
        this.playerMoney = playerMoney;
    }

    /**
     * Sets the base health.
     *
     * @param baseHealth the health to set
     */
    public void setBaseHealth(int baseHealth) {
        this.baseHealth = baseHealth;
    }

    /**
     * Forcefully sets the game over state.
     *
     * @param isGameOver the game over state
     */
    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    /**
     * Selects a tower type for future placement.
     *
     * @param selectedTowerType the tower type to select
     */
    public void setSelectedTowerType(TowerType selectedTowerType) {
        this.selectedTowerType = selectedTowerType;
        System.out.println("Selected tower type: " + selectedTowerType);
    }
}
