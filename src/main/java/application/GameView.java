package application;

import javafx.scene.layout.StackPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.GameManager;
import logic.enemy.Enemy;
import logic.map.Decoration;
import logic.map.GameMap;
import logic.map.Theme;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameView extends StackPane {
    private Canvas canvas;
    private GraphicsContext gc;
    private GameManager gameManager;
    private static final int TILE_SIZE = 50;
    private static final double ENEMY_SPRITE_DRAW_SCALE = 3.0;

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

        GameMap map = gameManager.getCurrentMap();
        String grassPath;
        String pathPath;
        String castlePath;
        Theme theme = map.getTheme();
        switch (theme) {
            case AUTUMN:
                grassPath = "Environment/Grass/spr_grass_03.png";
                pathPath = "Environment/Tile Set/spr_tile_set_stone.png";
                castlePath = "Towers/Castle/spr_castle_red.png";
                break;
            case SPRING:
                grassPath = "Environment/Grass/spr_grass_01.png";
                pathPath = "Environment/Tile Set/spr_tile_set_ground.png";
                castlePath = "Towers/Castle/spr_castle_green.png";
                break;
            case NORMAL:
            default:
                grassPath = "Environment/Grass/spr_grass_02.png";
                pathPath = "Environment/Tile Set/spr_tile_set_ground.png";
                castlePath = "Towers/Castle/spr_castle_blue.png";
                break;
        }
        Image grass = assets.getImage(grassPath);
        Image groundSet = assets.getImage(pathPath);
        Image castle = assets.getImage(castlePath);

        int[][] grid = map.getGridLayout();
        List<Decoration> decorations = map.getDecorations();

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

            }
        }

        double castleDrawWidth = TILE_SIZE * 3.0;
        double castleDrawHeight = TILE_SIZE * 2.0;
        double castleDx = 0;
        double castleDy = 0;
        boolean hasCastleCell = false;

        if (castle != null) {
            outerCastle:
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (grid[r][c] == 2) {
                        castleDx = (c * TILE_SIZE) - (castleDrawWidth / 2.0) + (TILE_SIZE / 2.0);
                        castleDy = (r * TILE_SIZE) - castleDrawHeight + TILE_SIZE;
                        hasCastleCell = true;
                        break outerCastle;
                    }
                }
            }
        }

        double castleBottomY = castleDy + castleDrawHeight;
        boolean castleDrawn = false;

        List<DepthSprite> depthSprites = new ArrayList<>();
        if (decorations != null) {
            for (Decoration d : decorations) {
                depthSprites.add(DepthSprite.decoration(d, decorationBottomY(d, assets)));
            }
        }
        for (Enemy e : gameManager.getActiveEnemies()) {
            depthSprites.add(DepthSprite.enemy(e, e.getY()));
        }

        depthSprites.sort(Comparator.comparingDouble(d -> d.bottomY));

        for (DepthSprite item : depthSprites) {
            if (castle != null && hasCastleCell && !castleDrawn && item.bottomY > castleBottomY) {
                drawCastleSprite(gc, castle, castleDx, castleDy);
                castleDrawn = true;
            }

            if (item.decoration != null) {
                Decoration dec = item.decoration;
                Image img = assets.getImage(dec.getSpriteName());
                if (img != null) {
                    double drawW = img.getWidth() * dec.getScale();
                    double drawH = img.getHeight() * dec.getScale();
                    gc.drawImage(img, dec.getX(), dec.getY(), drawW, drawH);
                }
            } else if (item.enemy != null) {
                drawEnemy(gc, assets, item.enemy);
            }
        }

        if (castle != null && hasCastleCell && !castleDrawn) {
            drawCastleSprite(gc, castle, castleDx, castleDy);
        }
    }

    private static void drawEnemy(GraphicsContext gc, AssetManager assets, Enemy enemy) {
        Image img = assets.getImage(enemy.getSpriteName());
        if (img == null) {
            return;
        }
        double frameW = img.getWidth() / 4.0;
        double frameH = img.getHeight();
        double sx = enemy.getCurrentFrame() * frameW;
        double destW = frameW * ENEMY_SPRITE_DRAW_SCALE;
        double destH = frameH * ENEMY_SPRITE_DRAW_SCALE;
        double drawX = enemy.getX() - destW / 2.0;
        double drawY = enemy.getY() - destH;
        gc.drawImage(img, sx, 0, frameW, frameH, drawX, drawY, destW, destH);
    }

    private static final class DepthSprite {
        final double bottomY;
        final Decoration decoration;
        final Enemy enemy;

        private DepthSprite(double bottomY, Decoration decoration, Enemy enemy) {
            this.bottomY = bottomY;
            this.decoration = decoration;
            this.enemy = enemy;
        }

        static DepthSprite decoration(Decoration d, double bottomY) {
            return new DepthSprite(bottomY, d, null);
        }

        static DepthSprite enemy(Enemy e, double bottomY) {
            return new DepthSprite(bottomY, null, e);
        }
    }

    private static double decorationBottomY(Decoration dec, AssetManager assets) {
        Image img = assets.getImage(dec.getSpriteName());
        double h = (img != null) ? img.getHeight() * dec.getScale() : TILE_SIZE;
        return dec.getY() + h;
    }

    private static void drawCastleSprite(GraphicsContext gc, Image castle, double dx, double dy) {
        double frameWidth = castle.getWidth() / 4.0;
        double frameHeight = castle.getHeight();
        double castleDrawWidth = TILE_SIZE * 3.0;
        double castleDrawHeight = TILE_SIZE * 2.0;
        gc.drawImage(castle, 0, 0, frameWidth, frameHeight, dx, dy, castleDrawWidth, castleDrawHeight);
    }

    // Neighbor-check autotiling to select the correct tile.
    private int[] getPathTileSourceCoords(int r, int c, int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Check for path or castle (2) around the tile.
        boolean up = (r > 0) && ((grid[r - 1][c] == 1) || (grid[r - 1][c] == 2));
        boolean down = (r < rows - 1) && ((grid[r + 1][c] == 1) || (grid[r + 1][c] == 2));
        boolean left = (c > 0) && ((grid[r][c - 1] == 1) || (grid[r][c - 1] == 2));
        boolean right = (c < cols - 1) && ((grid[r][c + 1] == 1) || (grid[r][c + 1] == 2));

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
            // Horizontal end (connects right)
            sx = 16; sy = 0;
        } else if (left && !right && !up && !down) {
            // Horizontal end (connects left)
            sx = 16; sy = 0;
        } else if (up && !down && !left && !right) {
            // Vertical end (connects up)
            sx = 0; sy = 16;
        } else if (down && !up && !left && !right) {
            // Vertical end (connects down)
            sx = 0; sy = 16;
        }

        return new int[]{sx, sy};
    }
}
