package logic;

import java.util.List;

// Represents an entity that can use an active skill
public interface Skillable {
    void useActiveSkill(List<Enemy> targets);
}
