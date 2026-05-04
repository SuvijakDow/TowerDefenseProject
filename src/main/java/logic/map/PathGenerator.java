package logic.map;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PathGenerator {
    private static final int ROWS = 12;
    private static final int COLS = 16;
    private static int[][] grid;
    
    // Up, Down, Left, Right
    private static final int[] dRow = {-1, 1, 0, 0};
    private static final int[] dCol = {0, 0, -1, 1};
    private static final Random rand = new Random();

    public static int[][] generateRandomPath() {
        while (true) {
            grid = new int[ROWS][COLS];
            int startR = 0, startC = 0, nextR = 0, nextC = 0;

            // Pick start edge: 0=Left, 1=Top, 2=Bottom
            int edge = rand.nextInt(3);
            if (edge == 0) { 
                startR = 1 + rand.nextInt(ROWS - 2);
                startC = 0;
                nextR = startR; nextC = 1; // Force Right
            } else if (edge == 1) { 
                startR = 0;
                startC = 1 + rand.nextInt(COLS - 2);
                nextR = 1; nextC = startC; // Force Down
            } else { 
                startR = ROWS - 1;
                startC = 1 + rand.nextInt(COLS - 2);
                nextR = ROWS - 2; nextC = startC; // Force Up
            }

            // Set start and first step
            grid[startR][startC] = 1;
            grid[nextR][nextC] = 1;

            // Start DFS. If success, return map. Else, retry.
            if (generatePath(nextR, nextC, 2, startR, startC)) {
                return grid;
            }
        }
    }

    private static boolean generatePath(int r, int c, int length, int startR, int startC) {
        // Check win conditions: Length > 40, not on edges, far from start
        if (length > 40 && r > 0 && r < ROWS - 1 && c > 0 && c < COLS - 1) {
            int dist = Math.abs(r - startR) + Math.abs(c - startC);
            if (dist > 8 && rand.nextDouble() < 0.2) {
                grid[r][c] = 2; // Place castle
                return true;
            }
        }

        // Shuffle directions
        List<Integer> dirs = Arrays.asList(0, 1, 2, 3);
        Collections.shuffle(dirs);

        for (int i : dirs) {
            int nr = r + dRow[i];
            int nc = c + dCol[i];

            if (isValid(nr, nc)) {
                grid[nr][nc] = 1; // Move forward
                if (generatePath(nr, nc, length + 1, startR, startC)) {
                    return true;
                }
                grid[nr][nc] = 0; // Backtrack
            }
        }
        return false;
    }

    private static boolean isValid(int r, int c) {
        // PREVENT EDGE TOUCHING: Restrict path to strictly inside a 1-block padding
        if (r <= 0 || r >= ROWS - 1 || c <= 0 || c >= COLS - 1) return false;
        
        // Check visited
        if (grid[r][c] != 0) return false;

        // Check neighbors (must be exactly 1 to avoid touching)
        int neighbors = 0;
        for (int i = 0; i < 4; i++) {
            int nr = r + dRow[i];
            int nc = c + dCol[i];
            if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS) {
                if (grid[nr][nc] == 1) neighbors++;
            }
        }
        return neighbors == 1; 
    }
}