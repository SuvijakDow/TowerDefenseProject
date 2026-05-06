package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Background;

import java.util.Objects;

public class MainMenu {
    private static final String BACKGROUND_IMAGE_RESOURCE = "/Backgrounds/main_menu_bg.png";
    private static final String TITLE_FONT_RESOURCE = "/Fonts/CWEBS.TTF";
    private static final int SCENE_WIDTH = 1180;
    private static final int SCENE_HEIGHT = 600;
    private static final double TITLE_FONT_SIZE = 120;
    private static final double BUTTON_FONT_SIZE = 30;
    private static final double SOUND_BUTTON_FONT_SIZE = 12;

    private Scene mainMenuScene;
    private Button soundToggleButton;

    public MainMenu() {
        StackPane root = createRootWithBackground();
        VBox uiContainer = createUiContainer();

        Font titleFont = Font.loadFont(getClass().getResourceAsStream(TITLE_FONT_RESOURCE), TITLE_FONT_SIZE);
        Text titleText = new Text("TOWER DEFENSE");
        titleText.setFont(titleFont);
        titleText.setFill(Color.WHITE);
        titleText.setEffect(new DropShadow(10, 3, 3, Color.BLACK));

        Font buttonFont = Font.font("Verdana", BUTTON_FONT_SIZE);
        Button startButton = UIUtils.createStyledButton("START GAME", buttonFont);
        Button exitButton = UIUtils.createStyledButton("EXIT", buttonFont);
        Font soundButtonFont = Font.font("Verdana", SOUND_BUTTON_FONT_SIZE);
        soundToggleButton = UIUtils.createSoundToggleButton(soundButtonFont, 120, 30);

        startButton.setOnAction(e -> startGame());
        exitButton.setOnAction(e -> exitGame());
        uiContainer.getChildren().addAll(titleText, startButton, exitButton);

        root.getChildren().addAll(uiContainer, soundToggleButton);
        StackPane.setAlignment(soundToggleButton, Pos.TOP_RIGHT);
        StackPane.setMargin(soundToggleButton, new Insets(16, 16, 0, 0));

        mainMenuScene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
    }

    private StackPane createRootWithBackground() {
        StackPane root = new StackPane();
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(BACKGROUND_IMAGE_RESOURCE)));
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );
        root.setBackground(new Background(background));
        return root;
    }

    private VBox createUiContainer() {
        VBox uiContainer = new VBox(30);
        uiContainer.setAlignment(Pos.CENTER);
        uiContainer.setStyle("-fx-background-color: transparent;");
        uiContainer.setTranslateY(-50);
        return uiContainer;
    }

    private void startGame() {
        stopMenuBgm();
        Main.startGameFromMenu();
    }

    private void exitGame() {
        stopMenuBgm();
        System.exit(0);
    }

    public void playMenuBgm() {
        UIUtils.refreshSoundToggleButtonText(soundToggleButton);
        SoundManager.playMenuBgm();
    }

    public void stopMenuBgm() {
        SoundManager.stopMenuBgm();
    }

    public Scene getScene() {
        return mainMenuScene;
    }
}
