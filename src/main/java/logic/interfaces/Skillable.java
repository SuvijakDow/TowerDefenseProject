package logic.interfaces;

import java.util.List;

import logic.enemy.Enemy;

// Represents an entity that can use an active skill
public interface Skillable {
    void useActiveSkill(List<Enemy> targets);
}
