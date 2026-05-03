package logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import logic.enemy.Enemy;
import logic.enemy.SlimeEnemy;
import logic.map.GameMap;
import logic.map.Waypoint;
import logic.tower.Tower;

public class GameManager {
    private GameMap currentMap;
    private List<Enemy> activeEnemies;
    private List<Tower> activeTowers;
    private int playerMoney;
    private int baseHealth;
    private boolean isGameOver;

    public GameManager(GameMap map) {
        this.currentMap = map;
        this.activeEnemies = new ArrayList<>();
        this.activeTowers = new ArrayList<>();
        this.playerMoney = 500;
        this.baseHealth = 100;
        this.isGameOver = false;
    }

    // Places a tower if the player has enough money (no grid validation; tests / legacy).
    public boolean placeTower(Tower tower) {
        if (playerMoney >= tower.getCost()) {
            playerMoney -= tower.getCost();
            activeTowers.add(tower);
            return true;
        }
        return false;
    }

    /**
     * Places {@code tower} at tile {@code (row, col)} if buildable, affordable, and tile not
     * already occupied by another placed tower.
     */
    public boolean placeTower(Tower tower, int row, int col) {
        if (currentMap == null || isGameOver) {
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
        if (playerMoney < tower.getCost()) {
            return false;
        }

        int ts = GameMap.PATH_TILE_PIXEL_SIZE;
        tower.setX(col * ts + ts / 2.0);
        tower.setY(row * ts + ts / 2.0);
        tower.setPlacementTile(row, col);

        playerMoney -= tower.getCost();
        activeTowers.add(tower);
        return true;
    }

    // Spawns an enemy at the first waypoint
    public void spawnEnemy(Enemy enemy) {
        if (currentMap == null || currentMap.getPathWaypoints().isEmpty()) return;
        
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

    // Core game tick logic
    public void update() {
        if (isGameOver) return;

        List<Waypoint> waypoints = currentMap != null ? currentMap.getPathWaypoints() : List.of();

        // Move enemies (logic + animation inside Enemy.update)
        Iterator<Enemy> enemyIterator = activeEnemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update(waypoints);

            // Reached end of path (past last waypoint)
            if (enemy.getCurrentWaypointIndex() >= waypoints.size()) {
                baseHealth--;
                enemyIterator.remove();
                if (baseHealth <= 0) {
                    isGameOver = true;
                }
            }
        }

        // Towers attack
        for (Tower tower : activeTowers) {
            tower.attack(activeEnemies);
        }

        // Post-attack check for dead enemies
        enemyIterator = activeEnemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (enemy.isDead()) {
                playerMoney += enemy.getRewardMoney();
                enemyIterator.remove();
            }
        }
    }

    // Getters and Setters
    public GameMap getCurrentMap() { return currentMap; }
    public void setCurrentMap(GameMap currentMap) { this.currentMap = currentMap; }
    public List<Enemy> getActiveEnemies() { return activeEnemies; }
    public List<Tower> getActiveTowers() { return activeTowers; }
    public int getPlayerMoney() { return playerMoney; }
    public void setPlayerMoney(int playerMoney) { this.playerMoney = playerMoney; }
    public int getBaseHealth() { return baseHealth; }
    public void setBaseHealth(int baseHealth) { this.baseHealth = baseHealth; }
    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean isGameOver) { this.isGameOver = isGameOver; }
}
