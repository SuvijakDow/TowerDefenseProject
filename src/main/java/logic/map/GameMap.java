package logic.map;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class GameMap {
    /** Must match {@code GameView.TILE_SIZE} for pixel alignment. */
    public static final int PATH_TILE_PIXEL_SIZE = 50;
    private static final int TILE_GRASS = 0;
    private static final int TILE_PATH = 1;
    private static final int TILE_CASTLE = 2;
    private static final int[][] CARDINAL_OFFSETS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    private int[][] gridLayout;
    private final List<Decoration> decorations = new ArrayList<>();
    private final List<Waypoint> pathWaypoints = new ArrayList<>();
    private Theme theme = Theme.NORMAL;

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

    /** Castle base cell ({@code 2}), or {@code null}. */
    public int[] getCastleBaseCell() {
        if (!hasGrid()) {
            return null;
        }
        return findCastleCell(gridLayout);
    }

    /**
     * 3×2 clearance aligned with castle rendering: rows {@code castleR-1..castleR},
     * cols {@code castleC-1..castleC+1}.
     */
    public static boolean isCastleClearanceTile(int row, int col, int castleR, int castleC) {
        return row >= castleR - 1 && row <= castleR && col >= castleC - 1 && col <= castleC + 1;
    }

    /** {@code true} if {@code (row,col)} lies in the castle footprint clearance zone. */
    public boolean isInCastleClearanceZone(int row, int col) {
        int[] castle = getCastleBaseCell();
        if (castle == null) {
            return false;
        }
        return isCastleClearanceTile(row, col, castle[0], castle[1]);
    }

    /**
     * {@code true} if a tower may be built at {@code (row, col)}.
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

    // Scans gridLayout (0=Grass, 1=Path) and calculates actual coordinates.
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

    public int[][] getGridLayout() {
        return gridLayout;
    }

    public void setGridLayout(int[][] gridLayout) {
        this.gridLayout = gridLayout;
    }

    public List<Decoration> getDecorations() {
        return decorations;
    }

    public List<Waypoint> getPathWaypoints() {
        return pathWaypoints;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = (theme == null) ? Theme.NORMAL : theme;
    }

    private boolean hasGrid() {
        return gridLayout != null && gridLayout.length > 0;
    }

    private boolean isInsideGrid(int row, int col) {
        return hasGrid() && row >= 0 && row < gridLayout.length && col >= 0 && col < gridLayout[row].length;
    }

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

    private static Waypoint toWaypoint(int row, int col, int tileSize) {
        double x = col * tileSize + (tileSize / 2.0);
        double y = row * tileSize + (tileSize / 2.0);
        return new Waypoint(x, y);
    }

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
     * Border tiles ({@code 1}) valid as path entrances, distinct cells, deterministic order.
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
     * BFS on cells {@code 1} (path) and {@code 2} (castle goal).
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

    private static void fillParentArrays(int[][] parentRow, int[][] parentCol) {
        for (int row = 0; row < parentRow.length; row++) {
            for (int col = 0; col < parentRow[row].length; col++) {
                parentRow[row][col] = -1;
                parentCol[row][col] = -1;
            }
        }
    }

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
