package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
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
        gameManager.spawnEnemyWave(40);

        // Load custom font with larger size
        Font customFont = Font.loadFont(getClass().getResourceAsStream("/Fonts/CWEBS.TTF"), 36); // Massive font size
        
        // Create Top-Left HUD
        HBox topLeftHUD = createTopLeftHUD(gameManager, customFont);
        StackPane.setAlignment(topLeftHUD, Pos.TOP_LEFT);
        
        // Store HUD text references for game loop updates
        Text[] hudTexts = new Text[2]; // [hpText, moneyText]
        extractHUDTexts(topLeftHUD, hudTexts);
        
        // Create Right-Side Tower Shop in separate container
        VBox towerShop = createTowerShop(gameManager, customFont);
        towerShop.setPrefWidth(javafx.scene.layout.Region.USE_COMPUTED_SIZE); // Let it size naturally
        towerShop.setMaxWidth(380); // Allow wider to prevent clipping
        
        // Store shop row references for selection updates
        HBox[] shopRows = new HBox[GameManager.TowerType.values().length];
        extractShopRows(towerShop, shopRows);
        updateShopSelectionUI(gameManager, shopRows);
        
        // Add HUD as overlay on game canvas only
        StackPane gameWithHUD = new StackPane();
        gameWithHUD.getChildren().addAll(gameView, topLeftHUD);
        
        // Create side-by-side HBox layout
        HBox rootPane = new HBox();
        rootPane.setStyle("-fx-background-color: #1a1a1a;"); // Dark background to eliminate white borders
        rootPane.getChildren().addAll(gameWithHUD, towerShop); // Game left, Shop right

        // Scene dimensions to fit both canvas and shop perfectly
        Scene scene = new Scene(rootPane);
        scene.setFill(javafx.scene.paint.Color.BLACK); // Black fill just in case
        
        // Ensure game view gets mouse events by setting scene event handlers
        scene.setOnMouseClicked(e -> {
            // Check if click is on game area (left side)
            if (e.getX() < 800) { // Game area width
                int col = (int) (e.getX() / 50); // TILE_SIZE = 50
                int row = (int) (e.getY() / 50);
                gameManager.placeTower(row, col);
            }
        });
        
        scene.setOnMouseMoved(e -> {
            // Update hover only for game area
            if (e.getX() < 800) {
                int col = (int) (e.getX() / 50);
                int row = (int) (e.getY() / 50);
                // Update hover by calling public method
                gameView.updateHover(row, col);
            } else {
                // Clear hover when mouse leaves game area
                gameView.updateHover(-1, -1);
            }
        });
        
        scene.setOnMouseExited(e -> {
            // Clear hover when mouse leaves entire scene
            gameView.updateHover(-1, -1);
        });
        primaryStage.setTitle("Tower Defense");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Check game over state and stop if needed
                if (gameManager.isGameOver()) {
                    this.stop();
                    // Draw game over screen directly here
                    GraphicsContext gc = gameView.getGraphicsContext2D();
                    if (gc != null) {
                        // Draw semi-transparent overlay
                        gc.setFill(Color.rgb(0, 0, 0, 0.7));
                        gc.fillRect(0, 0, 800, 600);
                        
                        // Load custom font
                        Font gameOverFont = Font.loadFont(getClass().getResourceAsStream("/Fonts/CWEBS.TTF"), 80);
                        if (gameOverFont == null) {
                            gameOverFont = new Font("Arial", 80);
                        }
                        
                        // Draw GAME OVER text
                        gc.setFill(Color.RED);
                        gc.setFont(gameOverFont);
                        gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
                        gc.fillText("GAME OVER", 400, 300);
                        
                        // Draw "Base Destroyed!" text
                        Font statsFont = Font.loadFont(getClass().getResourceAsStream("/Fonts/CWEBS.TTF"), 36);
                        if (statsFont == null) {
                            statsFont = new Font("Arial", 36);
                        }
                        gc.setFill(Color.WHITE);
                        gc.setFont(statsFont);
                        gc.fillText("Base Destroyed!", 400, 360);
                    }
                    return;
                }
                
                gameManager.update();
                gameView.drawMap();
                
                // Update HUD text every frame
                updateHUDTexts(gameManager, hudTexts);
                
                // Update shop selection highlight
                updateShopSelectionUI(gameManager, shopRows);
            }
        };
        gameLoop.start();
    }

    private void extractHUDTexts(HBox hud, Text[] hudTexts) {
        // Extract HP and Money text references
        hudTexts[0] = (Text) hud.getChildren().get(1); // HP text after heart icon
        hudTexts[1] = (Text) hud.getChildren().get(3); // Money text after coin icon
    }
    
    private void updateHUDTexts(GameManager gameManager, Text[] hudTexts) {
        // Update HP and Money text manually every frame
        if (hudTexts[0] != null) {
            hudTexts[0].setText(String.valueOf(gameManager.getBaseHealth()));
        }
        if (hudTexts[1] != null) {
            hudTexts[1].setText(String.valueOf(gameManager.getPlayerMoney()));
        }
    }
    
    private void extractShopRows(VBox shop, HBox[] shopRows) {
        // Extract tower row references from GridPane
        javafx.scene.layout.GridPane gridPane = (javafx.scene.layout.GridPane) shop.getChildren().get(1);
        GameManager.TowerType[] towerTypes = GameManager.TowerType.values();
        for (int i = 0; i < towerTypes.length; i++) {
            int row = i / 2;
            int col = i % 2;
            shopRows[i] = (HBox) gridPane.getChildren().get(row * 2 + col);
        }
    }
    
    private void updateShopSelectionUI(GameManager gameManager, HBox[] shopRows) {
        GameManager.TowerType selectedType = gameManager.getSelectedTowerType();
        GameManager.TowerType[] towerTypes = GameManager.TowerType.values();
        
        for (int i = 0; i < towerTypes.length; i++) {
            HBox row = shopRows[i];
            if (row != null) {
                if (towerTypes[i] == selectedType) {
                    row.setStyle("-fx-background-color: rgba(255,215,0,0.3); -fx-border-color: gold; -fx-border-width: 3px;");
                } else {
                    row.setStyle("-fx-background-color: rgba(255,255,255,0.1);");
                }
            }
        }
    }
    
    private HBox createTopLeftHUD(GameManager gameManager, Font customFont) {
        HBox hud = new HBox(15);
        hud.setPadding(new Insets(10));
        // Remove background for cleaner look
        
        // Heart icon and HP text
        ImageView heartIcon = new ImageView(new Image("/Icons/heart.png"));
        heartIcon.setFitWidth(36); // Match massive font size
        heartIcon.setFitHeight(36); // Match massive font size
        
        Text hpText = new Text();
        hpText.setFont(customFont); // Uses size 36 font
        hpText.setFill(javafx.scene.paint.Color.WHITE);
        // Set initial value without binding
        hpText.setText(String.valueOf(gameManager.getBaseHealth()));
        
        // Coin icon and Money text
        ImageView coinIcon = new ImageView(new Image("/Icons/coin.png"));
        coinIcon.setFitWidth(36); // Match massive font size
        coinIcon.setFitHeight(36); // Match massive font size
        
        Text moneyText = new Text();
        moneyText.setFont(customFont); // Uses size 36 font
        moneyText.setFill(javafx.scene.paint.Color.WHITE);
        // Set initial value without binding
        moneyText.setText(String.valueOf(gameManager.getPlayerMoney()));
        
        hud.getChildren().addAll(heartIcon, hpText, coinIcon, moneyText);
        return hud;
    }
    
    private VBox createTowerShop(GameManager gameManager, Font customFont) {
        VBox shop = new VBox(10);
        shop.setPadding(new Insets(20)); // Increased padding for proper encapsulation
        shop.setStyle("-fx-background-color: rgba(40, 40, 40, 0.9);"); // Dark gray background
        
        // Shop title
        Text shopTitle = new Text("TOWER SHOP");
        shopTitle.setFont(Font.font(customFont.getFamily(), 46)); // Massive title font
        shopTitle.setFill(javafx.scene.paint.Color.GOLD);
        shop.getChildren().add(shopTitle);
        
        // Create uniform 2-column grid for towers
        javafx.scene.layout.GridPane towerGrid = new javafx.scene.layout.GridPane();
        towerGrid.setHgap(10);
        towerGrid.setVgap(10);
        
        // CRITICAL: Force exactly 2 columns with 50% width each for perfect symmetry
        javafx.scene.layout.ColumnConstraints cc1 = new javafx.scene.layout.ColumnConstraints();
        cc1.setPercentWidth(50);
        javafx.scene.layout.ColumnConstraints cc2 = new javafx.scene.layout.ColumnConstraints();
        cc2.setPercentWidth(50);
        towerGrid.getColumnConstraints().addAll(cc1, cc2);
        
        GameManager.TowerType[] towerTypes = GameManager.TowerType.values();
        for (int i = 0; i < towerTypes.length; i++) {
            HBox towerRow = createTowerShopRow(gameManager, towerTypes[i], customFont);
            int row = i / 2;
            int col = i % 2;
            
            // CRITICAL: Force each card to stretch and fill its grid cell completely
            towerRow.setMaxWidth(Double.MAX_VALUE);
            towerRow.setMaxHeight(Double.MAX_VALUE);
            towerRow.setPrefHeight(60); // Consistent height for uniform rows
            javafx.scene.layout.GridPane.setFillWidth(towerRow, true);
            javafx.scene.layout.GridPane.setFillHeight(towerRow, true);
            
            towerGrid.add(towerRow, col, row);
        }
        
        shop.getChildren().add(towerGrid);
        return shop;
    }
    
    private HBox createTowerShopRow(GameManager gameManager, GameManager.TowerType towerType, Font customFont) {
        HBox row = new HBox(8);
        row.setPadding(new Insets(6));
        row.setStyle("-fx-background-color: rgba(255,255,255,0.1);"); // Remove border radius
        
        // Tower sprite
        String spritePath = getTowerSpritePath(towerType);
        ImageView towerSprite = new ImageView(new Image(spritePath));
        towerSprite.setFitWidth(40);
        towerSprite.setPreserveRatio(true);
        towerSprite.setSmooth(true);
        
        // Tower name and cost
        VBox textContainer = new VBox(2);
        Text nameText = new Text(getTowerDisplayName(towerType));
        nameText.setFont(Font.font(customFont.getFamily(), 26)); // Massive name font
        nameText.setFill(javafx.scene.paint.Color.WHITE);
        nameText.setWrappingWidth(100); // Smaller wrapping width to constrain text
        
        Text costText = new Text("$" + getTowerCost(towerType));
        costText.setFont(Font.font(customFont.getFamily(), 22)); // Massive cost font
        costText.setFill(javafx.scene.paint.Color.GOLD); // Gold color to pop
        
        textContainer.getChildren().addAll(nameText, costText);
        
        row.getChildren().addAll(towerSprite, textContainer);
        
        // Click handler with input separation
        row.setOnMouseClicked(e -> {
            gameManager.setSelectedTowerType(towerType);
            e.consume(); // Prevent tower placement on map beneath
        });
        
        return row;
    }
    
    private String getTowerSpritePath(GameManager.TowerType towerType) {
        switch (towerType) {
            case ARCHER: return "/Towers/Combat Towers/spr_tower_archer.png";
            case CANNON: return "/Towers/Combat Towers/spr_tower_cannon.png";
            case CROSSBOW: return "/Towers/Combat Towers/spr_tower_crossbow.png";
            case ICE_WIZARD: return "/Towers/Combat Towers/spr_tower_ice_wizard.png";
            case LIGHTNING_WIZARD: return "/Towers/Combat Towers/spr_tower_lightning_tower.png";
            case POISON_WIZARD: return "/Towers/Combat Towers/spr_tower_poison_wizard.png";
            default: return "/Towers/Combat Towers/spr_tower_archer.png";
        }
    }
    
    private String getTowerDisplayName(GameManager.TowerType towerType) {
        switch (towerType) {
            case ARCHER: return "Archer";
            case CANNON: return "Cannon";
            case CROSSBOW: return "Crossbow";
            case ICE_WIZARD: return "Ice Wizard";
            case LIGHTNING_WIZARD: return "Lightning Wizard";
            case POISON_WIZARD: return "Poison Wizard";
            default: return "Tower";
        }
    }
    
    private int getTowerCost(GameManager.TowerType towerType) {
        switch (towerType) {
            case ARCHER: return 100;
            case CANNON: return 120;
            case CROSSBOW: return 130;
            case ICE_WIZARD: return 150;
            case LIGHTNING_WIZARD: return 150;
            case POISON_WIZARD: return 150;
            default: return 100;
        }
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

    public static void main(String[] args) {
        launch(args);
    }

}
