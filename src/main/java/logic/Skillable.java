package logic;

import java.util.List;

// Contract for entities that can use an active skill.
public interface Skillable {
    // Uses an active skill against the provided enemies.
    void useActiveSkill(List<Enemy> allEnemies);
}
