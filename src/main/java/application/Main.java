package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logic.GameManager;
import logic.map.Decoration;
import logic.map.GameMap;
import logic.map.PathGenerator;
import logic.map.Theme;
import logic.tower.Tower;

import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Main extends Application {
    private static final String GAME_TITLE = "Tower Defense";
    private static final String GAME_FONT_RESOURCE = "/Fonts/CWEBS.TTF";

    private static final int GAME_CANVAS_WIDTH = 800;
    private static final int GAME_CANVAS_HEIGHT = 600;
    private static final int TILE_SIZE = GameMap.PATH_TILE_PIXEL_SIZE;
    private static final int SIDE_PANEL_WIDTH = 380;
    private static final int SHOP_COLUMNS = 2;

    private static final double LOGIC_TICKS_PER_SECOND = 60.0;
    private static final double LOGIC_STEP_SECONDS = 1.0 / LOGIC_TICKS_PER_SECOND;
    private static final double LOGIC_SPEED_MULTIPLIER = 1.0;
    private static final int MAX_LOGIC_STEPS_PER_FRAME = 6;
    private static final double MAX_DELTA_SECONDS = 0.1;

    private static final double HUD_FONT_SIZE = 36;
    private static final double OVERLAY_TITLE_FONT_SIZE = 120;
    private static final double OVERLAY_SUBTITLE_FONT_SIZE = 50;
    private static final double SHOP_TITLE_FONT_SIZE = 55;
    private static final double SHOP_NAME_FONT_SIZE = 26;
    private static final double SHOP_COST_FONT_SIZE = 30;
    private static final double STATUS_TITLE_FONT_SIZE = 55;
    private static final double STATUS_NAME_FONT_SIZE = 30;
    private static final double STATUS_INFO_FONT_SIZE = 26;
    private static final double BUTTON_FONT_SIZE = 20;

    private static final String MAIN_MENU_BUTTON_TEXT = "RETURN TO MAIN MENU";
    private static final String PANEL_STYLE = "-fx-background-color: rgba(40, 40, 40, 0.9);";
    private static final String ROOT_STYLE = "-fx-background-color: #1a1a1a;";
    private static final String OVERLAY_STYLE = "-fx-background-color: rgba(0, 0, 0, 0.8);";
    private static final String SHOP_ROW_DEFAULT_STYLE = "-fx-background-color: rgba(255,255,255,0.1);";
    private static final String SHOP_ROW_SELECTED_STYLE = "-fx-background-color: rgba(255,215,0,0.3); -fx-border-color: gold; -fx-border-width: 3px;";
    private static final String PANEL_BUTTON_BASE_STYLE = "-fx-background-color: rgba(0, 0, 0, 0.75); -fx-text-fill: white; -fx-font-family: 'CWEBS'; -fx-font-size: 20px; -fx-background-radius: 8px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-cursor: hand;";
    private static final String PANEL_BUTTON_HOVER_STYLE = "-fx-background-color: rgba(50, 50, 50, 0.9); -fx-text-fill: white; -fx-font-family: 'CWEBS'; -fx-font-size: 20px; -fx-background-radius: 8px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-cursor: hand;";

    private static final String MUSHROOM_1 = "Environment/Decoration/spr_mushroom_01.png";
    private static final String MUSHROOM_2 = "Environment/Decoration/spr_mushroom_02.png";
    private static final double DECOR_SCALE = 3.0;
    private static final int DECOR_ATTEMPTS_PER_TILE = 2;
    private static final double DECOR_SPAWN_CHANCE = 0.25;
    private static final double ROCK_MUSHROOM_CHANCE = 0.3;

    private static final String[] AUTUMN_DECOR_POOL = {
            "Environment/Decoration/spr_rock_01.png",
            "Environment/Decoration/spr_rock_02.png",
            "Environment/Decoration/spr_rock_03.png",
            "Environment/Decoration/spr_tree_01_autumn.png",
            "Environment/Decoration/spr_tree_02_autumn.png",
            "Environment/Decoration/spr_tree_01_autumn.png",
            "Environment/Decoration/spr_tree_02_autumn.png",
            "Environment/Decoration/spr_tree_01_autumn.png",
            "Environment/Decoration/spr_tree_02_autumn.png",
            MUSHROOM_1,
            MUSHROOM_2
    };

    private static final String[] SPRING_DECOR_POOL = {
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
            MUSHROOM_1,
            MUSHROOM_2
    };

    private static final String[] NORMAL_DECOR_POOL = {
            "Environment/Decoration/spr_rock_01.png",
            "Environment/Decoration/spr_rock_02.png",
            "Environment/Decoration/spr_rock_03.png",
            "Environment/Decoration/spr_tree_01_normal.png",
            "Environment/Decoration/spr_tree_02_normal.png",
            "Environment/Decoration/spr_tree_02_spruce.png",
            "Environment/Decoration/spr_tree_01_normal.png",
            "Environment/Decoration/spr_tree_02_normal.png",
            "Environment/Decoration/spr_tree_02_spruce.png",
            MUSHROOM_1,
            MUSHROOM_2
    };

    private record TowerInfo(String spritePath, String displayName, int fallbackCost) {
    }

    private static final Map<GameManager.TowerType, TowerInfo> TOWER_INFO = createTowerInfo();

    public static Stage primaryStage;
    private static GameView gameView;
    private static GameManager gameManager;
    private static AnimationTimer gameLoop;
    private static MainMenu mainMenu;
    private static GameMap gameMap;
    private static StackPane sidePanelContainer;
    private static VBox towerShopPanel;
    private static VBox towerStatusPanel;
    private static VBox gameOverOverlay;
    private static VBox victoryOverlay;
    private static Font gameUiFont;

    private static final class HudTexts {
        private Text hpText;
        private Text moneyText;
        private Text timerText;
    }

    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        SoundManager.initialize();

        Main.mainMenu = new MainMenu();
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("/Icons/GameIcon.png"))));
        primaryStage.setTitle(GAME_TITLE);
        primaryStage.setScene(mainMenu.getScene());
        primaryStage.setResizable(false);
        primaryStage.show();
        Main.mainMenu.playMenuBgm();
    }

    public static void startGameFromMenu() {
        if (primaryStage == null) {
            return;
        }

        if (Main.mainMenu != null) {
            Main.mainMenu.stopMenuBgm();
        }
        initializeGame();
        startGameLoop();
        SoundManager.playInGameBgm();
    }

    private static void initializeGame() {
        int[][] gridLayout = PathGenerator.generateRandomPath();
        Main.gameMap = new GameMap(gridLayout);

        Random random = new Random();
        Theme selectedTheme = pickRandomTheme(random);
        gameMap.setTheme(selectedTheme);
        System.out.println("Selected theme: " + selectedTheme);

        String[] decorPool = getDecorPoolForTheme(selectedTheme);
        populateDecorations(gridLayout, gameMap, decorPool, random);
        gameMap.generatePath();
    }

    private static Theme pickRandomTheme(Random random) {
        Theme[] themes = Theme.values();
        return themes[random.nextInt(themes.length)];
    }

    private static String[] getDecorPoolForTheme(Theme theme) {
        return switch (theme) {
            case AUTUMN -> AUTUMN_DECOR_POOL;
            case SPRING -> SPRING_DECOR_POOL;
            default -> NORMAL_DECOR_POOL;
        };
    }

    private static void populateDecorations(int[][] gridLayout, GameMap map, String[] decorPool, Random random) {
        List<Decoration> decorations = map.getDecorations();
        for (int row = 0; row < gridLayout.length; row++) {
            for (int col = 0; col < gridLayout[row].length; col++) {
                if (!isDecoratableTile(gridLayout, map, row, col)) {
                    continue;
                }
                tryPlaceDecoration(decorations, decorPool, random, row, col);
            }
        }
    }

    private static boolean isDecoratableTile(int[][] gridLayout, GameMap map, int row, int col) {
        return gridLayout[row][col] == 0 && !map.isInCastleClearanceZone(row, col);
    }

    private static void tryPlaceDecoration(List<Decoration> decorations, String[] decorPool, Random random, int row, int col) {
        for (int attempt = 0; attempt < DECOR_ATTEMPTS_PER_TILE; attempt++) {
            if (random.nextDouble() >= DECOR_SPAWN_CHANCE) {
                continue;
            }

            String decor = decorPool[random.nextInt(decorPool.length)];
            decorations.add(new Decoration(decor, row, col, DECOR_SCALE));
            maybeAddMushroomForRock(decorations, random, row, col, decor);
            return;
        }
    }

    private static void maybeAddMushroomForRock(List<Decoration> decorations, Random random, int row, int col, String decor) {
        if (!decor.contains("rock") || random.nextDouble() >= ROCK_MUSHROOM_CHANCE) {
            return;
        }
        String mushroom = random.nextBoolean() ? MUSHROOM_1 : MUSHROOM_2;
        decorations.add(new Decoration(mushroom, row, col, DECOR_SCALE));
    }

    private static void startGameLoop() {
        Main.gameView = new GameView(null);
        Main.gameManager = new GameManager(gameMap, gameView);
        gameView.setGameManager(gameManager);
        gameView.setPlacedTowerSelectionListener(tower -> {
            if (tower == null) {
                showTowerShopPanel();
                return;
            }
            showTowerStatusPanel(tower);
        });

        Font customFont = loadGameFont(HUD_FONT_SIZE);
        Main.gameUiFont = customFont;

        HudTexts hudTexts = new HudTexts();
        HBox topLeftHUD = createTopLeftHUD(gameManager, customFont, hudTexts);
        StackPane.setAlignment(topLeftHUD, Pos.TOP_LEFT);

        HBox[] shopRows = new HBox[GameManager.TowerType.values().length];
        Main.towerShopPanel = createTowerShop(gameManager, customFont, shopRows);
        configureSidePanelWidth(Main.towerShopPanel);

        Main.towerStatusPanel = createTowerStatusPanel(null, customFont, Main::exitTowerStatusMode);
        configureSidePanelWidth(Main.towerStatusPanel);
        setPanelVisible(Main.towerStatusPanel, false);

        Main.sidePanelContainer = createSidePanelContainer(Main.towerShopPanel, Main.towerStatusPanel);
        addSoundToggleButton(Main.sidePanelContainer);
        updateShopSelectionUI(Main.gameManager, shopRows);

        StackPane gameWithHUD = createGameWithHud(topLeftHUD);
        Main.gameOverOverlay = createGameOverOverlay();
        Main.victoryOverlay = createVictoryOverlay();
        Main.gameOverOverlay.setVisible(false);
        Main.victoryOverlay.setVisible(false);
        gameWithHUD.getChildren().addAll(Main.gameOverOverlay, Main.victoryOverlay);

        Scene scene = createGameScene(gameWithHUD);
        configureSceneInputHandlers(scene);
        showGameScene(scene);

        Main.gameLoop = createGameLoop(hudTexts, shopRows);
        Main.gameLoop.start();
    }

    private static StackPane createSidePanelContainer(VBox shopPanel, VBox statusPanel) {
        StackPane container = new StackPane();
        container.setPrefWidth(SIDE_PANEL_WIDTH);
        container.setMaxWidth(SIDE_PANEL_WIDTH);
        container.getChildren().addAll(shopPanel, statusPanel);
        return container;
    }

    private static void configureSidePanelWidth(VBox panel) {
        panel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        panel.setMaxWidth(SIDE_PANEL_WIDTH);
    }

    private static void addSoundToggleButton(StackPane container) {
        Font soundButtonFont = Font.font("Verdana", 12);
        Button soundToggleButton = UIUtils.createSoundToggleButton(soundButtonFont, 120, 20);
        container.getChildren().add(soundToggleButton);
        StackPane.setAlignment(soundToggleButton, Pos.TOP_RIGHT);
        StackPane.setMargin(soundToggleButton, new Insets(10, 10, 0, 0));
    }

    private static StackPane createGameWithHud(HBox topLeftHUD) {
        StackPane gameWithHUD = new StackPane();
        gameWithHUD.getChildren().addAll(Main.gameView, topLeftHUD);
        return gameWithHUD;
    }

    private static Scene createGameScene(StackPane gameWithHUD) {
        HBox rootPane = new HBox();
        rootPane.setStyle(ROOT_STYLE);
        rootPane.getChildren().addAll(gameWithHUD, Main.sidePanelContainer);
        Scene scene = new Scene(rootPane);
        scene.setFill(Color.BLACK);
        return scene;
    }

    private static void showGameScene(Scene scene) {
        Main.primaryStage.setTitle(GAME_TITLE);
        Main.primaryStage.setScene(scene);
        Main.primaryStage.setResizable(false);
        Main.primaryStage.show();
    }

    private static void configureSceneInputHandlers(Scene scene) {
        scene.setOnMouseClicked(Main::handleSceneMouseClicked);
        scene.setOnMouseMoved(Main::handleSceneMouseMoved);
        scene.setOnMouseExited(e -> Main.gameView.updateHover(-1, -1));
    }

    private static void handleSceneMouseClicked(MouseEvent event) {
        if (event.isConsumed() || isEventInsideSidePanel(event.getTarget())) {
            return;
        }

        if (event.getButton() == MouseButton.SECONDARY) {
            clearTowerSelection();
            event.consume();
            return;
        }
        if (event.getButton() != MouseButton.PRIMARY || event.getSceneX() >= GAME_CANVAS_WIDTH) {
            return;
        }

        int col = toGridIndex(event.getSceneX());
        int row = toGridIndex(event.getSceneY());
        boolean towerClickHandled = Main.gameView.togglePlacedTowerRangeAt(row, col);
        if (!towerClickHandled) {
            Main.gameView.clearPlacedTowerSelection();
            Main.gameManager.placeTower(row, col);
        }
        event.consume();
    }

    private static void handleSceneMouseMoved(MouseEvent event) {
        if (event.getSceneX() >= GAME_CANVAS_WIDTH) {
            Main.gameView.updateHover(-1, -1);
            return;
        }
        int col = toGridIndex(event.getSceneX());
        int row = toGridIndex(event.getSceneY());
        Main.gameView.updateHover(row, col);
    }

    private static int toGridIndex(double coordinate) {
        return (int) (coordinate / TILE_SIZE);
    }

    private static void clearTowerSelection() {
        Main.gameManager.setSelectedTowerType(null);
        Main.gameView.clearPlacedTowerSelection();
        Main.gameView.updateHover(-1, -1);
    }

    private static AnimationTimer createGameLoop(HudTexts hudTexts, HBox[] shopRows) {
        return new AnimationTimer() {
            private long lastTime = 0;
            private double logicAccumulator = 0.0;

            @Override
            public void handle(long now) {
                double deltaTime = calculateDeltaSeconds(now);
                lastTime = now;

                logicAccumulator += deltaTime * LOGIC_SPEED_MULTIPLIER;
                logicAccumulator = Math.min(
                        logicAccumulator,
                        LOGIC_STEP_SECONDS * MAX_LOGIC_STEPS_PER_FRAME
                );

                if (handleTerminalState(this)) {
                    return;
                }

                int logicSteps = 0;
                while (logicAccumulator >= LOGIC_STEP_SECONDS && logicSteps < MAX_LOGIC_STEPS_PER_FRAME) {
                    Main.gameManager.update(LOGIC_STEP_SECONDS);
                    logicAccumulator -= LOGIC_STEP_SECONDS;
                    logicSteps++;
                }

                Main.gameView.updateCastleHitEffect(deltaTime);
                Main.gameView.drawMap();
                updateHUDTexts(Main.gameManager, hudTexts);
                updateShopSelectionUI(Main.gameManager, shopRows);
            }

            private double calculateDeltaSeconds(long now) {
                if (lastTime <= 0) {
                    return LOGIC_STEP_SECONDS;
                }
                double delta = (now - lastTime) / 1_000_000_000.0;
                return Math.min(delta, MAX_DELTA_SECONDS);
            }
        };
    }

    private static boolean handleTerminalState(AnimationTimer timer) {
        if (Main.gameManager == null) {
            return false;
        }
        if (Main.gameManager.isVictory()) {
            timer.stop();
            SoundManager.stopInGameBgm();
            SoundManager.playVictorySfx();
            showVictoryOverlay();
            return true;
        }
        if (Main.gameManager.isGameOver()) {
            timer.stop();
            SoundManager.stopInGameBgm();
            SoundManager.playDefeatSfx();
            showGameOverOverlay();
            return true;
        }
        return false;
    }

    private static VBox createGameOverOverlay() {
        return createEndStateOverlay("GAME OVER", Color.RED, "Base Destroyed!");
    }

    private static VBox createVictoryOverlay() {
        return createEndStateOverlay("VICTORY!", Color.GOLD, "You survived the siege!");
    }

    private static VBox createEndStateOverlay(String title, Color titleColor, String subtitle) {
        VBox overlay = new VBox(10);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle(OVERLAY_STYLE);
        overlay.setPrefSize(GAME_CANVAS_WIDTH, GAME_CANVAS_HEIGHT);

        Text titleText = new Text(title);
        titleText.setFont(loadGameFont(OVERLAY_TITLE_FONT_SIZE));
        titleText.setFill(titleColor);
        titleText.setEffect(new DropShadow(10, 3, 3, Color.BLACK));

        Text subtitleText = new Text(subtitle);
        subtitleText.setFont(loadGameFont(OVERLAY_SUBTITLE_FONT_SIZE));
        subtitleText.setFill(Color.WHITE);

        Font buttonFont = Font.font("Verdana", 30);
        Button returnButton = UIUtils.createStyledButton(MAIN_MENU_BUTTON_TEXT, buttonFont, 500, 60);
        returnButton.setOnAction(e -> returnToMenu());

        overlay.getChildren().addAll(titleText, subtitleText, returnButton);
        return overlay;
    }

    private static void showGameOverOverlay() {
        showOverlay(Main.gameOverOverlay);
    }

    private static void showVictoryOverlay() {
        showOverlay(Main.victoryOverlay);
    }

    private static void showOverlay(VBox overlay) {
        if (overlay == null) {
            return;
        }
        overlay.setVisible(true);
        overlay.toFront();
    }

    private static HBox createTopLeftHUD(GameManager manager, Font customFont, HudTexts hudTexts) {
        HBox hud = new HBox(15);
        hud.setPadding(new Insets(10));

        ImageView heartIcon = createHudIcon("/Icons/heart.png");
        Text hpText = new Text(String.valueOf(manager.getBaseHealth()));
        hpText.setFont(customFont);
        hpText.setFill(Color.WHITE);

        ImageView coinIcon = createHudIcon("/Icons/coin.png");
        Text moneyText = new Text(String.valueOf(manager.getPlayerMoney()));
        moneyText.setFont(customFont);
        moneyText.setFill(Color.WHITE);

        ImageView timerIcon = createHudIcon("/Icons/timer.png");
        Text timerText = new Text(manager.getFormattedTime());
        timerText.setFont(customFont);
        timerText.setFill(Color.AQUA);

        hudTexts.hpText = hpText;
        hudTexts.moneyText = moneyText;
        hudTexts.timerText = timerText;

        hud.getChildren().addAll(heartIcon, hpText, coinIcon, moneyText, timerIcon, timerText);
        return hud;
    }

    private static ImageView createHudIcon(String resourcePath) {
        ImageView icon = new ImageView(loadImageResource(resourcePath));
        icon.setFitWidth(HUD_FONT_SIZE);
        icon.setFitHeight(HUD_FONT_SIZE);
        return icon;
    }

    private static void updateHUDTexts(GameManager manager, HudTexts hudTexts) {
        hudTexts.hpText.setText(String.valueOf(manager.getBaseHealth()));
        hudTexts.moneyText.setText(String.valueOf(manager.getPlayerMoney()));
        hudTexts.timerText.setText(manager.getFormattedTime());
    }

    private static VBox createTowerShop(GameManager manager, Font customFont, HBox[] shopRows) {
        VBox shop = new VBox(10);
        shop.setPadding(new Insets(20));
        shop.setStyle(PANEL_STYLE);

        Text shopTitle = new Text("TOWER SHOP");
        shopTitle.setFont(Font.font(customFont.getFamily(), SHOP_TITLE_FONT_SIZE));
        shopTitle.setFill(Color.GOLD);
        shop.getChildren().add(shopTitle);

        GridPane towerGrid = createTowerGrid(manager, customFont, shopRows);
        shop.getChildren().add(towerGrid);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        shop.getChildren().add(spacer);

        Button mainMenuButton = createPanelActionButton(MAIN_MENU_BUTTON_TEXT, true);
        mainMenuButton.setOnAction(e -> returnToMenu());
        shop.getChildren().add(mainMenuButton);

        return shop;
    }

    private static GridPane createTowerGrid(GameManager manager, Font customFont, HBox[] shopRows) {
        GridPane towerGrid = new GridPane();
        towerGrid.setHgap(10);
        towerGrid.setVgap(10);

        ColumnConstraints leftColumn = new ColumnConstraints();
        leftColumn.setPercentWidth(50);
        ColumnConstraints rightColumn = new ColumnConstraints();
        rightColumn.setPercentWidth(50);
        towerGrid.getColumnConstraints().addAll(leftColumn, rightColumn);

        GameManager.TowerType[] towerTypes = GameManager.TowerType.values();
        for (int i = 0; i < towerTypes.length; i++) {
            GameManager.TowerType towerType = towerTypes[i];
            HBox towerRow = createTowerShopRow(manager, towerType, customFont);
            towerRow.setMaxWidth(Double.MAX_VALUE);
            towerRow.setMaxHeight(Double.MAX_VALUE);
            towerRow.setPrefHeight(60);
            GridPane.setFillWidth(towerRow, true);
            GridPane.setFillHeight(towerRow, true);

            int row = i / SHOP_COLUMNS;
            int col = i % SHOP_COLUMNS;
            towerGrid.add(towerRow, col, row);
            shopRows[i] = towerRow;
        }
        return towerGrid;
    }

    private static void updateShopSelectionUI(GameManager manager, HBox[] shopRows) {
        GameManager.TowerType selectedType = manager.getSelectedTowerType();
        GameManager.TowerType[] towerTypes = GameManager.TowerType.values();
        for (int i = 0; i < towerTypes.length; i++) {
            HBox row = shopRows[i];
            if (row == null) {
                continue;
            }
            row.setStyle(towerTypes[i] == selectedType ? SHOP_ROW_SELECTED_STYLE : SHOP_ROW_DEFAULT_STYLE);
        }
    }

    private static VBox createTowerStatusPanel(Tower tower, Font customFont, Runnable onBack) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(20));
        panel.setStyle(PANEL_STYLE);

        Text title = new Text("TOWER STATUS");
        title.setFont(Font.font(customFont.getFamily(), STATUS_TITLE_FONT_SIZE));
        title.setFill(Color.GOLD);

        ImageView towerSprite = createCrispStatusTowerSprite(tower);
        StackPane previewCard = new StackPane(towerSprite);
        previewCard.setPadding(new Insets(12));
        previewCard.setMinHeight(180);
        previewCard.setPrefHeight(180);
        previewCard.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-border-color: rgba(255,215,0,0.45); -fx-border-width: 2px;");

        VBox statsCard = createTowerStatsCard(tower, customFont);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        boolean canUpgrade = tower != null && tower.canUpgrade();
        String upgradeLabel = canUpgrade ? "UPGRADE ($" + tower.getUpgradeCost() + ")" : "MAX LEVEL";
        Button upgradeButton = createPanelActionButton(upgradeLabel, canUpgrade);
        upgradeButton.setOnAction(e -> tryUpgradeTower(tower));

        Button closeButton = createPanelActionButton("BACK", true);
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

    private static VBox createTowerStatsCard(Tower tower, Font customFont) {
        VBox statsCard = new VBox(8);
        statsCard.setPadding(new Insets(10));
        statsCard.setStyle("-fx-background-color: rgba(255,255,255,0.1);");

        String towerName = (tower != null) ? tower.getClass().getSimpleName() : "-";
        int level = (tower != null) ? tower.getLevel() : 0;
        int damage = (tower != null) ? tower.getDamage() : 0;
        double range = (tower != null) ? tower.getRange() : 0.0;
        int cooldown = (tower != null) ? tower.getFireCooldown() : 0;

        Text nameText = createStatusText("Name: " + towerName, customFont, STATUS_NAME_FONT_SIZE);
        Text levelText = createStatusText("Level: " + level, customFont, STATUS_INFO_FONT_SIZE);
        Text damageText = createStatusText("Damage: " + damage, customFont, STATUS_INFO_FONT_SIZE);
        Text rangeText = createStatusText("Range: " + String.format("%.1f", range), customFont, STATUS_INFO_FONT_SIZE);
        Text cooldownText = createStatusText("Fire Cooldown: " + cooldown, customFont, STATUS_INFO_FONT_SIZE);

        statsCard.getChildren().addAll(nameText, levelText, damageText, rangeText, cooldownText);
        return statsCard;
    }

    private static Text createStatusText(String value, Font customFont, double size) {
        Text text = new Text(value);
        text.setFont(Font.font(customFont.getFamily(), size));
        text.setFill(Color.WHITE);
        return text;
    }

    private static Button createPanelActionButton(String text, boolean enabled) {
        Button button = new Button(text);
        UIUtils.attachClickSfx(button);
        button.setDisable(!enabled);
        button.setMaxWidth(Double.MAX_VALUE);
        applyPanelButtonStyle(button, false);
        button.setOnMouseEntered(e -> {
            if (!button.isDisable()) {
                applyPanelButtonStyle(button, true);
            }
        });
        button.setOnMouseExited(e -> applyPanelButtonStyle(button, false));
        return button;
    }

    private static void applyPanelButtonStyle(Button button, boolean hovered) {
        button.setStyle(hovered ? PANEL_BUTTON_HOVER_STYLE : PANEL_BUTTON_BASE_STYLE);
    }

    private static void tryUpgradeTower(Tower tower) {
        if (tower == null || Main.gameManager == null) {
            return;
        }
        boolean upgraded = Main.gameManager.tryUpgradeTower(tower);
        if (!upgraded) {
            return;
        }
        showTowerStatusPanel(tower);
        if (Main.gameView != null) {
            Main.gameView.drawMap();
        }
    }

    private static ImageView createCrispStatusTowerSprite(Tower tower) {
        ImageView spriteView = new ImageView();
        spriteView.setPreserveRatio(true);
        spriteView.setSmooth(false);

        String spritePath = (tower != null && tower.getSpriteName() != null) ? tower.getSpriteName() : "";
        if (spritePath.isBlank()) {
            return spriteView;
        }
        if (!spritePath.startsWith("/")) {
            spritePath = "/" + spritePath;
        }

        Image sprite = loadImageResource(spritePath);
        spriteView.setImage(sprite);

        double imageWidth = sprite.getWidth();
        double imageHeight = sprite.getHeight();
        if (imageWidth <= 0 || imageHeight <= 0) {
            return spriteView;
        }

        double maxWidth = 140.0;
        double maxHeight = 150.0;
        double fitScale = Math.min(maxWidth / imageWidth, maxHeight / imageHeight);

        double drawWidth;
        double drawHeight;
        if (fitScale >= 1.0) {
            int integerScale = Math.max(1, (int) Math.floor(fitScale));
            drawWidth = imageWidth * integerScale;
            drawHeight = imageHeight * integerScale;
        } else {
            drawWidth = Math.max(1.0, Math.floor(imageWidth * fitScale));
            drawHeight = Math.max(1.0, Math.floor(imageHeight * fitScale));
        }

        spriteView.setFitWidth(drawWidth);
        spriteView.setFitHeight(drawHeight);
        return spriteView;
    }

    private static void showTowerStatusPanel(Tower tower) {
        if (tower == null || sidePanelContainer == null || towerShopPanel == null) {
            return;
        }
        Font panelFont = gameUiFont != null ? gameUiFont : Font.font("Verdana", 24);
        VBox newStatusPanel = createTowerStatusPanel(tower, panelFont, Main::exitTowerStatusMode);
        configureSidePanelWidth(newStatusPanel);

        if (towerStatusPanel != null) {
            sidePanelContainer.getChildren().remove(towerStatusPanel);
        }
        towerStatusPanel = newStatusPanel;
        sidePanelContainer.getChildren().add(towerStatusPanel);

        setPanelVisible(towerShopPanel, false);
        setPanelVisible(towerStatusPanel, true);
        towerStatusPanel.toFront();
    }

    private static void showTowerShopPanel() {
        if (towerShopPanel == null) {
            return;
        }
        setPanelVisible(towerShopPanel, true);
        if (towerStatusPanel != null) {
            setPanelVisible(towerStatusPanel, false);
        }
    }

    private static void setPanelVisible(VBox panel, boolean visible) {
        panel.setVisible(visible);
        panel.setManaged(visible);
    }

    private static void exitTowerStatusMode() {
        if (Main.gameView != null) {
            Main.gameView.clearPlacedTowerSelection();
        }
        showTowerShopPanel();
    }

    private static HBox createTowerShopRow(GameManager manager, GameManager.TowerType towerType, Font customFont) {
        HBox row = new HBox(8);
        row.setPadding(new Insets(6));
        row.setStyle(SHOP_ROW_DEFAULT_STYLE);

        ImageView towerSprite = new ImageView(loadImageResource(getTowerSpritePath(towerType)));
        towerSprite.setFitWidth(40);
        towerSprite.setPreserveRatio(true);
        towerSprite.setSmooth(true);

        VBox textContainer = new VBox(2);
        Text nameText = new Text(getTowerDisplayName(towerType));
        nameText.setFont(Font.font(customFont.getFamily(), SHOP_NAME_FONT_SIZE));
        nameText.setFill(Color.WHITE);
        nameText.setWrappingWidth(100);

        Text costText = new Text("$" + getTowerCost(manager, towerType));
        costText.setFont(Font.font(customFont.getFamily(), SHOP_COST_FONT_SIZE));
        costText.setFill(Color.GOLD);

        textContainer.getChildren().addAll(nameText, costText);
        row.getChildren().addAll(towerSprite, textContainer);

        row.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                manager.setSelectedTowerType(null);
                if (Main.gameView != null) {
                    Main.gameView.updateHover(-1, -1);
                }
                event.consume();
                return;
            }
            if (event.getButton() == MouseButton.PRIMARY) {
                manager.setSelectedTowerType(towerType);
                event.consume();
            }
        });

        return row;
    }

    private static String getTowerSpritePath(GameManager.TowerType towerType) {
        return getString(towerType);
    }

    static String getString(GameManager.TowerType towerType) {
        return getTowerInfo(towerType).spritePath();
    }

    private static String getTowerDisplayName(GameManager.TowerType towerType) {
        return getTowerInfo(towerType).displayName();
    }

    private static int getTowerCost(GameManager manager, GameManager.TowerType towerType) {
        Tower tempTower = manager.createTowerFromType(towerType);
        if (tempTower != null) {
            return tempTower.getCost();
        }
        return getTowerInfo(towerType).fallbackCost();
    }

    private static TowerInfo getTowerInfo(GameManager.TowerType towerType) {
        TowerInfo fallback = TOWER_INFO.get(GameManager.TowerType.ARCHER);
        if (towerType == null) {
            return fallback;
        }
        return TOWER_INFO.getOrDefault(towerType, fallback);
    }

    private static Map<GameManager.TowerType, TowerInfo> createTowerInfo() {
        EnumMap<GameManager.TowerType, TowerInfo> metadata = new EnumMap<>(GameManager.TowerType.class);
        metadata.put(
                GameManager.TowerType.ARCHER,
                new TowerInfo("/Towers/Combat Towers/spr_tower_archer.png", "Archer", 100)
        );
        metadata.put(
                GameManager.TowerType.CANNON,
                new TowerInfo("/Towers/Combat Towers/spr_tower_cannon.png", "Cannon", 120)
        );
        metadata.put(
                GameManager.TowerType.CROSSBOW,
                new TowerInfo("/Towers/Combat Towers/spr_tower_crossbow.png", "Crossbow", 130)
        );
        metadata.put(
                GameManager.TowerType.ICE_WIZARD,
                new TowerInfo("/Towers/Combat Towers/spr_tower_ice_wizard.png", "Ice Wizard", 150)
        );
        metadata.put(
                GameManager.TowerType.LIGHTNING_WIZARD,
                new TowerInfo("/Towers/Combat Towers/spr_tower_lightning_tower.png", "Lightning Wizard", 150)
        );
        metadata.put(
                GameManager.TowerType.POISON_WIZARD,
                new TowerInfo("/Towers/Combat Towers/spr_tower_poison_wizard.png", "Poison Wizard", 150)
        );
        return Map.copyOf(metadata);
    }

    private static Font loadGameFont(double size) {
        Font font = Font.loadFont(Main.class.getResourceAsStream(GAME_FONT_RESOURCE), size);
        if (font == null) {
            throw new IllegalStateException("Missing font resource: " + GAME_FONT_RESOURCE);
        }
        return font;
    }

    private static Image loadImageResource(String resourcePath) {
        URL imageUrl = Main.class.getResource(resourcePath);
        if (imageUrl == null) {
            throw new IllegalStateException("Missing image resource: " + resourcePath);
        }
        return new Image(imageUrl.toExternalForm());
    }

    private static void returnToMenu() {
        stopGameLoop();
        SoundManager.stopInGameBgm();
        resetGameState();

        if (Main.primaryStage != null && Main.mainMenu != null) {
            Main.primaryStage.setScene(Main.mainMenu.getScene());
            Main.mainMenu.playMenuBgm();
        }
    }

    private static void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
    }

    private static void resetGameState() {
        if (gameManager != null) {
            gameManager.resetGameState();
        }
    }

    private static boolean isEventInsideSidePanel(Object target) {
        if (sidePanelContainer == null || !(target instanceof Node node)) {
            return false;
        }
        while (node != null) {
            if (node == sidePanelContainer) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
