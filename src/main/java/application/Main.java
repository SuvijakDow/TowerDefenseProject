package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;
import java.util.List;
import java.net.URL;
import logic.GameManager;
import logic.tower.Tower;
import logic.map.PathGenerator;
import logic.map.GameMap;
import logic.map.Theme;
import logic.map.Decoration;

public class Main extends Application {
    
    private static final double LOGIC_TICKS_PER_SECOND = 60.0;
    private static final double LOGIC_STEP_SECONDS = 1.0 / LOGIC_TICKS_PER_SECOND;
    private static final double LOGIC_SPEED_MULTIPLIER = 1.0;
    private static final int MAX_LOGIC_STEPS_PER_FRAME = 6;

    public static Stage primaryStage;
    private static GameView gameView;
    private static GameManager gameManager;
    private static AnimationTimer gameLoop;
    private static MainMenu mainMenu;
    private static GameMap gameMap;
    private static StackPane sidePanelContainer;
    private static VBox towerShopPanel;
    private static VBox towerStatusPanel;
    private static Font gameUiFont;

    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        // Preload all game audio once.
        SoundManager.initialize();
        
        // Create main menu first
        Main.mainMenu = new MainMenu(primaryStage);
        primaryStage.setTitle("Tower Defense");
        primaryStage.setScene(mainMenu.getScene());
        primaryStage.setResizable(false);
        primaryStage.show();
        Main.mainMenu.playMenuBgm();
    }
    
    public static void startGameFromMenu() {
        if (primaryStage != null) {
            if (Main.mainMenu != null) {
                Main.mainMenu.stopMenuBgm();
            }
            initializeGame();
            startGameLoop();
            SoundManager.playInGameBgm();
        }
    }
    
    private static void initializeGame() {
        int[][] gridLayout = PathGenerator.generateRandomPath(); // can use LevelLoader.loadMapGrid("/Paths/path1.txt") for custom levels.
        Main.gameMap = new GameMap(gridLayout);
        
        // Random theme selection
        Theme[] themes = Theme.values();
        Theme randomTheme = themes[(int)(Math.random() * themes.length)];
        gameMap.setTheme(randomTheme);
        System.out.println("Selected theme: " + randomTheme);

        // Create a pool of decorations to spawn randomly
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
        
        java.util.Random rand = new java.util.Random();
        List<Decoration> decorations = gameMap.getDecorations();
        int attemptsPerTile = 2;
        
        // Iterate through 2D grid
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
    }
    
    private static VBox createGameOverOverlay() {
        VBox gameOverOverlay = new VBox(10);
        gameOverOverlay.setAlignment(Pos.CENTER);
        gameOverOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        gameOverOverlay.setPrefSize(800, 600); // Match canvas size
        
        // GAME OVER text
        Font gameOverFont = Font.loadFont(Main.class.getResourceAsStream("/Fonts/CWEBS.TTF"), 120);
        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFont(gameOverFont);
        gameOverText.setFill(Color.RED);
        gameOverText.setEffect(new DropShadow(10, 3, 3, Color.BLACK));
        
        // Base Destroyed text
        Font statsFont = Font.loadFont(Main.class.getResourceAsStream("/Fonts/CWEBS.TTF"), 50);
        Text destroyedText = new Text("Base Destroyed!");
        destroyedText.setFont(statsFont);
        destroyedText.setFill(Color.WHITE);
        
        // RETURN TO MENU button
        Font buttonFont = Font.font("Verdana", 30); 
        Button returnButton = UIUtils.createStyledButton("RETURN TO MAIN MENU", buttonFont, 500, 60);
        returnButton.setOnAction(e -> returnToMenu());
        
        gameOverOverlay.getChildren().addAll(gameOverText, destroyedText, returnButton);
        return gameOverOverlay;
    }

    private static VBox createVictoryOverlay() {
        VBox victoryOverlay = new VBox(10);
        victoryOverlay.setAlignment(Pos.CENTER);
        victoryOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        victoryOverlay.setPrefSize(800, 600); // Match canvas size
        
        // VICTORY text
        Font victoryFont = Font.loadFont(Main.class.getResourceAsStream("/Fonts/CWEBS.TTF"), 120);
        Text victoryText = new Text("VICTORY!");
        victoryText.setFont(victoryFont);
        victoryText.setFill(javafx.scene.paint.Color.GOLD);
        victoryText.setEffect(new DropShadow(10, 3, 3, Color.BLACK));
        
        // Subtext
        Font statsFont = Font.loadFont(Main.class.getResourceAsStream("/Fonts/CWEBS.TTF"), 50);
        Text subText = new Text("You survived the siege!");
        subText.setFont(statsFont);
        subText.setFill(Color.WHITE);
        
        // RETURN TO MAIN MENU button
        Font buttonFont = Font.font("Verdana", 30); 
        Button returnButton = UIUtils.createStyledButton("RETURN TO MAIN MENU", buttonFont, 500, 60);
        returnButton.setOnAction(e -> returnToMenu());
        
        victoryOverlay.getChildren().addAll(victoryText, subText, returnButton);
        return victoryOverlay;
    }
    
    private static void showGameOverOverlay() {
        // Find the game over overlay in the scene and show it
        if (Main.primaryStage != null && Main.primaryStage.getScene() != null) {
            StackPane gameWithHUD = (StackPane) ((HBox) Main.primaryStage.getScene().getRoot()).getChildren().get(0);
            VBox gameOverOverlay = (VBox) gameWithHUD.getChildren().get(2); // Index 2: gameView(0), topLeftHUD(1), gameOverOverlay(2)
            gameOverOverlay.setVisible(true);
            gameOverOverlay.toFront();
        }
    }
    
    private static void showVictoryOverlay() {
        // Find the victory overlay in the scene and show it
        try {
            if (Main.primaryStage != null && Main.primaryStage.getScene() != null) {
                StackPane gameWithHUD = (StackPane) ((HBox) Main.primaryStage.getScene().getRoot()).getChildren().get(0);
                if (gameWithHUD.getChildren().size() > 3) {
                    VBox victoryOverlay = (VBox) gameWithHUD.getChildren().get(3); // Index 3: gameView(0), topLeftHUD(1), gameOverOverlay(2), victoryOverlay(3)
                    victoryOverlay.setVisible(true);
                    victoryOverlay.toFront();
                }
            }
        } catch (Exception e) {
            System.err.println("Error showing victory overlay: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void startGameLoop() {
        Main.gameView = new GameView(null);
        Main.gameManager = new GameManager(gameMap, gameView);
        gameView.setGameManager(gameManager);
        gameView.setPlacedTowerSelectionListener(tower -> {
            if (tower != null) {
                showTowerStatusPanel(tower);
            } else {
                showTowerShopPanel();
            }
        });

        // Load custom font with larger size
        Font customFont = Font.loadFont(Main.class.getResourceAsStream("/Fonts/CWEBS.TTF"), 36); // Massive font size
        Main.gameUiFont = customFont;

        // Create Top-Left HUD
        HBox topLeftHUD = createTopLeftHUD(gameManager, customFont);
        StackPane.setAlignment(topLeftHUD, Pos.TOP_LEFT);

        // Store HUD text references for game loop updates
        Text[] hudTexts = new Text[3]; // [hpText, moneyText, timerText]
        extractHUDTexts(topLeftHUD, hudTexts);

        // Create Right-Side Tower Shop in separate container
        Main.towerShopPanel = createTowerShop(gameManager, customFont);
        Main.towerShopPanel.setPrefWidth(javafx.scene.layout.Region.USE_COMPUTED_SIZE);
        Main.towerShopPanel.setMaxWidth(380);
        Main.towerStatusPanel = createTowerStatusPanel(null, customFont, Main::exitTowerStatusMode);
        Main.towerStatusPanel.setPrefWidth(javafx.scene.layout.Region.USE_COMPUTED_SIZE);
        Main.towerStatusPanel.setMaxWidth(380);
        Main.towerStatusPanel.setVisible(false);
        Main.towerStatusPanel.setManaged(false);
        Main.sidePanelContainer = new StackPane();
        Main.sidePanelContainer.setPrefWidth(380);
        Main.sidePanelContainer.setMaxWidth(380);
        Main.sidePanelContainer.getChildren().addAll(Main.towerShopPanel, Main.towerStatusPanel);

        // Store shop row references for selection updates
        HBox[] shopRows = new HBox[GameManager.TowerType.values().length];
        extractShopRows(Main.towerShopPanel, shopRows);
        updateShopSelectionUI(gameManager, shopRows);

        // Add HUD as overlay on game canvas only
        StackPane gameWithHUD = new StackPane();
        gameWithHUD.getChildren().addAll(Main.gameView, topLeftHUD);
        
        // Create Game Over overlay (hidden by default)
        VBox gameOverOverlay = createGameOverOverlay();
        gameOverOverlay.setVisible(false);
        
        // Create Victory overlay (hidden by default)
        VBox victoryOverlay = createVictoryOverlay();
        victoryOverlay.setVisible(false);
        
        // Add both overlays to the same StackPane
        gameWithHUD.getChildren().addAll(gameOverOverlay, victoryOverlay);
        
        // Create side-by-side HBox layout
        HBox rootPane = new HBox();
        rootPane.setStyle("-fx-background-color: #1a1a1a;"); // Dark background to eliminate white borders
        rootPane.getChildren().addAll(gameWithHUD, Main.sidePanelContainer); // Game left, side panel right

        // Scene dimensions to fit both canvas and shop perfectly
        Scene scene = new Scene(rootPane);
        scene.setFill(javafx.scene.paint.Color.BLACK); // Black fill just in case

        // Ensure game view gets mouse events by setting scene event handlers
        scene.setOnMouseClicked(e -> {
            if (e.isConsumed()) {
                return;
            }
            if (isEventInsideSidePanel(e.getTarget())) {
                return;
            }
            // Right-click cancels tower selection
            if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                gameManager.setSelectedTowerType(null);
                Main.gameView.clearPlacedTowerSelection();
                Main.gameView.updateHover(-1, -1);
                e.consume();
                return;
            }
            
            // Only process left-click for tower placement
            if (e.getButton() != javafx.scene.input.MouseButton.PRIMARY) {
                return;
            }
            
            // Check if click is on game area (left side)
            if (e.getSceneX() < 800) { // Game area width
                int col = (int) (e.getSceneX() / 50); // TILE_SIZE = 50
                int row = (int) (e.getSceneY() / 50);
                boolean towerClickHandled = Main.gameView.togglePlacedTowerRangeAt(row, col);
                if (!towerClickHandled) {
                    Main.gameView.clearPlacedTowerSelection();
                    gameManager.placeTower(row, col);
                }
                e.consume();
            }
        });

        scene.setOnMouseMoved(e -> {
            // Update hover only for game area
            if (e.getSceneX() < 800) {
                int col = (int) (e.getSceneX() / 50);
                int row = (int) (e.getSceneY() / 50);
                // Update hover by calling public method
                Main.gameView.updateHover(row, col);
            } else {
                // Clear hover when mouse leaves game area
                Main.gameView.updateHover(-1, -1);
            }
        });

        scene.setOnMouseExited(e -> {
            // Clear hover when mouse leaves entire scene
            Main.gameView.updateHover(-1, -1);
        });
        Main.primaryStage.setTitle("Tower Defense");
        Main.primaryStage.setScene(scene);
        Main.primaryStage.setResizable(false);
        Main.primaryStage.show();

        Main.gameLoop = new AnimationTimer() {
            private long lastTime = 0;
            private double logicAccumulator = 0.0;
            
            @Override
            public void handle(long now) {
                // Calculate deltaTime safely
                double deltaTime = LOGIC_STEP_SECONDS; // Default 60 FPS fallback
                if (lastTime > 0) {
                    deltaTime = (now - lastTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
                    deltaTime = Math.min(deltaTime, 0.1); // Cap at 100ms to prevent jumps
                }
                lastTime = now;
                logicAccumulator += deltaTime * LOGIC_SPEED_MULTIPLIER;
                logicAccumulator = Math.min(logicAccumulator, LOGIC_STEP_SECONDS * MAX_LOGIC_STEPS_PER_FRAME);
                
                // Check victory state
                if (Main.gameManager != null && Main.gameManager.isVictory()) {
                    this.stop();
                    // End gameplay audio and play victory SFX once.
                    SoundManager.stopInGameBgm();
                    SoundManager.playVictorySfx();
                    showVictoryOverlay();
                    return;
                }
                
                // Check game over state and stop if needed
                if (Main.gameManager != null && Main.gameManager.isGameOver()) {
                    this.stop();
                    // End gameplay audio and play defeat SFX once.
                    SoundManager.stopInGameBgm();
                    SoundManager.playDefeatSfx();
                    // Show game over overlay instead of drawing on canvas
                    showGameOverOverlay();
                    return;
                }

                // Safe update call
                try {
                    int logicSteps = 0;
                    while (logicAccumulator >= LOGIC_STEP_SECONDS && logicSteps < MAX_LOGIC_STEPS_PER_FRAME) {
                        Main.gameManager.update(LOGIC_STEP_SECONDS);
                        logicAccumulator -= LOGIC_STEP_SECONDS;
                        logicSteps++;
                    }
                    Main.gameView.updateCastleHitEffect(deltaTime);
                    Main.gameView.drawMap();

                    // Update HUD text every frame
                    updateHUDTexts(Main.gameManager, hudTexts);

                    // Update shop selection highlight
                    updateShopSelectionUI(Main.gameManager, shopRows);
                } catch (Exception e) {
                    System.err.println("Error in game loop: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        Main.gameLoop.start();
    }

    private static void returnToMenu() {
        // Stop game loop
        if (Main.gameLoop != null) {
            Main.gameLoop.stop();
        }
        SoundManager.stopInGameBgm();

        // Reset game state
        if (Main.gameManager != null) {
            Main.gameManager.resetGameState();
        }

        // Show main menu
        if (Main.primaryStage != null && Main.mainMenu != null) {
            Main.primaryStage.setScene(Main.mainMenu.getScene());
            Main.mainMenu.playMenuBgm();
        }
    }

    private static void extractHUDTexts(HBox hud, Text[] hudTexts) {
        // Extract HP, Money, and Timer text references
        hudTexts[0] = (Text) hud.getChildren().get(1); // HP text after heart icon
        hudTexts[1] = (Text) hud.getChildren().get(3); // Money text after coin icon
        hudTexts[2] = (Text) hud.getChildren().get(5); // Timer text after timer icon
    }
    
    private static void updateHUDTexts(GameManager gameManager, Text[] hudTexts) {
        // Update HP, Money, and Timer text manually every frame
        if (hudTexts[0] != null) {
            hudTexts[0].setText(String.valueOf(gameManager.getBaseHealth()));
        }
        if (hudTexts[1] != null) {
            hudTexts[1].setText(String.valueOf(gameManager.getPlayerMoney()));
        }
        if (hudTexts[2] != null) {
            hudTexts[2].setText(gameManager.getFormattedTime());
        }
    }
    
    private static void extractShopRows(VBox shop, HBox[] shopRows) {
        // Extract tower row references from GridPane
        javafx.scene.layout.GridPane gridPane = (javafx.scene.layout.GridPane) shop.getChildren().get(1);
        GameManager.TowerType[] towerTypes = GameManager.TowerType.values();
        for (int i = 0; i < towerTypes.length; i++) {
            int row = i / 2;
            int col = i % 2;
            shopRows[i] = (HBox) gridPane.getChildren().get(row * 2 + col);
        }
    }
    
    private static void updateShopSelectionUI(GameManager gameManager, HBox[] shopRows) {
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
    
    private static double getDecorScale(String decorName) {
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
    
    private static HBox createTopLeftHUD(GameManager gameManager, Font customFont) {
        HBox hud = new HBox(15);
        hud.setPadding(new Insets(10));
        // Remove background for cleaner look
        
        // Heart icon and HP text
        ImageView heartIcon = new ImageView(loadImageResource("/Icons/heart.png"));
        heartIcon.setFitWidth(36); // Match massive font size
        heartIcon.setFitHeight(36); // Match massive font size
        
        Text hpText = new Text();
        hpText.setFont(customFont); // Uses size 36 font
        hpText.setFill(javafx.scene.paint.Color.WHITE);
        // Set initial value without binding
        hpText.setText(String.valueOf(gameManager.getBaseHealth()));
        
        // Coin icon and Money text
        ImageView coinIcon = new ImageView(loadImageResource("/Icons/coin.png"));
        coinIcon.setFitWidth(36); // Match massive font size
        coinIcon.setFitHeight(36); // Match massive font size
        
        Text moneyText = new Text();
        moneyText.setFont(customFont); // Uses size 36 font
        moneyText.setFill(javafx.scene.paint.Color.WHITE);
        // Set initial value without binding
        moneyText.setText(String.valueOf(gameManager.getPlayerMoney()));
        
        // Timer icon and Timer text (fallback if icon missing)
        ImageView timerIcon = new ImageView(loadImageResource("/Icons/timer.png"));
        timerIcon.setFitWidth(36);
        timerIcon.setFitHeight(36);
        
        Text timerText = new Text();
        timerText.setFont(Font.loadFont(Main.class.getResourceAsStream("/Fonts/CWEBS.TTF"), 36)); // Size 30 as requested
        timerText.setFill(javafx.scene.paint.Color.AQUA);
        timerText.setText(gameManager.getFormattedTime());
        
        hud.getChildren().addAll(heartIcon, hpText, coinIcon, moneyText, timerIcon, timerText);
        return hud;
    }
    
    private static VBox createTowerShop(GameManager gameManager, Font customFont) {
        VBox shop = new VBox(10);
        shop.setPadding(new Insets(20)); // Increased padding for proper encapsulation
        shop.setStyle("-fx-background-color: rgba(40, 40, 40, 0.9);"); // Dark gray background
        
        // Shop title
        Text shopTitle = new Text("TOWER SHOP");
        shopTitle.setFont(Font.font(customFont.getFamily(), 55)); // Massive title font
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
        
        // Add flexible spacer to push button to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        shop.getChildren().add(spacer);
        
        // Create MAIN MENU button
        Button mainMenuButton = new Button("RETURN TO MAIN MENU");
        UIUtils.attachClickSfx(mainMenuButton);
        mainMenuButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75); -fx-text-fill: white; -fx-font-family: 'CWEBS'; -fx-font-size: 20px; -fx-background-radius: 8px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-cursor: hand;");
        mainMenuButton.setMaxWidth(Double.MAX_VALUE);
        
        // Add hover effects
        mainMenuButton.setOnMouseEntered(e -> {
            mainMenuButton.setStyle("-fx-background-color: rgba(50, 50, 50, 0.9); -fx-text-fill: white; -fx-font-family: 'CWEBS'; -fx-font-size: 20px; -fx-background-radius: 8px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-cursor: hand;");
        });
        
        mainMenuButton.setOnMouseExited(e -> {
            mainMenuButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75); -fx-text-fill: white; -fx-font-family: 'CWEBS'; -fx-font-size: 20px; -fx-background-radius: 8px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-cursor: hand;");
        });
        
        // Set button action to return to main menu
        mainMenuButton.setOnAction(e -> {
            stopGameLoop();
            resetGameState();
            returnToMainMenu();
        });
        
        shop.getChildren().add(mainMenuButton);
        return shop;
    }

    private static VBox createTowerStatusPanel(Tower tower, Font customFont, Runnable onBack) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: rgba(40, 40, 40, 0.9);");

        // Title
        Text title = new Text("TOWER STATUS");
        title.setFont(Font.font(customFont.getFamily(), 55));
        title.setFill(Color.GOLD);

        // Pixel-art preview card
        ImageView towerSprite = createCrispStatusTowerSprite(tower);
        StackPane previewCard = new StackPane(towerSprite);
        previewCard.setPadding(new Insets(12));
        previewCard.setMinHeight(180);
        previewCard.setPrefHeight(180);
        previewCard.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-border-color: rgba(255,215,0,0.45); -fx-border-width: 2px;");

        // Stats card
        VBox statsCard = new VBox(8);
        statsCard.setPadding(new Insets(10));
        statsCard.setStyle("-fx-background-color: rgba(255,255,255,0.1);");

        String towerName = (tower != null) ? tower.getClass().getSimpleName() : "-";
        int level = (tower != null) ? tower.getLevel() : 0;
        int damage = (tower != null) ? tower.getDamage() : 0;
        double range = (tower != null) ? tower.getRange() : 0.0;
        int cooldown = (tower != null) ? tower.getFireCooldown() : 0;

        Text nameText = new Text("Name: " + towerName);
        nameText.setFont(Font.font(customFont.getFamily(), 30));
        nameText.setFill(Color.WHITE);

        Text levelText = new Text("Level: " + level);
        levelText.setFont(Font.font(customFont.getFamily(), 26));
        levelText.setFill(Color.WHITE);

        Text damageText = new Text("Damage: " + damage);
        damageText.setFont(Font.font(customFont.getFamily(), 26));
        damageText.setFill(Color.WHITE);

        Text rangeText = new Text("Range: " + String.format("%.1f", range));
        rangeText.setFont(Font.font(customFont.getFamily(), 26));
        rangeText.setFill(Color.WHITE);

        Text cooldownText = new Text("Fire Cooldown: " + cooldown);
        cooldownText.setFont(Font.font(customFont.getFamily(), 26));
        cooldownText.setFill(Color.WHITE);

        statsCard.getChildren().addAll(nameText, levelText, damageText, rangeText, cooldownText);

        // Push button down
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Upgrade button
        Button upgradeButton = new Button();
        UIUtils.attachClickSfx(upgradeButton);
        boolean canUpgrade = tower != null && tower.canUpgrade();
        if (canUpgrade) {
            upgradeButton.setText("UPGRADE ($" + tower.getUpgradeCost() + ")");
        } else {
            upgradeButton.setText("MAX LEVEL");
        }
        upgradeButton.setDisable(!canUpgrade);
        upgradeButton.setMaxWidth(Double.MAX_VALUE);
        upgradeButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75); -fx-text-fill: white; -fx-font-family: 'CWEBS'; -fx-font-size: 20px; -fx-background-radius: 8px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-cursor: hand;");
        upgradeButton.setOnMouseEntered(e -> {
            if (!upgradeButton.isDisable()) {
                upgradeButton.setStyle("-fx-background-color: rgba(50, 50, 50, 0.9); -fx-text-fill: white; -fx-font-family: 'CWEBS'; -fx-font-size: 20px; -fx-background-radius: 8px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-cursor: hand;");
            }
        });
        upgradeButton.setOnMouseExited(e -> {
            if (!upgradeButton.isDisable()) {
                upgradeButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75); -fx-text-fill: white; -fx-font-family: 'CWEBS'; -fx-font-size: 20px; -fx-background-radius: 8px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-cursor: hand;");
            }
        });
        upgradeButton.setOnAction(e -> {
            if (tower == null || Main.gameManager == null) {
                return;
            }
            boolean upgraded = Main.gameManager.tryUpgradeTower(tower);
            if (upgraded) {
                showTowerStatusPanel(tower);
                if (Main.gameView != null) {
                    Main.gameView.drawMap();
                }
            }
        });

        // Close button
        Button closeButton = new Button("BACK");
        UIUtils.attachClickSfx(closeButton);
        closeButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75); -fx-text-fill: white; -fx-font-family: 'CWEBS'; -fx-font-size: 20px; -fx-background-radius: 8px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-cursor: hand;");
        closeButton.setMaxWidth(Double.MAX_VALUE);
        closeButton.setOnMouseEntered(e -> closeButton.setStyle("-fx-background-color: rgba(50, 50, 50, 0.9); -fx-text-fill: white; -fx-font-family: 'CWEBS'; -fx-font-size: 20px; -fx-background-radius: 8px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-cursor: hand;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75); -fx-text-fill: white; -fx-font-family: 'CWEBS'; -fx-font-size: 20px; -fx-background-radius: 8px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-cursor: hand;"));
        closeButton.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            } else {
                panel.setVisible(false);
            }
        });

        HBox actionRow = new HBox(10, upgradeButton, closeButton);
        actionRow.setAlignment(Pos.CENTER);
        actionRow.setFillHeight(true);
        HBox.setHgrow(upgradeButton, Priority.ALWAYS);
        HBox.setHgrow(closeButton, Priority.ALWAYS);

        panel.getChildren().addAll(title, previewCard, statsCard, spacer, actionRow);
        return panel;
    }

    private static ImageView createCrispStatusTowerSprite(Tower tower) {
        ImageView spriteView = new ImageView();
        spriteView.setPreserveRatio(true);
        spriteView.setSmooth(false); // Keep pixel edges sharp

        String spritePath = (tower != null && tower.getSpriteName() != null) ? tower.getSpriteName() : "";
        if (spritePath.isBlank()) {
            return spriteView;
        }
        if (!spritePath.startsWith("/")) {
            spritePath = "/" + spritePath;
        }

        Image sprite = loadImageResource(spritePath);
        spriteView.setImage(sprite);

        double iw = sprite.getWidth();
        double ih = sprite.getHeight();
        if (iw <= 0 || ih <= 0) {
            return spriteView;
        }

        double maxW = 140.0;
        double maxH = 150.0;
        double fitScale = Math.min(maxW / iw, maxH / ih);

        double drawW;
        double drawH;
        if (fitScale >= 1.0) {
            int integerScale = Math.max(1, (int) Math.floor(fitScale));
            drawW = iw * integerScale;
            drawH = ih * integerScale;
        } else {
            drawW = Math.max(1.0, Math.floor(iw * fitScale));
            drawH = Math.max(1.0, Math.floor(ih * fitScale));
        }

        spriteView.setFitWidth(drawW);
        spriteView.setFitHeight(drawH);
        return spriteView;
    }

    private static void showTowerStatusPanel(Tower tower) {
        if (tower == null || sidePanelContainer == null || towerShopPanel == null) {
            return;
        }
        Font panelFont = gameUiFont != null ? gameUiFont : Font.font("Verdana", 24);
        VBox newStatusPanel = createTowerStatusPanel(tower, panelFont, Main::exitTowerStatusMode);
        newStatusPanel.setPrefWidth(javafx.scene.layout.Region.USE_COMPUTED_SIZE);
        newStatusPanel.setMaxWidth(380);
        if (towerStatusPanel != null) {
            sidePanelContainer.getChildren().remove(towerStatusPanel);
        }
        towerStatusPanel = newStatusPanel;
        sidePanelContainer.getChildren().add(towerStatusPanel);
        towerShopPanel.setVisible(false);
        towerShopPanel.setManaged(false);
        towerStatusPanel.setVisible(true);
        towerStatusPanel.setManaged(true);
        towerStatusPanel.toFront();
    }

    private static void showTowerShopPanel() {
        if (towerShopPanel == null) {
            return;
        }
        towerShopPanel.setVisible(true);
        towerShopPanel.setManaged(true);
        if (towerStatusPanel != null) {
            towerStatusPanel.setVisible(false);
            towerStatusPanel.setManaged(false);
        }
    }

    private static void exitTowerStatusMode() {
        if (Main.gameView != null) {
            Main.gameView.clearPlacedTowerSelection();
        }
        showTowerShopPanel();
    }
    
    private static HBox createTowerShopRow(GameManager gameManager, GameManager.TowerType towerType, Font customFont) {
        HBox row = new HBox(8);
        row.setPadding(new Insets(6));
        row.setStyle("-fx-background-color: rgba(255,255,255,0.1);"); // Remove border radius
        
        // Tower sprite
        String spritePath = getTowerSpritePath(towerType);
        ImageView towerSprite = new ImageView(loadImageResource(spritePath));
        towerSprite.setFitWidth(40);
        towerSprite.setPreserveRatio(true);
        towerSprite.setSmooth(true);
        
        // Tower name and cost
        VBox textContainer = new VBox(2);
        Text nameText = new Text(getTowerDisplayName(towerType));
        nameText.setFont(Font.font(customFont.getFamily(), 26)); // Massive name font
        nameText.setFill(javafx.scene.paint.Color.WHITE);
        nameText.setWrappingWidth(100); // Smaller wrapping width to constrain text
        
        Text costText = new Text("$" + getTowerCost(gameManager, towerType));
        costText.setFont(Font.font(customFont.getFamily(), 30)); // Massive cost font
        costText.setFill(javafx.scene.paint.Color.GOLD); // Gold color to pop
        
        textContainer.getChildren().addAll(nameText, costText);
        
        row.getChildren().addAll(towerSprite, textContainer);
        
        // Click handler with input separation
        row.setOnMouseClicked(e -> {
            // Right-click cancels tower selection
            if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                gameManager.setSelectedTowerType(null);
                Main.gameView.updateHover(-1, -1);
                e.consume();
                return;
            }
            
            // Left-click selects tower
            if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                gameManager.setSelectedTowerType(towerType);
                e.consume(); // Prevent tower placement on map beneath
            }
        });
        
        return row;
    }
    
    private static String getTowerSpritePath(GameManager.TowerType towerType) {
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
    
    private static String getTowerDisplayName(GameManager.TowerType towerType) {
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
    
    private static int getTowerCost(GameManager gameManager, GameManager.TowerType towerType) {
        // Get actual cost from tower class instead of hardcoded values
        try {
            Tower tempTower = gameManager.createTowerFromType(towerType);
            return tempTower != null ? tempTower.getCost() : 100;
        } catch (Exception e) {
            // Fallback to hardcoded values if there's an error
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
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    private static void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
    }

    private static Image loadImageResource(String resourcePath) {
        URL imageUrl = Main.class.getResource(resourcePath);
        if (imageUrl == null) {
            throw new IllegalStateException("Missing image resource: " + resourcePath);
        }
        return new Image(imageUrl.toExternalForm());
    }

    private static void resetGameState() {
        if (gameManager != null) {
            gameManager.resetGameState();
        }
    }

    private static void returnToMainMenu() {
        if (primaryStage != null && mainMenu != null) {
            stopGameLoop();
            SoundManager.stopInGameBgm();
            resetGameState();
            primaryStage.setScene(mainMenu.getScene());
            mainMenu.playMenuBgm();
        }
    }

    private static boolean isEventInsideSidePanel(Object target) {
        if (sidePanelContainer == null || !(target instanceof Node)) {
            return false;
        }
        Node node = (Node) target;
        while (node != null) {
            if (node == sidePanelContainer) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }
}
