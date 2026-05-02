package application;

import javafx.scene.layout.StackPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.GameManager;
import logic.GameMap;
import logic.Waypoint;

import java.util.List;
import java.util.Random;

public class GameView extends StackPane {
    private Canvas canvas;
    private GraphicsContext gc;
    private GameManager gameManager;
    private static final int TILE_SIZE = 50;

    public GameView(GameManager gameManager) {
        this.gameManager = gameManager;
        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        drawMap();
    }

    public void drawMap() {
        gc.setImageSmoothing(false); // Fix blurry pixel art

        AssetManager assets = AssetManager.getInstance();
        Image grass = assets.getImage("spr_grass_02.png");
        Image groundSet = assets.getImage("spr_tile_set_ground.png");
        Image castle = assets.getImage("spr_castle_blue.png");
        Image rock = assets.getImage("spr_rock_01.png");
        Image tree = assets.getImage("spr_tree_01_normal.png");

        GameMap map = gameManager.getCurrentMap();
        int[][] grid = map.getGridLayout();
        String[][] decorGrid = map.getDecorationGrid();

        if (grid == null) return;

        int rows = grid.length;
        int cols = grid[0].length;

        // Tile background and draw path
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double dx = c * TILE_SIZE;
                double dy = r * TILE_SIZE;
                
                // Draw grass everywhere first
                if (grass != null) {
                    gc.drawImage(grass, dx, dy, TILE_SIZE, TILE_SIZE);
                }

                if (grid[r][c] == 1) {
                    if (groundSet != null) {
                        // Use neighbor checks to select the correct source x, y.
                        int[] srcCoords = getPathTileSourceCoords(r, c, grid);
                        int sx = srcCoords[0];
                        int sy = srcCoords[1];
                        gc.drawImage(groundSet, sx, sy, 16, 16, dx, dy, TILE_SIZE, TILE_SIZE);
                    }
                }

                // Draw decorations if any
                if (decorGrid != null && decorGrid[r][c] != null) {
                    String decorName = decorGrid[r][c];
                    Image decorImg = assets.getImage(decorName);

                    if (decorImg != null) {
                        // 1. Set the base scale multiplier (smaller = smaller image).
                        double decorScale = 1.5;

                        // 2. Adjust scale by asset name to preserve the original proportions.
                        if (decorName.contains("mushroom")) {
                            decorScale = 3.0;
                        } else if (decorName.contains("rock")) {
                            decorScale = 3.0;
                        } else if (decorName.contains("tree")) {
                            decorScale = 2.8;
                        }

                        // 3. Compute new width and height from the scale.
                        double drawW = decorImg.getWidth() * decorScale;
                        double drawH = decorImg.getHeight() * decorScale;

                        // 4. Center within the tile.
                        double drawX = dx + (TILE_SIZE - drawW) / 2.0;
                        double drawY = dy + (TILE_SIZE - drawH) / 2.0;

                        gc.drawImage(decorImg, drawX, drawY, drawW, drawH);
                    }
                }
            }
        }

        // Draw castle at the exact coordinate marked by '2'
        if (castle != null) {
            double frameWidth = castle.getWidth() / 4.0;
            double frameHeight = castle.getHeight();
            double castleDrawWidth = TILE_SIZE * 3.0; // Exactly 3 tiles wide
            double castleDrawHeight = TILE_SIZE * 2.0;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    // If you find a 2, draw the castle here.
                    if (grid[r][c] == 2) {
                        // Position so the 2 cell aligns with the castle bottom-center.
                        double dx = (c * TILE_SIZE) - (castleDrawWidth / 2.0) + (TILE_SIZE / 2.0);
                        double dy = (r * TILE_SIZE) - castleDrawHeight + TILE_SIZE;

                        gc.drawImage(castle, 0, 0, frameWidth, frameHeight, dx, dy, castleDrawWidth, castleDrawHeight);
                    }
                }
            }
        }
    }

    // Neighbor-check autotiling to select the correct tile.
    private int[] getPathTileSourceCoords(int r, int c, int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Check for path or castle (2) around the tile.
        boolean up = (r == 0) || (grid[r - 1][c] == 1) || (grid[r - 1][c] == 2);
        boolean down = (r == rows - 1) || (grid[r + 1][c] == 1) || (grid[r + 1][c] == 2);
        boolean left = (c == 0) || (grid[r][c - 1] == 1) || (grid[r][c - 1] == 2);
        boolean right = (c == cols - 1) || (grid[r][c + 1] == 1) || (grid[r][c + 1] == 2);

        int SRC_TILE_SIZE = 16;
        int sx = 16; // Default X (center of the sprite sheet)
        int sy = 16; // Default Y

        // Path adjacency rules (based on a standard 3x3 sprite sheet).
        if (left && right && !up && !down) {
            // Horizontal straight
            sx = 16; sy = 0;
        } else if (up && down && !left && !right) {
            // Vertical straight
            sx = 0; sy = 16;
        } else if (right && down && !up && !left) {
            // Top-left corner (turns down/right)
            sx = 0; sy = 0;
        } else if (left && down && !up && !right) {
            // Top-right corner (turns down/left)
            sx = 32; sy = 0;
        } else if (right && up && !down && !left) {
            // Bottom-left corner (turns up/right)
            sx = 0; sy = 32;
        } else if (left && up && !down && !right) {
            // Bottom-right corner (turns up/left)
            sx = 32; sy = 32;
        } else if (right && !left && !up && !down) {
            // Left end (dead end)
            sx = 0; sy = 16;
        } else if (left && !right && !up && !down) {
            // Right end (dead end)
            sx = 32; sy = 16;
        }

        return new int[]{sx, sy};
    }
}
