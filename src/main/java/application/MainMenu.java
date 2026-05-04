package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Background;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import java.net.URL;

public class MainMenu {
    private static final String MENU_BGM_PATH = "/Audio/startupMenu.mp3";
    private Scene mainMenuScene;
    public Stage primaryStage;
    private MediaPlayer menuBgmPlayer;
    
    public MainMenu(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createMainMenuScene();
    }
    
    private void createMainMenuScene() {
        // 1. Root Node: StackPane
        StackPane root = new StackPane();
        
        // 2. Background Image (Bottom Layer)
        Image backgroundImage = new Image(getClass().getResourceAsStream("/Backgrounds/main_menu_bg.png"));
        BackgroundImage backgroundImg = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        root.setBackground(new Background(backgroundImg));
        
        // 3. UI Container (Top Layer)
        VBox uiContainer = new VBox(30);
        uiContainer.setAlignment(Pos.CENTER);
        uiContainer.setStyle("-fx-background-color: transparent;");
        uiContainer.setStyle("-fx-translate-y: -50px;");
        
        // 4. Title Node
        Font titleFont = Font.loadFont(getClass().getResourceAsStream("/Fonts/CWEBS.TTF"), 120);
        Text titleText = new Text("TOWER DEFENSE");
        titleText.setFont(titleFont);
        titleText.setFill(Color.WHITE);
        titleText.setEffect(new DropShadow(10, 3, 3, Color.BLACK));
        
        // 5. Button Nodes
        Font buttonFont = Font.font("Verdana", 30); 
        Button startButton = UIUtils.createStyledButton("START GAME", buttonFont);
        Button exitButton = UIUtils.createStyledButton("EXIT", buttonFont);
        
        // Set button actions
        startButton.setOnAction(e -> startGame());
        exitButton.setOnAction(e -> exitGame());
        
        // Add nodes to VBox
        uiContainer.getChildren().addAll(titleText, startButton, exitButton);
        
        // Add UI container to StackPane
        root.getChildren().add(uiContainer);
        
        // Create scene
        mainMenuScene = new Scene(root, 1180, 600);
    }
    
    private void startGame() {
        Main.playMenuClickSfx();
        stopMenuBgm();
        Main.startGameFromMenu();
    }
    
    private void exitGame() {
        Main.playMenuClickSfx();
        disposeMenuBgm();
        System.exit(0);
    }
    
    public Scene getScene() {
        return mainMenuScene;
    }

    public void playMenuBgm() {
        ensureMenuBgmPlayer();
        if (menuBgmPlayer != null) {
            menuBgmPlayer.play();
        }
    }

    public void stopMenuBgm() {
        if (menuBgmPlayer != null) {
            menuBgmPlayer.stop();
        }
    }

    private void disposeMenuBgm() {
        if (menuBgmPlayer != null) {
            menuBgmPlayer.stop();
            menuBgmPlayer.dispose();
            menuBgmPlayer = null;
        }
    }

    private void ensureMenuBgmPlayer() {
        if (menuBgmPlayer != null) {
            return;
        }
        URL bgmUrl = getClass().getResource(MENU_BGM_PATH);
        if (bgmUrl == null) {
            System.err.println("Failed to load menu BGM: " + MENU_BGM_PATH);
            return;
        }
        Media media = new Media(bgmUrl.toExternalForm());
        menuBgmPlayer = new MediaPlayer(media);
        menuBgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        menuBgmPlayer.setVolume(0.8);
    }
}
