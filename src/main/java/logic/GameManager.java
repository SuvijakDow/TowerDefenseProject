package logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import logic.enemy.Enemy;
import logic.enemy.SlimeEnemy;
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

public class GameManager {
    public enum TowerType { ARCHER, CANNON, CROSSBOW, ICE_WIZARD, LIGHTNING_WIZARD, POISON_WIZARD }
    
    private GameMap currentMap;
    private List<Enemy> activeEnemies;
    private List<Tower> activeTowers;
    private List<Projectile> activeProjectiles;
    private int playerMoney;
    private int baseHealth;
    private boolean isGameOver;
    private TowerType selectedTowerType;

    public GameManager(GameMap map) {
        this.currentMap = map;
        this.activeEnemies = new ArrayList<>();
        this.activeTowers = new ArrayList<>();
        this.activeProjectiles = new ArrayList<>();
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

    /** Spawns one {@link SlimeEnemy} at path start (for testing / demo). */
    public void spawnTestSlime() {
        spawnEnemy(new SlimeEnemy());
    }
    
    /** Spawns enemies with delay to prevent overlap */
    public void spawnEnemyWave(int count) {
        new Thread(() -> {
            try {
                for (int i = 0; i < count; i++) {
                    final int enemyIndex = i; // Make effectively final for lambda
                    Thread.sleep(500); // 0.5 second delay between spawns
                    javafx.application.Platform.runLater(() -> {
                        // Spawn different enemy types for variety
                        switch (enemyIndex % 5) {
                            case 0:
                                spawnEnemy(new SlimeEnemy());
                                break;
                            case 1:
                                spawnEnemy(new BigSlimeEnemy());
                                break;
                            case 2:
                                spawnEnemy(new GoblinEnemy());
                                break;
                            case 3:
                                spawnEnemy(new DemonEnemy());
                                break;
                            case 4:
                                spawnEnemy(new KingSlimeEnemy());
                                break;
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println("Spawned enemy wave of " + count + " enemies with delay!");
    }

    // Core game tick logic
    public void update() {
        if (isGameOver)
            return;

        List<Waypoint> waypoints = currentMap != null ? currentMap.getPathWaypoints() : List.of();

        // Move enemies (logic + animation inside Enemy.update)
        Iterator<Enemy> enemyIterator = activeEnemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update(waypoints);

            // Reached end of path (past last waypoint)
            if (enemy.getCurrentWaypointIndex() >= waypoints.size()) {
                baseHealth -= enemy.getDamage();
                baseHealth = Math.max(0, baseHealth); // Prevent negative HP
                System.out.println("Enemy reached base! Base health: " + baseHealth);
                enemyIterator.remove();
                if (baseHealth <= 0) {
                    baseHealth = 0; // Ensure exactly 0
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
                }
                projectileIterator.remove();
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
    
    private Tower createTowerFromType(TowerType type) {
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
    
    public void resetGameState() {
        // Clear all game entities
        activeEnemies.clear();
        activeTowers.clear();
        activeProjectiles.clear();
        
        // Reset player stats
        baseHealth = 100;
        playerMoney = 500;
        
        // Reset game state
        isGameOver = false;
        selectedTowerType = TowerType.ARCHER;
        
        // Clear decorations if needed
        if (currentMap != null) {
            currentMap.getDecorations().clear();
        }
    }
}
