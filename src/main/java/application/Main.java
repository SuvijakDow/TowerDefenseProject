package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.GameManager;
import logic.GameMap;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Sample 2D int array map (0=grass, 1=path)
        int[][] gridLayout = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // จุดเกิดศัตรู
                {0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 2, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // <--- แก้ไขจุดนี้ เติม 0 ให้ครบ 16 ตัวแล้วครับ
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        GameMap gameMap = new GameMap(gridLayout);

        // สร้างรายการของตกแต่งที่ต้องการให้สุ่มโผล่ขึ้นมา (คุณเพิ่มเห็ด หรือต้นไม้ฤดูใบไม้ร่วงเข้าไปได้เลย)
        String[] decorPool = {
                "spr_rock_01.png",
                "spr_tree_01_normal.png",
                "spr_rock_01.png",
                "spr_rock_02.png",
                "spr_rock_03.png",
                "spr_tree_01_normal.png",
                "spr_tree_02_normal.png",
                "spr_tree_01_autumn.png",
                "spr_mushroom_01.png",
                "spr_mushroom_02.png"
        };

        java.util.Random rand = new java.util.Random();
        String[][] decorGrid = gameMap.getDecorationGrid();

        // อัลกอริทึมวิ่งเช็คตาราง 2D Matrix
        for (int r = 0; r < gridLayout.length; r++) {
            for (int c = 0; c < gridLayout[0].length; c++) {
                // เงื่อนไขสำคัญ: วางของตกแต่งเฉพาะบน "หญ้า" (เลข 0) เท่านั้น!
                if (gridLayout[r][c] == 0) {
                    // กำหนดโอกาสเกิด (เช่น 0.15 คือมีโอกาส 15% ที่ช่องหญ้านี้จะมีของตกแต่งโผล่มา)
                    if (rand.nextDouble() < 0.20) {
                        // สุ่มหยิบของตกแต่งจาก decorPool มา 1 ชิ้น
                        String randomDecor = decorPool[rand.nextInt(decorPool.length)];
                        decorGrid[r][c] = randomDecor;
                    }
                }
            }
        }

        gameMap.generateWaypointsFromGrid(50); // TILE_SIZE is 50 in GameView
        GameManager gameManager = new GameManager(gameMap);

        GameView gameView = new GameView(gameManager);

        Scene scene = new Scene(gameView, 800, 600);
        primaryStage.setTitle("Tower Defense");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
