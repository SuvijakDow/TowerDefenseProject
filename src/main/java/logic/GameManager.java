package logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.scene.paint.Color;
import logic.enemy.Enemy;
import logic.enemy.SlimeEnemy;
import logic.enemy.BatEnemy;
import logic.enemy.SkeletonEnemy;
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
import logic.enemy.BigSlimeEnemy;
import logic.enemy.GoblinEnemy;
import logic.enemy.DemonEnemy;
import logic.enemy.KingSlimeEnemy;
import application.GameView;
import application.SoundManager;

public class GameManager {
    public enum TowerType { ARCHER, CANNON, CROSSBOW, ICE_WIZARD, LIGHTNING_WIZARD, POISON_WIZARD }
    
    private GameMap currentMap;
    private List<Enemy> activeEnemies;
    private List<Tower> activeTowers;
    private List<Projectile> activeProjectiles;
    private List<DamageText> activeDamageTexts;
    private int playerMoney;
    private int baseHealth;
    private boolean isGameOver;
    private TowerType selectedTowerType;
    private GameView gameView;
    
    // Timer and Victory System
    private double timeRemaining = 180.0; // 3 minutes
    private boolean isVictory = false;
    private double spawnCooldown = 0.0;

    public GameManager(GameMap map, GameView gameView) {
        this.currentMap = map;
        this.gameView = gameView;
        this.activeEnemies = new ArrayList<>();
        this.activeTowers = new ArrayList<>();
        this.activeProjectiles = new ArrayList<>();
        this.activeDamageTexts = new ArrayList<>();
        this.playerMoney = 500;
        this.baseHealth = 100;
        this.isGameOver = false;
        this.selectedTowerType = TowerType.ARCHER;
    }

    // Places a tower if the player has enough money (no grid validation; tests /
    // legacy).
    public boolean placeTower(Tower tower) {
        if (playerMoney >= tower.getCost()) {
            playerMoney -= tower.getCost();
            activeTowers.add(tower);
            return true;
        }
        return false;
    }

    /**
     * Places {@code tower} at tile {@code (row, col)} if buildable, affordable, and
     * tile not
     * already occupied by another placed tower.
     */
    public boolean placeTower(int row, int col) {
        if (currentMap == null || isGameOver) {
            return false;
        }
        if (selectedTowerType == null) {
            return false;
        }
        if (!currentMap.isBuildable(row, col, currentMap.getDecorations())) {
            return false;
        }
        for (Tower t : activeTowers) {
            if (t.getGridRow() == row && t.getGridCol() == col) {
                return false;
            }
        }
        
        Tower tower = createTowerFromType(selectedTowerType);
        if (tower == null) {
            return false;
        }
        
        if (playerMoney < tower.getCost()) {
            System.out.println("Not enough money! Need: " + tower.getCost() + ", Have: " + playerMoney);
            return false;
        }

        int ts = GameMap.PATH_TILE_PIXEL_SIZE;
        tower.setX(col * ts + ts / 2.0);
        tower.setY(row * ts + ts / 2.0);
        tower.setPlacementTile(row, col);

        playerMoney -= tower.getCost();
        playerMoney = Math.max(0, playerMoney); // Prevent negative money
        activeTowers.add(tower);
        System.out.println("Placed " + selectedTowerType + " tower at (" + row + "," + col + "). Money remaining: " + playerMoney);
        return true;
    }

    // Spawns an enemy at the first waypoint
    public void spawnEnemy(Enemy enemy) {
        if (currentMap == null || currentMap.getPathWaypoints().isEmpty())
            return;

        Waypoint start = currentMap.getPathWaypoints().get(0);
        enemy.setX(start.getX());
        enemy.setY(start.getY());
        enemy.setCurrentWaypointIndex(0);
        activeEnemies.add(enemy);
    }

    // Core game tick logic
    public void update(double deltaTime) {
        if (isGameOver || isVictory)
            return;

        // Update timer
        if (timeRemaining > 0) {
            timeRemaining -= deltaTime;
            timeRemaining = Math.max(0, timeRemaining);
        }
        
        // Victory check: time up, no enemies, and base still alive
        if (timeRemaining <= 0 && activeEnemies.isEmpty() && baseHealth > 0) {
            isVictory = true;
            System.out.println("Victory! You survived the siege!");
            return;
        }
        
        // Handle spawning
        handleSpawning(deltaTime);

        List<Waypoint> waypoints = currentMap != null ? currentMap.getPathWaypoints() : List.of();

        // Move enemies (logic + animation inside Enemy.update)
        Iterator<Enemy> enemyIterator = activeEnemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update(waypoints);

            // Reached end of path (past last waypoint)
            if (enemy.getCurrentWaypointIndex() >= waypoints.size()) {
                baseHealth -= enemy.getDamage();
                baseHealth = Math.max(0, baseHealth);
                // Castle hit SFX whenever base takes damage.
                SoundManager.playCastleIsAttackedSfx();
                System.out.println("Enemy reached base! Base health: " + baseHealth);
                
                // Trigger castle hit effect
                if (gameView != null) {
                    gameView.playCastleHitEffect();
                }
                
                enemyIterator.remove();
                if (baseHealth <= 0) {
                    baseHealth = 0;
                    isGameOver = true;
                    System.out.println("Game Over! Base destroyed.");
                }
            }
        }

        if (isGameOver) {
            return;
        }

        for (Tower tower : activeTowers) {
            tower.update(activeEnemies, activeProjectiles);
        }

        Iterator<Projectile> projectileIterator = activeProjectiles.iterator();
        while (projectileIterator.hasNext()) {
            Projectile p = projectileIterator.next();
            if (p.update()) {
                Enemy target = p.getTarget();
                if (target != null && !target.isDead()) {
                    target.takeDamage(p.getDamage());
                    // Create damage text at enemy position
                    createDamageText(String.valueOf(p.getDamage()), target.getX(), target.getY(), Color.WHITE);
                }
                projectileIterator.remove();
            }
        }

        // Update damage texts
        Iterator<DamageText> damageTextIterator = activeDamageTexts.iterator();
        while (damageTextIterator.hasNext()) {
            DamageText damageText = damageTextIterator.next();
            if (damageText.update(deltaTime)) {
                damageTextIterator.remove();
            }
        }

        enemyIterator = activeEnemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (enemy.isDead()) {
                playerMoney += enemy.getBounty();
                System.out.println("Enemy defeated! Earned: " + enemy.getBounty() + ", Total money: " + playerMoney);
                enemyIterator.remove();
            }
        }
    }

    // Getters and Setters
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
        switch (type) {
            case ARCHER:
                return new ArcherTower();
            case CANNON:
                return new CannonTower();
            case CROSSBOW:
                return new CrossbowTower();
            case ICE_WIZARD:
                return new IceWizardTower();
            case LIGHTNING_WIZARD:
                return new LightningWizardTower();
            case POISON_WIZARD:
                return new PoisonWizardTower();
            default:
                return null;
        }
    }
    
    public double getTowerRange(TowerType type) {
        if (type == null) {
            return 0.0;
        }
        Tower tempTower = createTowerFromType(type);
        return tempTower != null ? tempTower.getRange() : 100.0;
    }

    private void handleSpawning(double deltaTime) {
        if (timeRemaining <= 0) {
            return;
        }
        
        spawnCooldown -= deltaTime;

        double spawnInterval;
        if (timeRemaining > 120) {
            spawnInterval = 1.2;
            if (spawnCooldown <= 0) {
                int enemyType = (int)(Math.random() * 3);
                switch (enemyType) {
                    case 0: spawnEnemy(new SlimeEnemy()); break;
                    case 1: spawnEnemy(new GoblinEnemy()); break;
                    case 2: spawnEnemy(new SkeletonEnemy()); break;
                }
                spawnCooldown = spawnInterval;
            }
        } else if (timeRemaining > 60) {
            spawnInterval = 0.8;
            if (spawnCooldown <= 0) {
                int enemyType = (int)(Math.random() * 3);
                switch (enemyType) {
                    case 0: spawnEnemy(new BigSlimeEnemy()); break;
                    case 1: spawnEnemy(new DemonEnemy()); break;
                    case 2: spawnEnemy(new ZombieEnemy()); break;
                }
                spawnCooldown = spawnInterval;
            }
        } else {
            spawnInterval = 0.5;
            if (spawnCooldown <= 0) {
                int enemyType = (int)(Math.random() * 3);
                switch (enemyType) {
                    case 0: spawnEnemy(new DemonEnemy()); break;
                    case 1: spawnEnemy(new KingSlimeEnemy()); break;
                    case 2: spawnEnemy(new BatEnemy()); break;
                }
                spawnCooldown = spawnInterval;
            }
        }
    }
    
    /**
     * Check if a tower can be placed at the given position without actually placing it.
     * This is used for hover highlighting (white = can place, red = cannot place).
     */
    public boolean canPlaceTower(int row, int col) {
        if (currentMap == null || isGameOver) {
            return false;
        }
        if (selectedTowerType == null) {
            return false;
        }
        if (!currentMap.isBuildable(row, col, currentMap.getDecorations())) {
            return false;
        }
        for (Tower t : activeTowers) {
            if (t.getGridRow() == row && t.getGridCol() == col) {
                return false;
            }
        }
        
        Tower tower = createTowerFromType(selectedTowerType);
        if (tower == null) {
            return false;
        }
        
        // Check if player has enough money
        return playerMoney >= tower.getCost();
    }

    public boolean tryUpgradeTower(Tower tower) {
        if (tower == null || isGameOver) {
            return false;
        }
        if (!activeTowers.contains(tower)) {
            return false;
        }
        if (!tower.canUpgrade()) {
            return false;
        }
        int cost = tower.getUpgradeCost();
        if (playerMoney < cost) {
            return false;
        }
        playerMoney -= cost;
        playerMoney = Math.max(0, playerMoney);
        tower.upgrade();
        return true;
    }
    
    public void resetGameState() {
        // Clear all game entities
        activeEnemies.clear();
        activeTowers.clear();
        activeProjectiles.clear();
        activeDamageTexts.clear();
        
        // Reset player stats
        baseHealth = 100;
        playerMoney = 500;
        
        // Reset game state
        isGameOver = false;
        isVictory = false;
        timeRemaining = 180.0;
        spawnCooldown = 0.0;
        selectedTowerType = TowerType.ARCHER;
        
        // Clear decorations if needed
        if (currentMap != null) {
            currentMap.getDecorations().clear();
        }
    }
    
    // Timer and Victory getters
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
        // Limit maximum damage texts to prevent lag
        if (activeDamageTexts.size() >= 50) {
            return;
        }
        activeDamageTexts.add(new DamageText(text, x, y, color));
    }
    
    /**
     * Gets the list of active damage texts for rendering.
     */
    public List<DamageText> getActiveDamageTexts() {
        return activeDamageTexts;
    }
}
