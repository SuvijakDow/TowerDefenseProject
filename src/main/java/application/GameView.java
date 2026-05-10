package application;

import javafx.scene.layout.StackPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.Light;
import logic.GameManager;
import logic.enemy.Enemy;
import logic.map.Decoration;
import logic.map.GameMap;
import logic.map.Theme;
import logic.tower.Projectile;
import logic.tower.Tower;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import logic.DamageText;

import static application.Main.getString;

/**
 * JavaFX gameplay canvas that renders map entities and handles in-game pointer input.
 */
public class GameView extends StackPane {
    private Canvas canvas;
    private GraphicsContext gc;
    private GameManager gameManager;
    private static final int TILE_SIZE = 50;
    private static final int PATH_SOURCE_TILE_SIZE = 16;
    private static final int PATH_SOURCE_DEFAULT_CORD = 16;
    private static final double ENEMY_SPRITE_DRAW_SCALE = 3.0;
    private static final double PROJECTILE_DRAW_SIZE = 16.0;
    
    // Castle hit effect fields
    private double castleHitShakeX = 0;
    private double castleHitShakeTimer = 0;
    private boolean castleIsHit = false;
    private static final double CASTLE_HIT_SHAKE_DURATION = 0.3;
    private static final double CASTLE_HIT_SHAKE_INTENSITY = 8;

    private int hoverRow = -1;
    private int hoverCol = -1;
    private boolean hoverValid = false;
    private Tower selectedPlacedTower = null;
    private Consumer<Tower> placedTowerSelectionListener;
    private Font damageFont = null;

    /**
     * Creates the gameplay canvas view and wires mouse/keyboard input handlers.
     *
     * @param gameManager gameplay state owner used for interaction and rendering
     */
    public GameView(GameManager gameManager) {
        this.gameManager = gameManager;
        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        canvas.setOnMouseClicked(e -> {
            if (this.gameManager == null) {
                return;
            }
            int col = toTileIndex(e.getX());
            int row = toTileIndex(e.getY());
            if (e.getButton() == MouseButton.SECONDARY) {
                this.gameManager.setSelectedTowerType(null);
                clearPlacedTowerSelection();
                updateHover(-1, -1);
                e.consume();
                return;
            }
            if (e.getButton() == MouseButton.PRIMARY) {
                boolean towerClickHandled = togglePlacedTowerRangeAt(row, col);
                if (!towerClickHandled) {
                    clearPlacedTowerSelection();
                    this.gameManager.placeTower(row, col);
                }
                e.consume();
            }
        });

        canvas.setOnMouseMoved(e -> {
            int col = toTileIndex(e.getX());
            int row = toTileIndex(e.getY());
            updateHover(row, col);
        });

        canvas.setOnMouseExited(e -> {
            updateHover(-1, -1);
        });
        
        // Add keyboard shortcuts for tower selection
        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(this::handleKeyPress);
    }

    /**
     * Updates hover tile state used for placement preview and redraws the map.
     *
     * @param row hovered row index, or negative to clear hover
     * @param col hovered column index, or negative to clear hover
     */
    public void updateHover(int row, int col) {
        this.hoverRow = row;
        this.hoverCol = col;
        if (gameManager == null || row < 0 || col < 0) {
            hoverValid = false;
            return;
        }

        GameMap map = gameManager.getCurrentMap();
        if (map == null) {
            hoverValid = false;
            return;
        }

        int[][] grid = map.getGridLayout();
        if (grid == null || !isInsideGrid(row, col, grid)) {
            hoverValid = false;
            return;
        }

        GameManager.TowerType selectedType = gameManager.getSelectedTowerType();
        if (selectedType == null) {
            hoverValid = false;
            drawMap();
            return;
        }

        // Use the new canPlaceTower method which includes money check
        hoverValid = gameManager.canPlaceTower(row, col);

        drawMap();
    }

    /**
     * Advances the castle-hit shake animation timer.
     *
     * @param deltaTime elapsed frame time in seconds
     */
    public void updateCastleHitEffect(double deltaTime) {
        if (castleIsHit && castleHitShakeTimer > 0) {
            castleHitShakeTimer -= deltaTime;

            if (castleHitShakeTimer > 0) {
                castleHitShakeX = Math.sin(castleHitShakeTimer * 50) * CASTLE_HIT_SHAKE_INTENSITY * (castleHitShakeTimer / CASTLE_HIT_SHAKE_DURATION);
            } else {
                castleHitShakeX = 0;
                castleIsHit = false;
            }
        }
    }

    /**
     * Triggers the castle-hit shake animation from the start.
     */
    public void playCastleHitEffect() {
        castleIsHit = true;
        castleHitShakeTimer = CASTLE_HIT_SHAKE_DURATION;
    }

    /**
     * Toggles range selection for a placed tower at the given tile.
     *
     * @param row target tile row
     * @param col target tile column
     * @return {@code true} if a placed tower was clicked and selection was handled
     */
    public boolean togglePlacedTowerRangeAt(int row, int col) {
        if (gameManager == null) {
            return false;
        }
        Tower clickedTower = findTowerAtTile(row, col);
        if (clickedTower == null) {
            return false;
        }
        if (clickedTower == selectedPlacedTower) {
            selectedPlacedTower = null;
        } else {
            selectedPlacedTower = clickedTower;
        }
        drawMap();
        notifyPlacedTowerSelectionChanged();
        return true;
    }

    /**
     * Clears currently selected placed tower and notifies listeners.
     */
    public void clearPlacedTowerSelection() {
        selectedPlacedTower = null;
        drawMap();
        notifyPlacedTowerSelectionChanged();
    }

    private void notifyPlacedTowerSelectionChanged() {
        if (placedTowerSelectionListener != null) {
            placedTowerSelectionListener.accept(selectedPlacedTower);
        }
    }

    private Tower findTowerAtTile(int row, int col) {
        if (gameManager == null) {
            return null;
        }
        for (Tower tower : gameManager.getActiveTowers()) {
            if (tower.getGridRow() == row && tower.getGridCol() == col) {
                return tower;
            }
        }
        return null;
    }

    /**
     * Renders the current gameplay frame to the canvas.
     *
     * <p>When no map is available, the canvas is cleared instead of rendering entities.</p>
     */
    public void drawMap() {
        gc.setImageSmoothing(false); // Fix blurry pixel art

        if (gameManager == null) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            return;
        }

        AssetManager assets = AssetManager.getInstance();

        GameMap map = gameManager.getCurrentMap();
        if (map == null) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            return;
        }

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
                        gc.drawImage(
                                groundSet,
                                sx,
                                sy,
                                PATH_SOURCE_TILE_SIZE,
                                PATH_SOURCE_TILE_SIZE,
                                dx,
                                dy,
                                TILE_SIZE,
                                TILE_SIZE
                        );
                    }
                }

            }
        }

        // Draw hover indicator
        if (hoverRow >= 0 && hoverCol >= 0) {
            if (hoverValid) {
                gc.setFill(Color.rgb(255, 255, 255, 0.4));
            } else {
                gc.setFill(Color.rgb(255, 0, 0, 0.4));
            }
            gc.fillRect(hoverCol * TILE_SIZE, hoverRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            
            // Draw range indicator (only when valid)
            if (hoverValid && selectedPlacedTower == null) {
                double range = gameManager.getTowerRange(gameManager.getSelectedTowerType());
                double centerX = hoverCol * TILE_SIZE + TILE_SIZE / 2.0;
                double centerY = hoverRow * TILE_SIZE + TILE_SIZE / 2.0;
                drawRangeIndicator(centerX, centerY, range);
            }
            
            // Draw ghost tower preview (always show when hovering)
            drawGhostTower(gc, hoverCol * TILE_SIZE, hoverRow * TILE_SIZE);
        }

        if (selectedPlacedTower != null) {
            if (gameManager.getActiveTowers().contains(selectedPlacedTower)) {
                drawRangeIndicator(selectedPlacedTower.getX(), selectedPlacedTower.getY(), selectedPlacedTower.getRange());
            } else {
                selectedPlacedTower = null;
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
        for (Tower t : gameManager.getActiveTowers()) {
            depthSprites.add(DepthSprite.tower(t, towerBottomY(t)));
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
                    double footprintBottom = dec.getY() + TILE_SIZE / 2.0;
                    double drawX = dec.getX() - (drawW / 2.0);
                    double drawY = footprintBottom - drawH;
                    gc.drawImage(img, drawX, drawY, drawW, drawH);
                }
            } else if (item.enemy != null) {
                drawEnemy(gc, assets, item.enemy);
            } else if (item.tower != null) {
                drawTower(gc, assets, item.tower);
            }
        }

        if (castle != null && hasCastleCell && !castleDrawn) {
            drawCastleSprite(gc, castle, castleDx, castleDy);
        }

        drawProjectiles(gc, assets);
        drawDamageTexts(gc, assets);
    }

    private void drawRangeIndicator(double centerX, double centerY, double range) {
        gc.setFill(Color.rgb(255, 255, 255, 0.15));
        gc.fillOval(centerX - range, centerY - range, range * 2, range * 2);
    }

    private void drawProjectiles(GraphicsContext gc, AssetManager assets) {
        for (Projectile p : gameManager.getActiveProjectiles()) {
            Image img = assets.getImage(p.getSpriteName());
            if (img == null) {
                continue;
            }
            double half = PROJECTILE_DRAW_SIZE / 2.0;
            
            // Calculate rotation angle based on target position
            double dx = p.getTarget().getX() - p.getX();
            double dy = p.getTarget().getY() - p.getY();
            double angle = Math.toDegrees(Math.atan2(dy, dx));
            
            // Save context, translate, rotate, draw, restore
            gc.save();
            gc.translate(p.getX(), p.getY());
            gc.rotate(angle);
            gc.drawImage(img, 0, 0, img.getWidth(), img.getHeight(),
                    -half, -half, PROJECTILE_DRAW_SIZE, PROJECTILE_DRAW_SIZE);
            gc.restore();
        }
    }

    private static void drawTower(GraphicsContext gc, AssetManager assets, Tower tower) {
        Image img = assets.getImage(tower.getSpriteName());
        if (img == null) {
            return;
        }
        double iw = img.getWidth();
        double ih = img.getHeight();
        if (iw <= 0 || ih <= 0) {
            return;
        }
        double destW = TILE_SIZE;
        double destH = (ih / iw) * destW;
        double footprintBottom = tower.getY() + TILE_SIZE / 2.0;
        double drawX = tower.getX() - destW / 2.0;
        double drawY = footprintBottom - destH;
        gc.drawImage(img, 0, 0, iw, ih, drawX, drawY, destW, destH);
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

        // Draw red flash effect if enemy is hit
        if (enemy.isHit()) {
            gc.save();
            gc.setGlobalAlpha(0.6);
            gc.setFill(javafx.scene.paint.Color.rgb(255, 0, 0));
            gc.fillRect(drawX, drawY, destW, destH);
            gc.restore();
        }
    }

    private void drawCastleSprite(GraphicsContext gc, Image castle, double dx, double dy) {
        double frameWidth = castle.getWidth() / 4.0;
        double frameHeight = castle.getHeight();
        double castleDrawWidth = TILE_SIZE * 3.0;
        double castleDrawHeight = TILE_SIZE * 2.0;

        double actualX = dx + castleHitShakeX;

        if (castleIsHit) {
            Lighting redTint = new Lighting();
            Light.Distant light = new Light.Distant();

            light.setColor(Color.rgb(180, 0, 0));
            redTint.setLight(light);
            redTint.setSurfaceScale(0.0);

            gc.setEffect(redTint);
            gc.drawImage(castle, 0, 0, frameWidth, frameHeight, actualX, dy, castleDrawWidth, castleDrawHeight);
            gc.setEffect(null);
        } else {
            gc.drawImage(castle, 0, 0, frameWidth, frameHeight, actualX, dy, castleDrawWidth, castleDrawHeight);
        }
    }

    private void drawGhostTower(GraphicsContext gc, int tileX, int tileY) {
        String spritePath = getTowerSpritePath(gameManager.getSelectedTowerType());
        if (spritePath == null) {
            return;
        }

        String assetKey = normalizeAssetKey(spritePath);
        Image towerSprite = AssetManager.getInstance().getImage(assetKey);
        if (towerSprite == null || towerSprite.isError()) {
            return;
        }

        double iw = towerSprite.getWidth();
        double ih = towerSprite.getHeight();
        if (iw <= 0 || ih <= 0) {
            return;
        }

        double towerCenterX = tileX + TILE_SIZE / 2.0;
        double towerCenterY = tileY + TILE_SIZE / 2.0;

        double destW = TILE_SIZE;
        double destH = (ih / iw) * destW;
        double footprintBottom = towerCenterY + TILE_SIZE / 2.0;
        double drawX = towerCenterX - destW / 2.0;
        double drawY = footprintBottom - destH;

        gc.setGlobalAlpha(0.5);
        gc.drawImage(towerSprite, 0, 0, iw, ih, drawX, drawY, destW, destH);
        gc.setGlobalAlpha(1.0);
    }

    private void drawDamageTexts(GraphicsContext gc, AssetManager assets) {
        // Load font once and cache it
        if (damageFont == null) {
            try {
                damageFont = Font.loadFont(getClass().getResourceAsStream("/Fonts/CWEBS.TTF"), 28);
                if (damageFont == null) {
                    damageFont = new Font("Arial", 28);
                }
            } catch (Exception e) {
                damageFont = new Font("Arial", 28);
            }
        }

        gc.setFont(damageFont);

        for (DamageText damageText : gameManager.getActiveDamageTexts()) {
            if (damageText.getOpacity() <= 0) {
                continue;
            }

            gc.save();

            // Apply opacity
            gc.setGlobalAlpha(damageText.getOpacity());

            // Draw drop shadow (black outline) - optimized to fewer calls
            gc.setFill(javafx.scene.paint.Color.BLACK);
            String text = damageText.getText();
            double x = damageText.getX();
            double y = damageText.getY();

            gc.fillText(text, x + 1, y + 1);
            gc.fillText(text, x - 1, y + 1);
            gc.fillText(text, x + 1, y - 1);
            gc.fillText(text, x - 1, y - 1);

            // Draw main text
            gc.setFill(damageText.getColor());
            gc.fillText(text, x, y);

            gc.restore();
        }
    }

    private static double towerBottomY(Tower tower) {
        return tower.getY() + TILE_SIZE / 2.0;
    }

    private static double decorationBottomY(Decoration dec, AssetManager assets) {
        return dec.getY() + TILE_SIZE / 2.0;
    }

    private record DepthSprite(double bottomY, Decoration decoration, Enemy enemy, Tower tower) {

        static DepthSprite decoration(Decoration d, double bottomY) {
                return new DepthSprite(bottomY, d, null, null);
            }

            static DepthSprite enemy(Enemy e, double bottomY) {
                return new DepthSprite(bottomY, null, e, null);
            }

            static DepthSprite tower(Tower t, double bottomY) {
                return new DepthSprite(bottomY, null, null, t);
            }
        }

    private static int toTileIndex(double coordinate) {
        return (int) (coordinate / TILE_SIZE);
    }

    private static boolean isInsideGrid(int row, int col, int[][] grid) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[0].length;
    }

    private static String normalizeAssetKey(String spritePath) {
        if (spritePath.startsWith("/")) {
            return spritePath.substring(1);
        }
        return spritePath;
    }

    private void handleKeyPress(KeyEvent e) {
        switch (e.getCode()) {
            case DIGIT1:
                gameManager.setSelectedTowerType(GameManager.TowerType.ARCHER);
                break;
            case DIGIT2:
                gameManager.setSelectedTowerType(GameManager.TowerType.CANNON);
                break;
            case DIGIT3:
                gameManager.setSelectedTowerType(GameManager.TowerType.CROSSBOW);
                break;
            case DIGIT4:
                gameManager.setSelectedTowerType(GameManager.TowerType.ICE_WIZARD);
                break;
            case DIGIT5:
                gameManager.setSelectedTowerType(GameManager.TowerType.LIGHTNING_WIZARD);
                break;
            case DIGIT6:
                gameManager.setSelectedTowerType(GameManager.TowerType.POISON_WIZARD);
                break;
            default:
                break;
        }
    }

    /**
     * Sets a listener invoked whenever placed-tower selection changes.
     *
     * @param listener callback receiving selected tower or {@code null}
     */
    public void setPlacedTowerSelectionListener(Consumer<Tower> listener) {
        this.placedTowerSelectionListener = listener;
    }

    /**
     * Replaces the gameplay state source used by this view.
     *
     * @param gameManager new game manager instance
     */
    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    private String getTowerSpritePath(GameManager.TowerType towerType) {
        if (towerType == null) {
            return null;
        }
        return getString(towerType);
    }

    private int[] getPathTileSourceCoords(int r, int c, int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Check for path or castle (2) around the tile.
        boolean up = (r > 0) && ((grid[r - 1][c] == 1) || (grid[r - 1][c] == 2));
        boolean down = (r < rows - 1) && ((grid[r + 1][c] == 1) || (grid[r + 1][c] == 2));
        boolean left = (c > 0) && ((grid[r][c - 1] == 1) || (grid[r][c - 1] == 2));
        boolean right = (c < cols - 1) && ((grid[r][c + 1] == 1) || (grid[r][c + 1] == 2));

        int sx = PATH_SOURCE_DEFAULT_CORD; // Default X (center of the sprite sheet)
        int sy = PATH_SOURCE_DEFAULT_CORD; // Default Y

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
