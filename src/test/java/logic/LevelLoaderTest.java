package logic;

import logic.map.LevelLoader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LevelLoaderTest {

    @Test
    void loadMapGridParsesValidResource() {
        int[][] grid = LevelLoader.loadMapGrid("/Paths/path1.txt");

        assertEquals(12, grid.length);
        assertEquals(16, grid[0].length);
        assertEquals(1, grid[1][0]);
        assertEquals(2, grid[2][14]);
    }

    @Test
    void loadMapGridReturnsFallbackForBlankNullOrMissingPath() {
        assertFallback(LevelLoader.loadMapGrid(""));
        assertFallback(LevelLoader.loadMapGrid(null));
        assertFallback(LevelLoader.loadMapGrid("/Paths/does_not_exist.txt"));
    }

    @Test
    void loadMapGridReturnsFallbackForInvalidFileShapes() {
        assertFallback(LevelLoader.loadMapGrid("/Paths/invalid_columns.txt"));
        assertFallback(LevelLoader.loadMapGrid("/Paths/invalid_number.txt"));
        assertFallback(LevelLoader.loadMapGrid("/Paths/empty_map.txt"));
    }

    private static void assertFallback(int[][] grid) {
        assertEquals(1, grid.length);
        assertEquals(1, grid[0].length);
        assertEquals(0, grid[0][0]);
    }
}
