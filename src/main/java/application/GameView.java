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
                        // เรียกใช้งานระบบเช็คบล็อกรอบข้างเพื่อหาตำแหน่ง x, y ของรูปที่ถูกต้อง
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
                        // 1. กำหนดตัวคูณขนาดเริ่มต้น (ยิ่งค่าน้อย รูปยิ่งเล็ก)
                        double decorScale = 1.5;

                        // 2. แยกปรับขนาดตามชื่อไฟล์ เพื่อให้สัดส่วนเป๊ะเหมือนรูปต้นฉบับ
                        if (decorName.contains("mushroom")) {
                            decorScale = 3.0;
                        } else if (decorName.contains("rock")) {
                            decorScale = 3.0;
                        } else if (decorName.contains("tree")) {
                            decorScale = 2.8;
                        }

                        // 3. คำนวณความกว้างและความสูงใหม่ตามตัวคูณ
                        double drawW = decorImg.getWidth() * decorScale;
                        double drawH = decorImg.getHeight() * decorScale;

                        // 4. จัดตำแหน่งให้อยู่กึ่งกลางของช่องพอดี
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
            double castleDrawWidth = TILE_SIZE * 3.0; // กว้าง 3 บล็อกเต็มๆ
            double castleDrawHeight = TILE_SIZE * 2.0;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    // ถ้าเจอเลข 2 ให้วาดปราสาทตรงนี้!
                    if (grid[r][c] == 2) {
                        // คำนวณพิกัดให้เลข 2 อยู่กึ่งกลางด้านล่างของปราสาทพอดี
                        double dx = (c * TILE_SIZE) - (castleDrawWidth / 2.0) + (TILE_SIZE / 2.0);
                        double dy = (r * TILE_SIZE) - castleDrawHeight + TILE_SIZE;

                        gc.drawImage(castle, 0, 0, frameWidth, frameHeight, dx, dy, castleDrawWidth, castleDrawHeight);
                    }
                }
            }
        }
    }

    // ระบบเช็คบล็อกรอบข้าง เพื่อเลือกรูปกระเบื้องให้ถูกต้อง
    private int[] getPathTileSourceCoords(int r, int c, int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        // เช็คว่ามีทางเดิน หรือมีฐานทัพ (เลข 2) อยู่รอบๆ ไหม
        boolean up = (r == 0) || (grid[r - 1][c] == 1) || (grid[r - 1][c] == 2);
        boolean down = (r == rows - 1) || (grid[r + 1][c] == 1) || (grid[r + 1][c] == 2);
        boolean left = (c == 0) || (grid[r][c - 1] == 1) || (grid[r][c - 1] == 2);
        boolean right = (c == cols - 1) || (grid[r][c + 1] == 1) || (grid[r][c + 1] == 2);

        int SRC_TILE_SIZE = 16;
        int sx = 16; // ค่า Default แกน X (ตั้งไว้ตรงกลางของ Sprite Sheet)
        int sy = 16; // ค่า Default แกน Y

        // เช็คเงื่อนไขทางเดิน (อ้างอิงจาก Sprite Sheet 3x3 มาตรฐาน)
        if (left && right && !up && !down) {
            // ทางตรงแนวนอน
            sx = 16; sy = 0;
        } else if (up && down && !left && !right) {
            // ทางตรงแนวตั้ง
            sx = 0; sy = 16;
        } else if (right && down && !up && !left) {
            // โค้งซ้ายบน (เลี้ยวลง/ขวา)
            sx = 0; sy = 0;
        } else if (left && down && !up && !right) {
            // โค้งขวาบน (เลี้ยวลง/ซ้าย)
            sx = 32; sy = 0;
        } else if (right && up && !down && !left) {
            // โค้งซ้ายล่าง (เลี้ยวขึ้น/ขวา)
            sx = 0; sy = 32;
        } else if (left && up && !down && !right) {
            // โค้งขวาล่าง (เลี้ยวขึ้น/ซ้าย)
            sx = 32; sy = 32;
        } else if (right && !left && !up && !down) {
            // ปลายทางด้านซ้าย (ตัน)
            sx = 0; sy = 16;
        } else if (left && !right && !up && !down) {
            // ปลายทางด้านขวา (ตัน)
            sx = 32; sy = 16;
        }

        return new int[]{sx, sy};
    }
}
