package logic.map;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates randomized playable path layouts for tower-defense maps.
 *
 * <p>Generated grids use tile values: {@code 0}=grass, {@code 1}=path, {@code 2}=castle.</p>
 */
public final class PathGenerator {
    private static final int ROWS = 12;
    private static final int COLS = 16;

    private static final int START_EDGE_LEFT = 0;
    private static final int START_EDGE_TOP = 1;
    private static final int START_EDGE_BOTTOM = 2;

    private static final int MIN_PATH_LENGTH_BEFORE_CASTLE = 40;
    private static final int MIN_CASTLE_DISTANCE_FROM_START = 8;
    private static final double CASTLE_PLACEMENT_CHANCE = 0.2;

    // Up, Down, Left, Right
    private static final int[] DELTA_ROW = {-1, 1, 0, 0};
    private static final int[] DELTA_COL = {0, 0, -1, 1};
    private static final Random RANDOM = new Random();

    private PathGenerator() {
    }

    /**
     * Produces a random grid containing a valid path and one castle tile.
     *
     * @return generated map grid
     */
    public static int[][] generateRandomPath() {
        while (true) {
            int[][] grid = new int[ROWS][COLS];
            StartState start = createStartState(RANDOM);
            grid[start.startRow()][start.startCol()] = 1;
            grid[start.nextRow()][start.nextCol()] = 1;

            if (generatePath(grid, start.nextRow(), start.nextCol(), 2, start.startRow(), start.startCol(), RANDOM)) {
                return grid;
            }
        }
    }

    private static StartState createStartState(Random random) {
        int edge = random.nextInt(3);

        if (edge == START_EDGE_LEFT) {
            int startRow = 1 + random.nextInt(ROWS - 2);
            return new StartState(startRow, 0, startRow, 1);
        }
        if (edge == START_EDGE_TOP) {
            int startCol = 1 + random.nextInt(COLS - 2);
            return new StartState(0, startCol, 1, startCol);
        }

        int startCol = 1 + random.nextInt(COLS - 2);
        return new StartState(ROWS - 1, startCol, ROWS - 2, startCol);
    }

    private static boolean generatePath(
            int[][] grid,
            int row,
            int col,
            int length,
            int startRow,
            int startCol,
            Random random
    ) {
        if (canPlaceCastle(grid, row, col, length, startRow, startCol, random)) {
            grid[row][col] = 2;
            return true;
        }

        List<Integer> directions = Arrays.asList(0, 1, 2, 3);
        Collections.shuffle(directions, random);

        for (int direction : directions) {
            int nextRow = row + DELTA_ROW[direction];
            int nextCol = col + DELTA_COL[direction];
            if (!isValidNextStep(grid, nextRow, nextCol)) {
                continue;
            }

            grid[nextRow][nextCol] = 1;
            if (generatePath(grid, nextRow, nextCol, length + 1, startRow, startCol, random)) {
                return true;
            }
            grid[nextRow][nextCol] = 0;
        }
        return false;
    }

    private static boolean canPlaceCastle(
            int[][] grid,
            int row,
            int col,
            int length,
            int startRow,
            int startCol,
            Random random
    ) {
        if (length <= MIN_PATH_LENGTH_BEFORE_CASTLE) {
            return false;
        }
        if (isOnOuterBorder(row, col, grid.length, grid[0].length)) {
            return false;
        }
        int manhattanDistance = Math.abs(row - startRow) + Math.abs(col - startCol);
        if (manhattanDistance <= MIN_CASTLE_DISTANCE_FROM_START) {
            return false;
        }
        return random.nextDouble() < CASTLE_PLACEMENT_CHANCE;
    }

    private static boolean isValidNextStep(int[][] grid, int row, int col) {
        if (isOnOuterBorder(row, col, grid.length, grid[0].length)) {
            return false;
        }
        if (grid[row][col] != 0) {
            return false;
        }
        return countPathNeighbors(grid, row, col) == 1;
    }

    private static boolean isOnOuterBorder(int row, int col, int rows, int cols) {
        return row <= 0 || row >= rows - 1 || col <= 0 || col >= cols - 1;
    }

    private static int countPathNeighbors(int[][] grid, int row, int col) {
        int neighbors = 0;
        for (int i = 0; i < DELTA_ROW.length; i++) {
            int neighborRow = row + DELTA_ROW[i];
            int neighborCol = col + DELTA_COL[i];
            if (neighborRow < 0 || neighborRow >= grid.length || neighborCol < 0 || neighborCol >= grid[0].length) {
                continue;
            }
            if (grid[neighborRow][neighborCol] == 1) {
                neighbors++;
            }
        }
        return neighbors;
    }

    private record StartState(int startRow, int startCol, int nextRow, int nextCol) {
    }
}
