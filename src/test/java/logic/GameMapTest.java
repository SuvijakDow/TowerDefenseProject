package logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameMapTest {

    @Test
    void testGenerateWaypointsFromGrid() {
        // Reason for testing: Verify that the system correctly reads a 2D array and calculates the X, Y coordinates at the center of path tiles (value 1) according to tileSize.
        int[][] grid = {
            {0, 1, 0},
            {0, 1, 0}
        };
        GameMap map = new GameMap(grid);
        map.generateWaypointsFromGrid(100);
        
        assertEquals(2, map.getPathWaypoints().size());
        
        // Col 1, Row 0 -> x = 1 * 100 + 50 = 150.0, y = 0 * 100 + 50 = 50.0
        assertEquals(150.0, map.getPathWaypoints().get(0).getX());
        assertEquals(50.0, map.getPathWaypoints().get(0).getY());
        
        // Col 1, Row 1 -> x = 1 * 100 + 50 = 150.0, y = 1 * 100 + 50 = 150.0
        assertEquals(150.0, map.getPathWaypoints().get(1).getX());
        assertEquals(150.0, map.getPathWaypoints().get(1).getY());
    }
}
