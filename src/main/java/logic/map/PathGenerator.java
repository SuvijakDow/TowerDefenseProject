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
    /** Number of rows in the generated grid. */
    private static final int ROWS = 12;
    /** Number of columns in the generated grid. */
    private static final int COLS = 16;

    /** Constant representing a start from the left edge. */
    private static final int START_EDGE_LEFT = 0;
    /** Constant representing a start from the top edge. */
    private static final int START_EDGE_TOP = 1;
    /** Constant representing a start from the bottom edge. */
    private static final int START_EDGE_BOTTOM = 2;

    /** Minimum path steps required before considering a castle placement. */
    private static final int MIN_PATH_LENGTH_BEFORE_CASTLE = 40;
    /** Minimum manhattan distance from start before considering a castle placement. */
    private static final int MIN_CASTLE_DISTANCE_FROM_START = 8;
    /** Probability to place the castle at a valid spot per step. */
    private static final double CASTLE_PLACEMENT_CHANCE = 0.2;

    /** Row direction deltas for up, down, left, right. */
    private static final int[] DELTA_ROW = {-1, 1, 0, 0};
    /** Column direction deltas for up, down, left, right. */
    private static final int[] DELTA_COL = {0, 0, -1, 1};
    /** Random number generator used for pathing and decisions. */
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

    /**
     * Selects a random starting position on the outer border and its first inward step.
     *
     * @param random the random number generator
     * @return the computed {@link StartState}
     */
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

    /**
     * Recursively generates a random winding path through the grid via depth-first search.
     *
     * @param grid the map grid
     * @param row current row
     * @param col current col
     * @param length current path length
     * @param startRow original starting row
     * @param startCol original starting column
     * @param random the random number generator
     * @return {@code true} if a valid path ending in a castle was found, {@code false} otherwise
     */
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

    /**
     * Evaluates whether the castle can be placed at the current position.
     *
     * @param grid the map grid
     * @param row current row
     * @param col current col
     * @param length current path length
     * @param startRow original starting row
     * @param startCol original starting column
     * @param random the random number generator
     * @return {@code true} if the castle should be placed, {@code false} otherwise
     */
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

    /**
     * Checks if the next grid step is valid for continuing the path.
     *
     * @param grid the map grid
     * @param row next row to evaluate
     * @param col next col to evaluate
     * @return {@code true} if valid, {@code false} otherwise
     */
    private static boolean isValidNextStep(int[][] grid, int row, int col) {
        if (isOnOuterBorder(row, col, grid.length, grid[0].length)) {
            return false;
        }
        if (grid[row][col] != 0) {
            return false;
        }
        return countPathNeighbors(grid, row, col) == 1;
    }

    /**
     * Checks if a coordinate is directly on the outer border of the grid.
     *
     * @param row grid row
     * @param col grid column
     * @param rows total grid rows
     * @param cols total grid columns
     * @return {@code true} if on the border, {@code false} otherwise
     */
    private static boolean isOnOuterBorder(int row, int col, int rows, int cols) {
        return row <= 0 || row >= rows - 1 || col <= 0 || col >= cols - 1;
    }

    /**
     * Counts the number of adjacent path tiles for a given coordinate.
     * Used to prevent paths from clumping together.
     *
     * @param grid the map grid
     * @param row grid row
     * @param col grid column
     * @return the number of neighboring path tiles
     */
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

    /**
     * Holds the initial border starting cell and its first inward step cell.
     *
     * @param startRow the initial row on the border
     * @param startCol the initial column on the border
     * @param nextRow the first row inside the map
     * @param nextCol the first column inside the map
     */
    private record StartState(int startRow, int startCol, int nextRow, int nextCol) {
    }
}
