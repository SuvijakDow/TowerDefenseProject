package logic;

import logic.map.Decoration;
import logic.map.GameMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameMapTest {

    @Test
    void generateWaypointsFromGridCalculatesTileCenters() {
        int[][] grid = {
                {0, 1, 0},
                {0, 1, 0}
        };
        GameMap map = new GameMap(grid);
        map.generateWaypointsFromGrid(100);

        assertEquals(2, map.getPathWaypoints().size());
        assertEquals(150.0, map.getPathWaypoints().get(0).getX());
        assertEquals(50.0, map.getPathWaypoints().get(0).getY());
        assertEquals(150.0, map.getPathWaypoints().get(1).getX());
        assertEquals(150.0, map.getPathWaypoints().get(1).getY());
    }

    @Test
    void isBuildableRejectsPathCastleClearanceAndDecorations() {
        int[][] grid = {
                {0, 1, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 0}
        };
        GameMap map = new GameMap(grid);

        assertTrue(map.isBuildable(0, 0, map.getDecorations()));
        assertFalse(map.isBuildable(0, 1, map.getDecorations()));
        assertFalse(map.isBuildable(1, 1, map.getDecorations()));

        map.getDecorations().add(new Decoration("tree", 0, 0, 1.0));
        assertFalse(map.isBuildable(0, 0, map.getDecorations()));
    }

    @Test
    void generatePathBuildsBfsRouteToCastle() {
        int[][] grid = {
                {0, 0, 0, 0, 0},
                {1, 1, 1, 2, 0},
                {0, 0, 0, 0, 0}
        };
        GameMap map = new GameMap(grid);

        map.generatePath();

        assertEquals(4, map.getPathWaypoints().size());
        assertEquals(25.0, map.getPathWaypoints().get(0).getX());
        assertEquals(175.0, map.getPathWaypoints().get(3).getX());
    }

    @Test
    void generatePathFallsBackToPathTileScanWhenNoCastle() {
        int[][] grid = {
                {1, 0},
                {0, 1}
        };
        GameMap map = new GameMap(grid);

        map.generatePath();

        assertEquals(2, map.getPathWaypoints().size());
        assertEquals(25.0, map.getPathWaypoints().get(0).getX());
        assertEquals(25.0, map.getPathWaypoints().get(0).getY());
        assertEquals(75.0, map.getPathWaypoints().get(1).getX());
        assertEquals(75.0, map.getPathWaypoints().get(1).getY());
    }
}
