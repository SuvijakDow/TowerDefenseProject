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

    private int[][] gridLayout;
    private List<Decoration> decorations = new ArrayList<>();
    private List<Waypoint> pathWaypoints;
    private Theme theme = Theme.AUTUMN;

    public GameMap(int[][] gridLayout) {
        this.gridLayout = gridLayout;
        this.pathWaypoints = new ArrayList<>();
    }

    /**
     * Builds an ordered path from a border path tile ({@code 1}) along walkable path/castle cells
     * to the castle ({@code 2}) via BFS. Updates {@link #pathWaypoints}. If no castle exists,
     * falls back to {@link #generateWaypointsFromGrid(int)} with {@link #PATH_TILE_PIXEL_SIZE}.
     */
    public List<Waypoint> generatePath() {
        pathWaypoints = new ArrayList<>();
        if (gridLayout == null || gridLayout.length == 0) {
            return pathWaypoints;
        }

        int[] castle = findCastleCell(gridLayout);
        if (castle == null) {
            generateWaypointsFromGrid(PATH_TILE_PIXEL_SIZE);
            return new ArrayList<>(pathWaypoints);
        }

        List<int[]> starts = borderPathStarts(gridLayout);
        List<int[]> cells = null;
        for (int[] s : starts) {
            cells = bfsPathToCastle(gridLayout, s[0], s[1], castle[0], castle[1]);
            if (!cells.isEmpty()) {
                break;
            }
        }

        if (cells == null || cells.isEmpty()) {
            generateWaypointsFromGrid(PATH_TILE_PIXEL_SIZE);
            return new ArrayList<>(pathWaypoints);
        }

        int tileSize = PATH_TILE_PIXEL_SIZE;
        for (int[] cell : cells) {
            int row = cell[0];
            int col = cell[1];
            double x = col * tileSize + (tileSize / 2.0);
            double y = row * tileSize + (tileSize / 2.0);
            pathWaypoints.add(new Waypoint(x, y));
        }
        return new ArrayList<>(pathWaypoints);
    }

    private static int[] findCastleCell(int[][] g) {
        for (int r = 0; r < g.length; r++) {
            for (int c = 0; c < g[r].length; c++) {
                if (g[r][c] == 2) {
                    return new int[] { r, c };
                }
            }
        }
        return null;
    }

    /** Border tiles ({@code 1}) valid as path entrances, distinct cells, deterministic order. */
    private static List<int[]> borderPathStarts(int[][] g) {
        int rows = g.length;
        int cols = g[0].length;
        Set<String> seen = new LinkedHashSet<>();
        List<int[]> out = new ArrayList<>();
        for (int c = 0; c < cols; c++) {
            tryAddBorderOne(g, seen, out, 0, c);
            tryAddBorderOne(g, seen, out, rows - 1, c);
        }
        for (int r = 0; r < rows; r++) {
            tryAddBorderOne(g, seen, out, r, 0);
            tryAddBorderOne(g, seen, out, r, cols - 1);
        }
        return out;
    }

    private static void tryAddBorderOne(int[][] g, Set<String> seen, List<int[]> out, int r, int c) {
        if (g[r][c] != 1) {
            return;
        }
        String key = r + "," + c;
        if (seen.add(key)) {
            out.add(new int[] { r, c });
        }
    }

    /**
     * BFS on cells {@code 1} (path) and {@code 2} (castle goal). Returns grid cells from start to
     * castle inclusive, or empty if unreachable.
     */
    private static List<int[]> bfsPathToCastle(int[][] g, int sr, int sc, int er, int ec) {
        int rows = g.length;
        int cols = g[0].length;
        boolean[][] vis = new boolean[rows][cols];
        int[][] pr = new int[rows][cols];
        int[][] pc = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                pr[r][c] = -1;
                pc[r][c] = -1;
            }
        }

        Queue<int[]> q = new ArrayDeque<>();
        int v = g[sr][sc];
        if (v != 1) {
            return new ArrayList<>();
        }

        vis[sr][sc] = true;
        pr[sr][sc] = -1;
        pc[sr][sc] = -1;
        q.add(new int[] { sr, sc });

        int[] dr = { -1, 1, 0, 0 };
        int[] dc = { 0, 0, -1, 1 };

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0];
            int c = cur[1];
            if (r == er && c == ec) {
                return reconstruct(pr, pc, sr, sc, er, ec);
            }
            for (int k = 0; k < 4; k++) {
                int nr = r + dr[k];
                int nc = c + dc[k];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols || vis[nr][nc]) {
                    continue;
                }
                int nv = g[nr][nc];
                if (nv != 1 && nv != 2) {
                    continue;
                }
                vis[nr][nc] = true;
                pr[nr][nc] = r;
                pc[nr][nc] = c;
                q.add(new int[] { nr, nc });
            }
        }
        return new ArrayList<>();
    }

    private static List<int[]> reconstruct(int[][] pr, int[][] pc, int sr, int sc, int er, int ec) {
        List<int[]> rev = new ArrayList<>();
        int r = er;
        int c = ec;
        while (r >= 0) {
            rev.add(new int[] { r, c });
            if (r == sr && c == sc) {
                break;
            }
            int pbr = pr[r][c];
            int pbc = pc[r][c];
            if (pbr < 0) {
                break;
            }
            r = pbr;
            c = pbc;
        }
        java.util.Collections.reverse(rev);
        return rev;
    }

    // Scans gridLayout (0=Grass, 1=Path) and calculates actual coordinates
    public void generateWaypointsFromGrid(int tileSize) {
        pathWaypoints.clear();
        if (gridLayout == null) return;
        
        for (int row = 0; row < gridLayout.length; row++) {
            for (int col = 0; col < gridLayout[row].length; col++) {
                if (gridLayout[row][col] == 1) {
                    double x = col * tileSize + (tileSize / 2.0);
                    double y = row * tileSize + (tileSize / 2.0);
                    pathWaypoints.add(new Waypoint(x, y));
                }
            }
        }
    }

    public int[][] getGridLayout() { return gridLayout; }
    public void setGridLayout(int[][] gridLayout) { this.gridLayout = gridLayout; }
    public List<Decoration> getDecorations() { return decorations; }
    public List<Waypoint> getPathWaypoints() { return pathWaypoints; }
    public Theme getTheme() { return theme; }
    public void setTheme(Theme theme) { this.theme = (theme == null) ? Theme.NORMAL : theme; }
}
