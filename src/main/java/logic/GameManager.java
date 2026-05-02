package logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    // Places a tower if the player has enough money
    public boolean placeTower(Tower tower) {
        if (playerMoney >= tower.getCost()) {
            playerMoney -= tower.getCost();
            activeTowers.add(tower);
            return true;
        }
        return false;
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

    // Core game tick logic
    public void update() {
        if (isGameOver) return;

        // Move enemies and check for waypoint completion
        Iterator<Enemy> enemyIterator = activeEnemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            List<Waypoint> waypoints = currentMap.getPathWaypoints();
            
            if (enemy.getCurrentWaypointIndex() < waypoints.size()) {
                Waypoint target = waypoints.get(enemy.getCurrentWaypointIndex());
                enemy.move(target);
                
                // Check if reached waypoint
                double dx = target.getX() - enemy.getX();
                double dy = target.getY() - enemy.getY();
                if (Math.sqrt(dx * dx + dy * dy) <= 0.1) { // Very close or exact
                    enemy.setCurrentWaypointIndex(enemy.getCurrentWaypointIndex() + 1);
                }
            }
            
            // Check if reached the end
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
