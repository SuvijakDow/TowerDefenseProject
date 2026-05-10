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

/**
 * Builds and controls the main menu scene shown before gameplay starts.
 */
public class MainMenu {
    /** Path to the main menu background image. */
    private static final String BACKGROUND_IMAGE_RESOURCE = "/Backgrounds/main_menu_bg.png";
    /** Path to the custom font used for the title. */
    private static final String TITLE_FONT_RESOURCE = "/Fonts/CWEBS.TTF";
    /** The width of the main menu scene. */
    private static final int SCENE_WIDTH = 1180;
    /** The height of the main menu scene. */
    private static final int SCENE_HEIGHT = 600;
    /** The font size of the title text. */
    private static final double TITLE_FONT_SIZE = 120;
    /** The font size of the primary menu buttons. */
    private static final double BUTTON_FONT_SIZE = 30;
    /** The font size of the sound toggle button. */
    private static final double SOUND_BUTTON_FONT_SIZE = 12;

    /** The scene instance that represents the main menu. */
    private Scene mainMenuScene;
    /** The button used to toggle global sound on and off. */
    private Button soundToggleButton;

    /**
     * Builds the main menu scene with title, start/exit actions, and sound toggle.
     */
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

    /**
     * Creates the root stack pane with the background image applied.
     *
     * @return a {@link StackPane} configured with the background
     */
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

    /**
     * Creates the VBox container that holds the title and menu buttons.
     *
     * @return a {@link VBox} configured for UI elements
     */
    private VBox createUiContainer() {
        VBox uiContainer = new VBox(30);
        uiContainer.setAlignment(Pos.CENTER);
        uiContainer.setStyle("-fx-background-color: transparent;");
        uiContainer.setTranslateY(-50);
        return uiContainer;
    }

    /**
     * Action handler to transition from the main menu to the actual game.
     */
    private void startGame() {
        stopMenuBgm();
        Main.startGameFromMenu();
    }

    /**
     * Action handler to gracefully exit the application.
     */
    private void exitGame() {
        stopMenuBgm();
        System.exit(0);
    }

    /**
     * Refreshes the sound button label and starts menu background music.
     */
    public void playMenuBgm() {
        UIUtils.refreshSoundToggleButtonText(soundToggleButton);
        SoundManager.playMenuBgm();
    }

    /**
     * Stops menu background music if it is currently active.
     */
    public void stopMenuBgm() {
        SoundManager.stopMenuBgm();
    }

    /**
     * Returns the scene rendered by this menu instance.
     *
     * @return main menu scene
     */
    public Scene getScene() {
        return mainMenuScene;
    }
}
