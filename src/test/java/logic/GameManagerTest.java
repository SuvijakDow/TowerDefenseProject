package logic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Tests for core GameManager logic.
class GameManagerTest {

    @Test
    void initialState_shouldUseDefaultValues() {
        GameManager manager = new GameManager();

        assertEquals(500, manager.getPlayerMoney());
        assertEquals(100, manager.getBaseHealth());
        assertEquals(800.0, manager.getMapEndX());
        assertTrue(manager.getActiveEnemies().isEmpty());
        assertTrue(manager.getActiveTowers().isEmpty());
    }

    @Test
    void placeTower_shouldDeductMoneyAndAddTower() {
        GameManager manager = new GameManager();
        SniperTower tower = new SniperTower(0.0, 0.0);

        manager.placeTower(tower, 200);

        assertEquals(300, manager.getPlayerMoney());
        assertEquals(1, manager.getActiveTowers().size());
        assertEquals(tower, manager.getActiveTowers().get(0));
    }

    @Test
    void placeTower_shouldThrowWhenInsufficientFunds() {
        GameManager manager = new GameManager();

        assertThrows(IllegalArgumentException.class, () -> manager.placeTower(new SniperTower(0.0, 0.0), 501));
        assertEquals(500, manager.getPlayerMoney());
        assertTrue(manager.getActiveTowers().isEmpty());
    }

    @Test
    void spawnEnemy_shouldAddEnemyToActiveList() {
        GameManager manager = new GameManager();
        BasicEnemy enemy = new BasicEnemy(0.0, 0.0);

        manager.spawnEnemy(enemy);

        assertEquals(1, manager.getActiveEnemies().size());
        assertEquals(enemy, manager.getActiveEnemies().get(0));
    }

    @Test
    void update_shouldMoveEnemies() {
        GameManager manager = new GameManager(500, 100, 1000.0);
        BasicEnemy enemy = new BasicEnemy(100, 2.0, 5.0, 0.0);
        manager.spawnEnemy(enemy);

        manager.update();

        assertEquals(7.0, enemy.getX());
    }

    @Test
    void update_shouldMakeTowerAttackEnemyInRange() {
        GameManager manager = new GameManager(500, 100, 1000.0);
        BasicEnemy enemy = new BasicEnemy(200, 1.0, 0.0, 0.0);
        SniperTower tower = new SniperTower(40, 10.0, 1, 0.0, 0.0);
        manager.spawnEnemy(enemy);
        manager.placeTower(tower, 0);

        manager.update();

        assertEquals(160, enemy.getHealth());
        assertTrue(manager.getActiveEnemies().contains(enemy));
    }

    @Test
    void update_shouldReduceBaseHealthAndRemoveEnemyWhenReachingMapEnd() {
        GameManager manager = new GameManager();
        BasicEnemy enemy = new BasicEnemy(100, 5.0, 798.0, 0.0);
        manager.spawnEnemy(enemy);

        manager.update();

        assertEquals(99, manager.getBaseHealth());
        assertTrue(manager.getActiveEnemies().isEmpty());
    }

    @Test
    void update_shouldRewardMoneyAndRemoveDeadEnemy() {
        GameManager manager = new GameManager();
        BasicEnemy enemy = new BasicEnemy(50, 0.0, 0.0, 0.0);
        SniperTower tower = new SniperTower(60, 30.0, 1, 0.0, 0.0);
        manager.spawnEnemy(enemy);
        manager.placeTower(tower, 100);

        manager.update();

        assertEquals(425, manager.getPlayerMoney());
        assertFalse(manager.getActiveEnemies().contains(enemy));
    }

    @Test
    void update_shouldSpawnBossChildrenWhenBossDies() {
        GameManager manager = new GameManager();
        BossEnemy boss = new BossEnemy(100, 0.0, 10.0, 20.0);
        SniperTower tower = new SniperTower(200, 100.0, 1, 10.0, 20.0);
        manager.spawnEnemy(boss);
        manager.placeTower(tower, 0);

        manager.update();

        List<Enemy> activeEnemies = manager.getActiveEnemies();
        assertEquals(3, activeEnemies.size());
        assertEquals(525, manager.getPlayerMoney());
        for (Enemy enemy : activeEnemies) {
            assertInstanceOf(BasicEnemy.class, enemy);
            assertEquals(10.0, enemy.getX());
            assertEquals(20.0, enemy.getY());
            assertTrue(enemy.isAlive());
        }
    }
}
