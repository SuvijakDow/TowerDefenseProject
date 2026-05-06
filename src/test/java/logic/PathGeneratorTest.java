package logic;

import logic.map.PathGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathGeneratorTest {

    @Test
    void generatedPathHasExpectedShapeAndCastleConnectivity() {
        int[][] grid = PathGenerator.generateRandomPath();

        assertEquals(12, grid.length);
        assertEquals(16, grid[0].length);

        int castleCount = 0;
        int borderPathCount = 0;
        int castleRow = -1;
        int castleCol = -1;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                int tile = grid[row][col];
                assertTrue(tile == 0 || tile == 1 || tile == 2);
                if (tile == 2) {
                    castleCount++;
                    castleRow = row;
                    castleCol = col;
                }
                if (tile == 1 && isBorderCell(row, col, grid.length, grid[0].length)) {
                    borderPathCount++;
                }
            }
        }

        assertEquals(1, castleCount);
        assertEquals(1, borderPathCount);
        assertTrue(castleRow > 0 && castleRow < grid.length - 1);
        assertTrue(castleCol > 0 && castleCol < grid[0].length - 1);

        assertTrue(isCastleReachableFromBorderPath(grid, castleRow, castleCol));
    }

    private static boolean isBorderCell(int row, int col, int rows, int cols) {
        return row == 0 || row == rows - 1 || col == 0 || col == cols - 1;
    }

    private static boolean isCastleReachableFromBorderPath(int[][] grid, int castleRow, int castleCol) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        Queue<int[]> queue = new ArrayDeque<>();

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == 1 && isBorderCell(row, col, grid.length, grid[0].length)) {
                    queue.add(new int[]{row, col});
                    visited[row][col] = true;
                }
            }
        }

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            if (current[0] == castleRow && current[1] == castleCol) {
                return true;
            }
            for (int[] direction : directions) {
                int nextRow = current[0] + direction[0];
                int nextCol = current[1] + direction[1];
                if (nextRow < 0 || nextRow >= grid.length || nextCol < 0 || nextCol >= grid[0].length) {
                    continue;
                }
                if (visited[nextRow][nextCol]) {
                    continue;
                }
                if (grid[nextRow][nextCol] != 1 && grid[nextRow][nextCol] != 2) {
                    continue;
                }
                visited[nextRow][nextCol] = true;
                queue.add(new int[]{nextRow, nextCol});
            }
        }

        return false;
    }
}
