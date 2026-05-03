package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.List;
import application.AssetManager;
import application.GameView;
import logic.GameManager;
import logic.map.GameMap;
import logic.map.Waypoint;
import logic.enemy.BatEnemy;
import logic.enemy.SlimeEnemy;
import logic.enemy.BigSlimeEnemy;
import logic.enemy.GoblinEnemy;
import logic.enemy.DemonEnemy;
import logic.enemy.KingSlimeEnemy;
import logic.map.Decoration;
import logic.map.LevelLoader;
import logic.map.Theme;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        int[][] gridLayout = LevelLoader.loadMapGrid("/Maps/level1.txt");
        GameMap gameMap = new GameMap(gridLayout);

        // Create a pool of decorations to spawn randomly (add mushrooms or autumn trees
        // here).
        String[] decorPool;
        switch (gameMap.getTheme()) {
            case AUTUMN:
                decorPool = new String[] {
                        "Environment/Decoration/spr_rock_01.png",
                        "Environment/Decoration/spr_rock_02.png",
                        "Environment/Decoration/spr_rock_03.png",
                        "Environment/Decoration/spr_tree_01_autumn.png",
                        "Environment/Decoration/spr_tree_02_autumn.png",
                        "Environment/Decoration/spr_tree_01_autumn.png",
                        "Environment/Decoration/spr_tree_02_autumn.png",
                        "Environment/Decoration/spr_tree_01_autumn.png",
                        "Environment/Decoration/spr_tree_02_autumn.png",
                        "Environment/Decoration/spr_mushroom_01.png",
                        "Environment/Decoration/spr_mushroom_02.png"
                };
                break;
            case SPRING:
                decorPool = new String[] {
                        "Environment/Decoration/spr_rock_01.png",
                        "Environment/Decoration/spr_rock_02.png",
                        "Environment/Decoration/spr_rock_03.png",
                        "Environment/Decoration/spr_tree_01_cherry_blossom.png",
                        "Environment/Decoration/spr_tree_01_normal.png",
                        "Environment/Decoration/spr_tree_02_normal.png",
                        "Environment/Decoration/spr_tree_02_spruce.png",
                        "Environment/Decoration/spr_tree_01_cherry_blossom.png",
                        "Environment/Decoration/spr_tree_01_normal.png",
                        "Environment/Decoration/spr_tree_02_normal.png",
                        "Environment/Decoration/spr_tree_02_spruce.png",
                        "Environment/Decoration/spr_mushroom_01.png",
                        "Environment/Decoration/spr_mushroom_02.png"
                };
                break;
            case NORMAL:
            default:
                decorPool = new String[] {
                        "Environment/Decoration/spr_rock_01.png",
                        "Environment/Decoration/spr_rock_02.png",
                        "Environment/Decoration/spr_rock_03.png",
                        "Environment/Decoration/spr_tree_01_normal.png",
                        "Environment/Decoration/spr_tree_02_normal.png",
                        "Environment/Decoration/spr_tree_02_spruce.png",
                        "Environment/Decoration/spr_tree_01_normal.png",
                        "Environment/Decoration/spr_tree_02_normal.png",
                        "Environment/Decoration/spr_tree_02_spruce.png",
                        "Environment/Decoration/spr_mushroom_01.png",
                        "Environment/Decoration/spr_mushroom_02.png"
                };
                break;
        }

        final int tileSize = 50;
        java.util.Random rand = new java.util.Random();
        List<Decoration> decorations = gameMap.getDecorations();
        int attemptsPerTile = 2;

        // Iterate through the 2D grid
        for (int r = 0; r < gridLayout.length; r++) {
            for (int c = 0; c < gridLayout[0].length; c++) {
                // Important: place decorations only on grass (0).
                if (gridLayout[r][c] == 0) {
                    if (gameMap.isInCastleClearanceZone(r, c)) {
                        continue;
                    }
                    for (int attempt = 0; attempt < attemptsPerTile; attempt++) {
                        // Spawn chance (e.g., 0.15 = 15% chance per attempt).
                        if (rand.nextDouble() < 0.25) {
                            // Pick one random decoration from decorPool
                            String randomDecor = decorPool[rand.nextInt(decorPool.length)];
                            double scale = getDecorScale(randomDecor);

                            decorations.add(new Decoration(
                                    randomDecor,
                                    r,
                                    c,
                                    scale));

                            // 30% chance to add a mushroom if the placed decor is a rock
                            if (randomDecor.contains("rock") && rand.nextDouble() < 0.3) {
                                String mushroom = rand.nextBoolean() ? "Environment/Decoration/spr_mushroom_01.png"
                                        : "Environment/Decoration/spr_mushroom_02.png";
                                decorations.add(new Decoration(mushroom, r, c, getDecorScale(mushroom)));
                            }

                            break; // One decoration per grid tile max (plus an optional mushroom on rock)
                        }
                    }
                }
            }
        }

        gameMap.generatePath();
        GameManager gameManager = new GameManager(gameMap);

        GameView gameView = new GameView(gameManager);
        gameManager.spawnEnemy(new BatEnemy());
        gameManager.spawnEnemy(new SlimeEnemy());
        
        // Test the new delayed spawn system
        gameManager.spawnEnemyWave(10);

        Scene scene = new Scene(gameView, 800, 600);
        primaryStage.setTitle("Tower Defense");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameManager.update();
                gameView.drawMap();
            }
        };
        gameLoop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private double getDecorScale(String decorName) {
        if (decorName.contains("mushroom")) {
            return 3.0;
        }
        if (decorName.contains("tree")) {
            return 3.0;
        }
        if (decorName.contains("rock")) {
            return 3.0;
        }
        return 3.0;
    }

}
