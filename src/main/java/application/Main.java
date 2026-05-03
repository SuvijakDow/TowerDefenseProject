package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.List;
import logic.GameManager;
import logic.enemy.BatEnemy;
import logic.map.Decoration;
import logic.map.GameMap;
import logic.map.LevelLoader;
import logic.map.Theme;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        int[][] gridLayout = LevelLoader.loadMapGrid("/Maps/level1.txt");
        GameMap gameMap = new GameMap(gridLayout);

        // Create a pool of decorations to spawn randomly (add mushrooms or autumn trees here).
        String[] decorPool;
        switch (gameMap.getTheme()) {
            case AUTUMN:
                decorPool = new String[] {
                        "Environment/Decoration/spr_rock_01.png",
                        "Environment/Decoration/spr_rock_02.png",
                        "Environment/Decoration/spr_rock_03.png",
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

        int[] castleBase = findCastleBaseCell(gridLayout);

        // Iterate through the 2D grid
        for (int r = 0; r < gridLayout.length; r++) {
            for (int c = 0; c < gridLayout[0].length; c++) {
                // Important: place decorations only on grass (0).
                if (gridLayout[r][c] == 0) {
                    if (castleBase != null && isInCastleClearanceZone(r, c, castleBase[0], castleBase[1])) {
                        continue;
                    }
                    for (int attempt = 0; attempt < attemptsPerTile; attempt++) {
                        // Spawn chance (e.g., 0.15 = 15% chance per attempt).
                        if (rand.nextDouble() < 0.30) {
                            // Pick one random decoration from decorPool
                            String randomDecor = decorPool[rand.nextInt(decorPool.length)];
                            double baseX = c * tileSize;
                            double baseY = r * tileSize;
                            double offsetX = (rand.nextDouble() * 30.0) - 15.0;
                            double offsetY = (rand.nextDouble() * 30.0) - 15.0;
                            double scale = getDecorScale(randomDecor);

                            decorations.add(new Decoration(
                                    randomDecor,
                                    baseX + offsetX,
                                    baseY + offsetY,
                                    scale
                            ));
                        }
                    }
                }
            }
        }

        gameMap.generatePath();
        GameManager gameManager = new GameManager(gameMap);

        GameView gameView = new GameView(gameManager);
        gameManager.spawnEnemy(new BatEnemy());

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
            return 2.5;
        }
        if (decorName.contains("tree")) {
            return 3.0;
        }
        if (decorName.contains("rock")) {
            return 3.0;
        }
        return 2.0;
    }

    /** First grid cell marked {@code 2} (castle base), or {@code null} if none. */
    private static int[] findCastleBaseCell(int[][] gridLayout) {
        if (gridLayout == null || gridLayout.length == 0) {
            return null;
        }
        for (int r = 0; r < gridLayout.length; r++) {
            for (int c = 0; c < gridLayout[r].length; c++) {
                if (gridLayout[r][c] == 2) {
                    return new int[] { r, c };
                }
            }
        }
        return null;
    }

    /**
     * Castle footprint clearance: rows {@code castleR-1..castleR}, cols {@code castleC-1..castleC+1}
     * (3×2 tiles aligned with the rendered castle).
     */
    private static boolean isInCastleClearanceZone(int r, int c, int castleR, int castleC) {
        return r >= castleR - 1 && r <= castleR && c >= castleC - 1 && c <= castleC + 1;
    }
}
