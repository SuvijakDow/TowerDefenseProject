package logic.map;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Mutable map model containing grid tiles, build decorations, and computed path waypoints.
 *
 * <p>Tile encoding: { 0}=grass, { 1}=path, { 2}=castle.</p>
 */
public class GameMap {
    /** Must match {@code GameView.TILE_SIZE} for pixel alignment. */
    public static final int PATH_TILE_PIXEL_SIZE = 50;
    /** Integer value representing a grass tile. */
    private static final int TILE_GRASS = 0;
    /** Integer value representing a path tile. */
    private static final int TILE_PATH = 1;
    /** Integer value representing the castle/base tile. */
    private static final int TILE_CASTLE = 2;
    /** Offsets for checking adjacent tiles in cardinal directions (up, down, left, right). */
    private static final int[][] CARDINAL_OFFSETS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    /** The 2D array representing the map grid tile types. */
    private int[][] gridLayout;
    /** List of all decorations currently placed on the map. */
    private final List<Decoration> decorations = new ArrayList<>();
    /** List of computed path waypoints for enemies to follow. */
    private final List<Waypoint> pathWaypoints = new ArrayList<>();
    /** The visual theme of the map. */
    private Theme theme = Theme.NORMAL;

    /**
     * Creates a map backed by the provided tile grid.
     *
     * @param gridLayout map grid data
     */
    public GameMap(int[][] gridLayout) {
        this.gridLayout = gridLayout;
    }

    /**
     * Builds an ordered path from a border path tile ({@code 1}) to castle ({@code 2}) via BFS.
     * If no castle path is found, falls back to {@link #generateWaypointsFromGrid(int)}.
     */
    public List<Waypoint> generatePath() {
        pathWaypoints.clear();
        if (!hasGrid()) {
            return new ArrayList<>(pathWaypoints);
        }

        int[] castleCell = findCastleCell(gridLayout);
        if (castleCell == null) {
            generateWaypointsFromGrid(PATH_TILE_PIXEL_SIZE);
            return new ArrayList<>(pathWaypoints);
        }

        List<int[]> pathCells = findFirstBorderPathToCastle(castleCell);
        if (pathCells.isEmpty()) {
            generateWaypointsFromGrid(PATH_TILE_PIXEL_SIZE);
            return new ArrayList<>(pathWaypoints);
        }

        for (int[] cell : pathCells) {
            pathWaypoints.add(toWaypoint(cell[0], cell[1], PATH_TILE_PIXEL_SIZE));
        }
        return new ArrayList<>(pathWaypoints);
    }

    /**
     * Gets the castle base cell coordinates.
     *
     * @return the castle base cell ({@code 2}), or {@code null} if none
     */
    public int[] getCastleBaseCell() {
        if (!hasGrid()) {
            return null;
        }
        return findCastleCell(gridLayout);
    }

    /**
     * Checks if a tile is within the 3x2 clearance zone aligned with castle rendering.
     * Clearance zone: rows {@code castleR-1..castleR}, cols {@code castleC-1..castleC+1}.
     *
     * @param row the row to check
     * @param col the column to check
     * @param castleR the castle's base row
     * @param castleC the castle's base column
     * @return {@code true} if within clearance, {@code false} otherwise
     */
    public static boolean isCastleClearanceTile(int row, int col, int castleR, int castleC) {
        return row >= castleR - 1 && row <= castleR && col >= castleC - 1 && col <= castleC + 1;
    }

    /**
     * Checks if {@code (row,col)} lies in the castle footprint clearance zone.
     *
     * @param row the row to check
     * @param col the column to check
     * @return {@code true} if within clearance zone, {@code false} otherwise
     */
    public boolean isInCastleClearanceZone(int row, int col) {
        int[] castle = getCastleBaseCell();
        if (castle == null) {
            return false;
        }
        return isCastleClearanceTile(row, col, castle[0], castle[1]);
    }

    /**
     * Checks if a tower may be built at {@code (row, col)}.
     *
     * @param row the grid row
     * @param col the grid column
     * @param decorations the list of map decorations
     * @return {@code true} if buildable, {@code false} otherwise
     */
    public boolean isBuildable(int row, int col, List<Decoration> decorations) {
        if (!isInsideGrid(row, col)) {
            return false;
        }
        if (gridLayout[row][col] != TILE_GRASS || isInCastleClearanceZone(row, col)) {
            return false;
        }
        return !hasDecorationAt(row, col, decorations);
    }

    /**
     * Scans the gridLayout and generates waypoints for all path tiles, primarily as a fallback.
     *
     * @param tileSize the size of a grid tile in pixels
     */
    public void generateWaypointsFromGrid(int tileSize) {
        pathWaypoints.clear();
        if (!hasGrid()) {
            return;
        }

        for (int row = 0; row < gridLayout.length; row++) {
            for (int col = 0; col < gridLayout[row].length; col++) {
                if (gridLayout[row][col] == TILE_PATH) {
                    pathWaypoints.add(toWaypoint(row, col, tileSize));
                }
            }
        }
    }

    /**
     * Gets the grid layout data.
     *
     * @return the 2D array of tile integers
     */
    public int[][] getGridLayout() {
        return gridLayout;
    }

    /**
     * Sets the grid layout data.
     *
     * @param gridLayout the new grid layout
     */
    public void setGridLayout(int[][] gridLayout) {
        this.gridLayout = gridLayout;
    }

    /**
     * Gets the list of active map decorations.
     *
     * @return the decorations list
     */
    public List<Decoration> getDecorations() {
        return decorations;
    }

    /**
     * Gets the ordered list of computed path waypoints.
     *
     * @return the waypoints list
     */
    public List<Waypoint> getPathWaypoints() {
        return pathWaypoints;
    }

    /**
     * Gets the current visual theme of the map.
     *
     * @return the map theme
     */
    public Theme getTheme() {
        return theme;
    }

    /**
     * Sets the visual theme of the map.
     *
     * @param theme the map theme (defaults to NORMAL if null)
     */
    public void setTheme(Theme theme) {
        this.theme = (theme == null) ? Theme.NORMAL : theme;
    }

    /**
     * Checks if the map has a valid grid layout.
     *
     * @return {@code true} if a grid is present, {@code false} otherwise
     */
    private boolean hasGrid() {
        return gridLayout != null && gridLayout.length > 0;
    }

    /**
     * Checks if the specified row and column are within the grid bounds.
     *
     * @param row the grid row
     * @param col the grid column
     * @return {@code true} if inside the grid, {@code false} otherwise
     */
    private boolean isInsideGrid(int row, int col) {
        return hasGrid() && row >= 0 && row < gridLayout.length && col >= 0 && col < gridLayout[row].length;
    }

    /**
     * Checks if a decoration exists at the specified grid cell.
     *
     * @param row the grid row
     * @param col the grid column
     * @param decorations the list of decorations to search
     * @return {@code true} if a decoration is present, {@code false} otherwise
     */
    private static boolean hasDecorationAt(int row, int col, List<Decoration> decorations) {
        if (decorations == null) {
            return false;
        }
        for (Decoration decoration : decorations) {
            if (decoration.getRow() == row && decoration.getCol() == col) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts a grid coordinate into a world waypoint centered on the tile.
     *
     * @param row the grid row
     * @param col the grid column
     * @param tileSize the size of a grid tile in pixels
     * @return the created waypoint
     */
    private static Waypoint toWaypoint(int row, int col, int tileSize) {
        double x = col * tileSize + (tileSize / 2.0);
        double y = row * tileSize + (tileSize / 2.0);
        return new Waypoint(x, y);
    }

    /**
     * Finds the first valid path from any map border to the castle.
     *
     * @param castleCell the coordinate of the castle
     * @return a list of path cell coordinates forming the route
     */
    private List<int[]> findFirstBorderPathToCastle(int[] castleCell) {
        for (int[] startCell : borderPathStarts(gridLayout)) {
            List<int[]> cells = bfsPathToCastle(
                    gridLayout,
                    startCell[0],
                    startCell[1],
                    castleCell[0],
                    castleCell[1]
            );
            if (!cells.isEmpty()) {
                return cells;
            }
        }
        return List.of();
    }

    /**
     * Locates the first cell containing the castle tile ID.
     *
     * @param grid the map grid
     * @return the coordinate of the castle cell, or null if not found
     */
    private static int[] findCastleCell(int[][] grid) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == TILE_CASTLE) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    /**
     * Collects all path tiles that reside on the outer border of the grid.
     *
     * @param grid the map grid
     * @return a list of border path cell coordinates
     */
    private static List<int[]> borderPathStarts(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        Set<Integer> seen = new LinkedHashSet<>();
        List<int[]> starts = new ArrayList<>();

        for (int col = 0; col < cols; col++) {
            tryAddBorderPathStart(grid, seen, starts, 0, col);
            tryAddBorderPathStart(grid, seen, starts, rows - 1, col);
        }
        for (int row = 0; row < rows; row++) {
            tryAddBorderPathStart(grid, seen, starts, row, 0);
            tryAddBorderPathStart(grid, seen, starts, row, cols - 1);
        }
        return starts;
    }

    /**
     * Attempts to register a path start at the specified border coordinate if valid.
     *
     * @param grid the map grid
     * @param seen a set to prevent duplicate starts
     * @param starts the list to populate with valid starts
     * @param row the grid row to test
     * @param col the grid column to test
     */
    private static void tryAddBorderPathStart(
            int[][] grid,
            Set<Integer> seen,
            List<int[]> starts,
            int row,
            int col
    ) {
        if (grid[row][col] != TILE_PATH) {
            return;
        }
        int key = row * grid[0].length + col;
        if (seen.add(key)) {
            starts.add(new int[]{row, col});
        }
    }

    /**
     * Finds the shortest contiguous path to the castle using Breadth-First Search.
     *
     * @param grid the map grid
     * @param sr the starting row
     * @param sc the starting column
     * @param er the ending row
     * @param ec the ending column
     * @return a list of path cell coordinates from start to end
     */
    private static List<int[]> bfsPathToCastle(int[][] grid, int sr, int sc, int er, int ec) {
        int rows = grid.length;
        int cols = grid[0].length;
        if (grid[sr][sc] != TILE_PATH) {
            return List.of();
        }

        boolean[][] visited = new boolean[rows][cols];
        int[][] parentRow = new int[rows][cols];
        int[][] parentCol = new int[rows][cols];
        fillParentArrays(parentRow, parentCol);

        Queue<int[]> queue = new ArrayDeque<>();
        visited[sr][sc] = true;
        queue.add(new int[]{sr, sc});

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];
            if (row == er && col == ec) {
                return reconstructPath(parentRow, parentCol, sr, sc, er, ec);
            }

            for (int[] offset : CARDINAL_OFFSETS) {
                int nextRow = row + offset[0];
                int nextCol = col + offset[1];
                if (nextRow < 0 || nextRow >= rows || nextCol < 0 || nextCol >= cols) {
                    continue;
                }
                if (visited[nextRow][nextCol]) {
                    continue;
                }
                int tileValue = grid[nextRow][nextCol];
                if (tileValue != TILE_PATH && tileValue != TILE_CASTLE) {
                    continue;
                }

                visited[nextRow][nextCol] = true;
                parentRow[nextRow][nextCol] = row;
                parentCol[nextRow][nextCol] = col;
                queue.add(new int[]{nextRow, nextCol});
            }
        }
        return List.of();
    }

    /**
     * Initializes the parent tracking arrays for BFS path reconstruction.
     *
     * @param parentRow the parent row tracking array
     * @param parentCol the parent column tracking array
     */
    private static void fillParentArrays(int[][] parentRow, int[][] parentCol) {
        for (int row = 0; row < parentRow.length; row++) {
            for (int col = 0; col < parentRow[row].length; col++) {
                parentRow[row][col] = -1;
                parentCol[row][col] = -1;
            }
        }
    }

    /**
     * Reconstructs the computed path from the target back to the start.
     *
     * @param parentRow the parent row tracking array
     * @param parentCol the parent column tracking array
     * @param sr the starting row
     * @param sc the starting column
     * @param er the ending row
     * @param ec the ending column
     * @return the ordered list of path cell coordinates
     */
    private static List<int[]> reconstructPath(int[][] parentRow, int[][] parentCol, int sr, int sc, int er, int ec) {
        List<int[]> reversed = new ArrayList<>();
        int row = er;
        int col = ec;

        while (row >= 0) {
            reversed.add(new int[]{row, col});
            if (row == sr && col == sc) {
                break;
            }
            int previousRow = parentRow[row][col];
            int previousCol = parentCol[row][col];
            if (previousRow < 0 || previousCol < 0) {
                break;
            }
            row = previousRow;
            col = previousCol;
        }

        java.util.Collections.reverse(reversed);
        return reversed;
    }
}
