package logic;

import java.util.List;

// Magic tower with normal attacks plus an active skill.
public class MageTower extends Tower implements Skillable {
    private static final int DEFAULT_DAMAGE = 90;
    private static final double DEFAULT_RANGE = 8.0;
    private static final int DEFAULT_COOLDOWN = 2;
    private static final int DEFAULT_ACTIVE_SKILL_DAMAGE = 300;

    private int activeSkillDamage;

    // Creates a mage tower with default stats.
    public MageTower(double x, double y) {
        this(DEFAULT_DAMAGE, DEFAULT_RANGE, DEFAULT_COOLDOWN, DEFAULT_ACTIVE_SKILL_DAMAGE, x, y);
    }

    // Creates a mage tower with custom stats.
    public MageTower(int damage, double range, int attackCooldown, int activeSkillDamage, double x, double y) {
        super(damage, range, attackCooldown, x, y);
        setActiveSkillDamage(activeSkillDamage);
    }

    // Returns active-skill damage.
    public int getActiveSkillDamage() {
        return activeSkillDamage;
    }

    // Sets active-skill damage.
    public void setActiveSkillDamage(int activeSkillDamage) {
        if (activeSkillDamage < 0) {
            throw new IllegalArgumentException("activeSkillDamage must be >= 0.");
        }
        this.activeSkillDamage = activeSkillDamage;
    }

    // Attacks the first alive enemy found within range.
    @Override
    public void attack(List<Enemy> targets) {
        if (targets == null) {
            throw new IllegalArgumentException("targets must not be null.");
        }

        for (Enemy enemy : targets) {
            if (enemy == null || !enemy.isAlive()) {
                continue;
            }
            if (distanceTo(enemy) <= range) {
                enemy.takeDamage(damage);
                return;
            }
        }
    }

    // Deals massive damage to all alive enemies in the provided list.
    @Override
    public void useActiveSkill(List<Enemy> allEnemies) {
        if (allEnemies == null) {
            throw new IllegalArgumentException("allEnemies must not be null.");
        }

        for (Enemy enemy : allEnemies) {
            if (enemy == null || !enemy.isAlive()) {
                continue;
            }
            enemy.takeDamage(activeSkillDamage);
        }
    }

    // Calculates distance from this tower to an enemy.
    private double distanceTo(Enemy enemy) {
        return Math.hypot(enemy.getX() - x, enemy.getY() - y);
    }
}
