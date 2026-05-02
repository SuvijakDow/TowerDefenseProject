package logic;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private int[][] gridLayout;
    private String[][] decorationGrid;
    private List<Waypoint> pathWaypoints;

    public GameMap(int[][] gridLayout) {
        this.gridLayout = gridLayout;
        this.decorationGrid = new String[gridLayout.length][gridLayout[0].length];
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
    public String[][] getDecorationGrid() { return decorationGrid; }
    public void setDecorationGrid(String[][] decorationGrid) { this.decorationGrid = decorationGrid; }
    public List<Waypoint> getPathWaypoints() { return pathWaypoints; }
}
