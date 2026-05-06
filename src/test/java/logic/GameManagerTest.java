package logic;

import logic.enemy.SlimeEnemy;
import logic.map.Decoration;
import logic.map.GameMap;
import logic.tower.ArcherTower;
import logic.tower.Projectile;
import logic.tower.Tower;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {

    @Test
    void placeTowerWithTowerObjectDeductsMoneyOnlyWhenAffordable() {
        GameMap map = new GameMap(new int[][]{{0}});
        GameManager manager = new GameManager(map, null);
        manager.setPlayerMoney(150);

        Tower archer = new ArcherTower();
        boolean success = manager.placeTower(archer);

        assertTrue(success);
        assertEquals(50, manager.getPlayerMoney());
        assertEquals(1, manager.getActiveTowers().size());

        boolean failWithNull = manager.placeTower(null);
        assertFalse(failWithNull);
        assertEquals(50, manager.getPlayerMoney());
        assertEquals(1, manager.getActiveTowers().size());
    }

    @Test
    void placeTowerByTilePlacesSelectedTowerAndRejectsOccupiedTile() {
        int[][] grid = {
                {0, 0},
                {0, 0}
        };
        GameMap map = new GameMap(grid);
        GameManager manager = new GameManager(map, null);
        manager.setSelectedTowerType(GameManager.TowerType.ARCHER);
        manager.setPlayerMoney(500);

        boolean firstPlacement = manager.placeTower(1, 1);
        assertTrue(firstPlacement);
        assertEquals(400, manager.getPlayerMoney());
        assertEquals(1, manager.getActiveTowers().size());

        Tower placed = manager.getActiveTowers().get(0);
        assertEquals(1, placed.getGridRow());
        assertEquals(1, placed.getGridCol());
        assertEquals(75.0, placed.getX());
        assertEquals(75.0, placed.getY());

        boolean duplicatePlacement = manager.placeTower(1, 1);
        assertFalse(duplicatePlacement);
        assertEquals(400, manager.getPlayerMoney());
        assertEquals(1, manager.getActiveTowers().size());
    }

    @Test
    void canPlaceTowerRequiresSelectionAndSufficientFunds() {
        GameMap map = new GameMap(new int[][]{{0}});
        GameManager manager = new GameManager(map, null);

        manager.setSelectedTowerType(null);
        assertFalse(manager.canPlaceTower(0, 0));

        manager.setSelectedTowerType(GameManager.TowerType.ARCHER);
        manager.setPlayerMoney(50);
        assertFalse(manager.canPlaceTower(0, 0));

        manager.setPlayerMoney(500);
        assertTrue(manager.canPlaceTower(0, 0));
    }

    @Test
    void spawnEnemyPlacesEnemyAtFirstWaypoint() {
        GameMap map = new GameMap(new int[][]{{1, 0}});
        map.generateWaypointsFromGrid(GameMap.PATH_TILE_PIXEL_SIZE);
        GameManager manager = new GameManager(map, null);
        SlimeEnemy slime = new SlimeEnemy();

        manager.spawnEnemy(slime);

        assertEquals(1, manager.getActiveEnemies().size());
        assertEquals(25.0, slime.getX());
        assertEquals(25.0, slime.getY());
        assertEquals(0, slime.getCurrentWaypointIndex());
    }

    @Test
    void tryUpgradeTowerNeedsOwnedTowerAndMoney() {
        GameMap map = new GameMap(new int[][]{{0}});
        GameManager manager = new GameManager(map, null);
        ArcherTower ownedTower = new ArcherTower();

        assertTrue(manager.placeTower(ownedTower));

        int upgradeCost = ownedTower.getUpgradeCost();
        manager.setPlayerMoney(upgradeCost);

        assertTrue(manager.tryUpgradeTower(ownedTower));
        assertEquals(2, ownedTower.getLevel());
        assertEquals(0, manager.getPlayerMoney());

        manager.setPlayerMoney(500);
        assertFalse(manager.tryUpgradeTower(new ArcherTower()));
    }

    @Test
    void resetGameStateRestoresDefaultsAndClearsCollections() {
        GameMap map = new GameMap(new int[][]{{0, 0}});
        map.getDecorations().add(new Decoration("tree", 0, 0, 1.0));
        GameManager manager = new GameManager(map, null);

        manager.setPlayerMoney(1);
        manager.setBaseHealth(1);
        manager.setGameOver(true);
        manager.setSelectedTowerType(GameManager.TowerType.CANNON);

        manager.getActiveEnemies().add(new SlimeEnemy());
        ArcherTower tower = new ArcherTower();
        manager.getActiveTowers().add(tower);
        manager.getActiveProjectiles().add(new Projectile(0, 0, 1, 1, new SlimeEnemy(), null));
        manager.createDamageText("10", 10, 10, javafx.scene.paint.Color.WHITE);

        manager.resetGameState();

        assertEquals(500, manager.getPlayerMoney());
        assertEquals(100, manager.getBaseHealth());
        assertFalse(manager.isGameOver());
        assertFalse(manager.isVictory());
        assertEquals(180.0, manager.getTimeRemaining());
        assertEquals(GameManager.TowerType.ARCHER, manager.getSelectedTowerType());
        assertTrue(manager.getActiveEnemies().isEmpty());
        assertTrue(manager.getActiveTowers().isEmpty());
        assertTrue(manager.getActiveProjectiles().isEmpty());
        assertTrue(manager.getActiveDamageTexts().isEmpty());
        assertTrue(map.getDecorations().isEmpty());
    }

    @Test
    void updateDoesNothingWhenGameIsAlreadyOver() {
        int[][] grid = {{0}};
        GameMap map = new GameMap(grid);
        GameManager manager = new GameManager(map, null);
        manager.setGameOver(true);
        double timeBefore = manager.getTimeRemaining();

        manager.update(30.0);

        assertEquals(timeBefore, manager.getTimeRemaining());
    }

    @Test
    void updateRemovesEnemyAndDamagesBaseWhenMapIsNull() {
        GameMap map = new GameMap(new int[][]{{0}});
        GameManager manager = new GameManager(map, null);
        manager.setCurrentMap(null);
        manager.setBaseHealth(10);

        SlimeEnemy enemy = new SlimeEnemy();
        enemy.setDamage(3);
        manager.getActiveEnemies().add(enemy);

        manager.update(0.1);

        assertEquals(7, manager.getBaseHealth());
        assertTrue(manager.getActiveEnemies().isEmpty());
    }

    @Test
    void updateCollectsBountyFromDeadEnemy() {
        GameMap map = new GameMap(new int[][]{{0}});
        GameManager manager = new GameManager(map, null);
        manager.setCurrentMap(null);
        manager.setPlayerMoney(0);

        SlimeEnemy deadEnemy = new SlimeEnemy();
        deadEnemy.setCurrentHealth(0);
        deadEnemy.setRewardMoney(25);
        deadEnemy.setCurrentWaypointIndex(-1);
        manager.getActiveEnemies().add(deadEnemy);

        manager.update(0.1);

        assertEquals(25, manager.getPlayerMoney());
        assertTrue(manager.getActiveEnemies().isEmpty());
    }

    @Test
    void updateSetsVictoryWhenTimerExpiresWithoutEnemies() {
        GameMap map = new GameMap(new int[][]{{0}});
        GameManager manager = new GameManager(map, null);

        manager.update(180.0);

        assertTrue(manager.isVictory());
        assertEquals(0.0, manager.getTimeRemaining());
    }
}
