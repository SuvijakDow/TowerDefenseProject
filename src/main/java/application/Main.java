package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.GameManager;
import logic.GameMap;
import logic.LevelLoader;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        int[][] gridLayout = LevelLoader.loadMapGrid("/maps/level1.txt");
        GameMap gameMap = new GameMap(gridLayout);

        // Create a pool of decorations to spawn randomly (add mushrooms or autumn trees here).
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

        // Iterate through the 2D grid
        for (int r = 0; r < gridLayout.length; r++) {
            for (int c = 0; c < gridLayout[0].length; c++) {
                // Important: place decorations only on grass (0).
                if (gridLayout[r][c] == 0) {
                    // Spawn chance (e.g., 0.15 = 15% chance for this grass tile).
                    if (rand.nextDouble() < 0.20) {
                        // Pick one random decoration from decorPool
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
