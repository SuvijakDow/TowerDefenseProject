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

public class GameManager {
    public enum TowerType { ARCHER, CANNON, CROSSBOW, ICE_WIZARD, LIGHTNING_WIZARD, POISON_WIZARD }

    private static final int INITIAL_PLAYER_MONEY = 500;
    private static final int INITIAL_BASE_HEALTH = 100;
    private static final TowerType DEFAULT_SELECTED_TOWER = TowerType.ARCHER;
    private static final double GAME_DURATION_SECONDS = 180.0;
    private static final int MAX_DAMAGE_TEXTS = 50;
    private static final double DEFAULT_TOWER_RANGE = 100.0;

    private static final double PHASE_EARLY_MIN_TIME = 120.0;
    private static final double PHASE_MID_MIN_TIME = 60.0;
    private static final double PHASE_EARLY_INTERVAL = 1.2;
    private static final double PHASE_MID_INTERVAL = 0.8;
    private static final double PHASE_LATE_INTERVAL = 0.5;

    private static final List<Supplier<Enemy>> EARLY_ENEMIES = List.of(
            SlimeEnemy::new,
            GoblinEnemy::new,
            SkeletonEnemy::new
    );
    private static final List<Supplier<Enemy>> MID_ENEMIES = List.of(
            BigSlimeEnemy::new,
            DemonEnemy::new,
            ZombieEnemy::new
    );
    private static final List<Supplier<Enemy>> LATE_ENEMIES = List.of(
            DemonEnemy::new,
            KingSlimeEnemy::new,
            BatEnemy::new
    );

    private static final Map<TowerType, Supplier<Tower>> TOWER_FACTORIES = createTowerFactories();

    private final Random random = new Random();

    private GameMap currentMap;
    private final List<Enemy> activeEnemies;
    private final List<Tower> activeTowers;
    private final List<Projectile> activeProjectiles;
    private final List<DamageText> activeDamageTexts;

    private int playerMoney;
    private int baseHealth;
    private boolean isGameOver;
    private TowerType selectedTowerType;
    private final GameView gameView;

    private double timeRemaining = GAME_DURATION_SECONDS;
    private boolean isVictory = false;
    private double spawnCooldown = 0.0;

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

    // Places a tower if the player has enough money (no grid validation; tests / legacy).
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

    // Spawns an enemy at the first waypoint.
    public void spawnEnemy(Enemy enemy) {
        if (enemy == null || currentMap == null || currentMap.getPathWaypoints().isEmpty()) {
            return;
        }

        Waypoint start = currentMap.getPathWaypoints().get(0);
        enemy.setX(start.getX());
        enemy.setY(start.getY());
        enemy.setCurrentWaypointIndex(0);
        activeEnemies.add(enemy);
    }

    // Core game tick logic.
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

    private void updateTimer(double deltaTime) {
        if (timeRemaining <= 0) {
            return;
        }
        timeRemaining = Math.max(0.0, timeRemaining - deltaTime);
    }

    private boolean checkAndHandleVictory() {
        if (timeRemaining > 0 || !activeEnemies.isEmpty() || baseHealth <= 0) {
            return false;
        }
        isVictory = true;
        System.out.println("Victory! You survived the siege!");
        return true;
    }

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

    private void updateTowers() {
        for (Tower tower : activeTowers) {
            tower.update(activeEnemies, activeProjectiles);
        }
    }

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

    private void updateDamageTexts(double deltaTime) {
        activeDamageTexts.removeIf(damageText -> damageText.update(deltaTime));
    }

    private void collectBountiesFromDeadEnemies() {
        Iterator<Enemy> enemyIterator = activeEnemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (!enemy.isDead()) {
                continue;
            }
            playerMoney += enemy.getBounty();
            System.out.println("Enemy defeated! Earned: " + enemy.getBounty() + ", Total money: " + playerMoney);
            enemyIterator.remove();
        }
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(GameMap currentMap) {
        this.currentMap = currentMap;
    }

    public List<Enemy> getActiveEnemies() {
        return activeEnemies;
    }

    public List<Tower> getActiveTowers() {
        return activeTowers;
    }

    public List<Projectile> getActiveProjectiles() {
        return activeProjectiles;
    }

    public int getPlayerMoney() {
        return playerMoney;
    }

    public void setPlayerMoney(int playerMoney) {
        this.playerMoney = playerMoney;
    }

    public int getBaseHealth() {
        return baseHealth;
    }

    public void setBaseHealth(int baseHealth) {
        this.baseHealth = baseHealth;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public TowerType getSelectedTowerType() {
        return selectedTowerType;
    }

    public void setSelectedTowerType(TowerType selectedTowerType) {
        this.selectedTowerType = selectedTowerType;
        System.out.println("Selected tower type: " + selectedTowerType);
    }

    public Tower createTowerFromType(TowerType type) {
        if (type == null) {
            return null;
        }
        Supplier<Tower> towerFactory = TOWER_FACTORIES.get(type);
        return towerFactory != null ? towerFactory.get() : null;
    }

    public double getTowerRange(TowerType type) {
        if (type == null) {
            return 0.0;
        }
        Tower tower = createTowerFromType(type);
        return tower != null ? tower.getRange() : DEFAULT_TOWER_RANGE;
    }

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

    private SpawnPlan selectSpawnPlan() {
        if (timeRemaining > PHASE_EARLY_MIN_TIME) {
            return new SpawnPlan(PHASE_EARLY_INTERVAL, EARLY_ENEMIES);
        }
        if (timeRemaining > PHASE_MID_MIN_TIME) {
            return new SpawnPlan(PHASE_MID_INTERVAL, MID_ENEMIES);
        }
        return new SpawnPlan(PHASE_LATE_INTERVAL, LATE_ENEMIES);
    }

    private Enemy selectRandomEnemy(List<Supplier<Enemy>> enemyPool) {
        if (enemyPool.isEmpty()) {
            return null;
        }
        Supplier<Enemy> enemyFactory = enemyPool.get(random.nextInt(enemyPool.size()));
        return enemyFactory.get();
    }

    /**
     * Check if a tower can be placed at the given position without actually placing it.
     * This is used for hover highlighting (white = can place, red = cannot place).
     */
    public boolean canPlaceTower(int row, int col) {
        if (!isPlacementRequestValid(row, col)) {
            return false;
        }

        Tower tower = createTowerFromType(selectedTowerType);
        return tower != null && playerMoney >= tower.getCost();
    }

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

    public double getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isVictory() {
        return isVictory;
    }

    public String getFormattedTime() {
        int minutes = (int) (timeRemaining / 60);
        int seconds = (int) (timeRemaining % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Creates a floating damage text at the specified position.
     * Limits damage texts to prevent performance issues.
     */
    public void createDamageText(String text, double x, double y, Color color) {
        if (activeDamageTexts.size() >= MAX_DAMAGE_TEXTS) {
            return;
        }
        activeDamageTexts.add(new DamageText(text, x, y, color));
    }

    public List<DamageText> getActiveDamageTexts() {
        return activeDamageTexts;
    }

    private boolean isPlacementRequestValid(int row, int col) {
        if (currentMap == null || isGameOver || selectedTowerType == null) {
            return false;
        }
        if (!currentMap.isBuildable(row, col, currentMap.getDecorations())) {
            return false;
        }
        return !isTileOccupiedByTower(row, col);
    }

    private boolean isTileOccupiedByTower(int row, int col) {
        for (Tower tower : activeTowers) {
            if (tower.getGridRow() == row && tower.getGridCol() == col) {
                return true;
            }
        }
        return false;
    }

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

    private static void placeTowerAtTile(Tower tower, int row, int col) {
        int tileSize = GameMap.PATH_TILE_PIXEL_SIZE;
        tower.setX(col * tileSize + tileSize / 2.0);
        tower.setY(row * tileSize + tileSize / 2.0);
        tower.setPlacementTile(row, col);
    }

    private record SpawnPlan(double spawnInterval, List<Supplier<Enemy>> enemyPool) {
    }
}
