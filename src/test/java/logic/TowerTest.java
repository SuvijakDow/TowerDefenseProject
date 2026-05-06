package logic;

import logic.enemy.Enemy;
import logic.enemy.SlimeEnemy;
import logic.tower.ArcherTower;
import logic.tower.CannonTower;
import logic.tower.CrossbowTower;
import logic.tower.IceWizardTower;
import logic.tower.LightningWizardTower;
import logic.tower.PoisonWizardTower;
import logic.tower.Projectile;
import logic.tower.Tower;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TowerTest {

    @Test
    void archerTargetsClosestEnemyInRange() {
        ArcherTower tower = new ArcherTower();
        tower.setX(0);
        tower.setY(0);
        tower.setRange(100);
        tower.setCurrentCooldown(0);

        Enemy fartherEnemy = enemyAt(70, 0, 100);
        Enemy closerEnemy = enemyAt(20, 0, 100);

        List<Projectile> projectiles = new ArrayList<>();
        tower.update(List.of(fartherEnemy, closerEnemy), projectiles);

        assertEquals(1, projectiles.size());
        Projectile projectile = projectiles.get(0);
        assertEquals(closerEnemy, projectile.getTarget());
        assertEquals("Towers/Combat Towers Projectiles/spr_tower_archer_projectile.png", projectile.getSpriteName());
        assertEquals(25.0, projectile.getX());
        assertEquals(10.0, projectile.getY());
        assertEquals(tower.getFireCooldown(), tower.getCurrentCooldown());
    }

    @Test
    void towerDoesNotFireWhileCoolingDownAndCooldownTicksDown() {
        ArcherTower tower = new ArcherTower();
        tower.setX(0);
        tower.setY(0);
        tower.setCurrentCooldown(2);

        Enemy enemy = enemyAt(10, 0, 100);
        List<Projectile> projectiles = new ArrayList<>();
        tower.update(List.of(enemy), projectiles);

        assertTrue(projectiles.isEmpty());
        assertEquals(1, tower.getCurrentCooldown());
    }

    @Test
    void towerSkipsDeadEnemiesWhenChoosingTarget() {
        ArcherTower tower = new ArcherTower();
        tower.setX(0);
        tower.setY(0);
        tower.setRange(100);
        tower.setCurrentCooldown(0);

        Enemy deadClosest = enemyAt(10, 0, 0);
        Enemy aliveFurther = enemyAt(30, 0, 100);

        List<Projectile> projectiles = new ArrayList<>();
        tower.update(List.of(deadClosest, aliveFurther), projectiles);

        assertEquals(1, projectiles.size());
        assertEquals(aliveFurther, projectiles.get(0).getTarget());
    }

    @Test
    void upgradeImprovesStatsAndStopsAtMaxLevel() {
        Tower tower = new CannonTower();
        int initialDamage = tower.getDamage();
        double initialRange = tower.getRange();
        int initialFireCooldown = tower.getFireCooldown();
        int initialUpgradeCost = tower.getUpgradeCost();

        tower.upgrade();

        assertEquals(2, tower.getLevel());
        assertTrue(tower.getDamage() > initialDamage);
        assertEquals(initialRange + 10.0, tower.getRange());
        assertEquals(initialFireCooldown - 2, tower.getFireCooldown());
        assertTrue(tower.getUpgradeCost() > initialUpgradeCost);

        while (tower.canUpgrade()) {
            tower.upgrade();
        }

        int levelAtCap = tower.getLevel();
        int damageAtCap = tower.getDamage();
        double rangeAtCap = tower.getRange();
        int fireCooldownAtCap = tower.getFireCooldown();
        int upgradeCostAtCap = tower.getUpgradeCost();

        assertFalse(tower.canUpgrade());
        tower.upgrade();

        assertEquals(levelAtCap, tower.getLevel());
        assertEquals(damageAtCap, tower.getDamage());
        assertEquals(rangeAtCap, tower.getRange());
        assertEquals(fireCooldownAtCap, tower.getFireCooldown());
        assertEquals(upgradeCostAtCap, tower.getUpgradeCost());
    }

    @Test
    void everyTowerVariantUsesExpectedProjectileSprite() {
        List<Tower> towers = List.of(
                new ArcherTower(),
                new CannonTower(),
                new CrossbowTower(),
                new IceWizardTower(),
                new LightningWizardTower(),
                new PoisonWizardTower()
        );
        List<String> expectedProjectileSprites = List.of(
                "Towers/Combat Towers Projectiles/spr_tower_archer_projectile.png",
                "Towers/Combat Towers Projectiles/spr_tower_cannon_projectile.png",
                "Towers/Combat Towers Projectiles/spr_tower_crossbow_projectile.png",
                "Towers/Combat Towers Projectiles/spr_tower_ice_wizard_projectile.png",
                "Towers/Combat Towers Projectiles/spr_tower_lightning_tower_projectile.png",
                "Towers/Combat Towers Projectiles/spr_tower_poison_wizard_projectile.png"
        );

        for (int i = 0; i < towers.size(); i++) {
            Tower tower = towers.get(i);
            tower.setX(0);
            tower.setY(0);
            tower.setCurrentCooldown(0);

            Enemy enemy = enemyAt(0, 0, 100);
            List<Projectile> projectiles = new ArrayList<>();
            tower.update(List.of(enemy), projectiles);

            assertEquals(1, projectiles.size());
            assertEquals(expectedProjectileSprites.get(i), projectiles.get(0).getSpriteName());
            assertEquals(tower.getDamage(), projectiles.get(0).getDamage());
        }
    }

    @Test
    void updateHandlesNullProjectileList() {
        Tower tower = new ArcherTower();
        tower.setX(0);
        tower.setY(0);
        tower.setCurrentCooldown(0);
        Enemy enemy = enemyAt(10, 0, 100);

        assertDoesNotThrow(() -> tower.update(List.of(enemy), null));
        assertEquals(0, tower.getCurrentCooldown());
    }

    private static Enemy enemyAt(double x, double y, int hp) {
        Enemy enemy = new SlimeEnemy();
        enemy.setX(x);
        enemy.setY(y);
        enemy.setCurrentHealth(hp);
        return enemy;
    }
}
