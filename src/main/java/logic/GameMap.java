package logic;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private int[][] gridLayout;
    private List<Decoration> decorations = new ArrayList<>();
    private List<Waypoint> pathWaypoints;
    private Theme theme = Theme.SPRING;

    public GameMap(int[][] gridLayout) {
        this.gridLayout = gridLayout;
        this.pathWaypoints = new ArrayList<>();
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
