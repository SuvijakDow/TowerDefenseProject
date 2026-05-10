package application;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.text.Font;

/**
 * Utility helpers for creating and updating commonly styled JavaFX UI controls.
 */
public final class UIUtils {
    /** Base background color style for buttons. */
    private static final String BASE_BUTTON_BG = "rgba(0, 0, 0, 0.75)";
    /** Hover background color style for buttons. */
    private static final String HOVER_BUTTON_BG = "rgba(50, 50, 50, 0.9)";

    /**
     * Private constructor to prevent instantiation.
     */
    private UIUtils() {
    }

    /**
     * Creates a styled button using the default size profile.
     *
     * @param text button label
     * @param font font used for rendering text
     * @return styled button with click sound effect and hover style
     */
    public static Button createStyledButton(String text, Font font) {
        return createStyledButton(text, font, 300, 60);
    }

    /**
     * Creates a styled button with custom size.
     *
     * @param text button label
     * @param font font used for rendering text
     * @param maxWidth maximum width in JavaFX layout units
     * @param prefHeight preferred height in JavaFX layout units
     * @return styled button with click sound effect and hover style
     */
    public static Button createStyledButton(String text, Font font, double maxWidth, double prefHeight) {
        Button button = new Button(text);
        button.setFont(font);
        button.setMaxWidth(maxWidth);
        button.setPrefHeight(prefHeight);
        attachClickSfx(button);

        String fontFamily = font.getFamily();
        double fontSize = font.getSize();

        String baseStyle = buildButtonStyle(fontFamily, fontSize, BASE_BUTTON_BG);
        button.setStyle(baseStyle);

        String hoverStyle = buildButtonStyle(fontFamily, fontSize, HOVER_BUTTON_BG);

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    /**
     * Attaches click sound playback to a button action.
     *
     * @param button target button; ignored when {@code null}
     */
    public static void attachClickSfx(Button button) {
        if (button == null) {
            return;
        }
        button.addEventHandler(ActionEvent.ACTION, e -> SoundManager.playClickSfx());
    }

    /**
     * Creates a sound toggle button using the default size profile.
     *
     * @param font font used for rendering text
     * @return button bound to the global mute toggle
     */
    public static Button createSoundToggleButton(Font font) {
        return createSoundToggleButton(font, 220, 48);
    }

    /**
     * Creates a sound toggle button with custom size.
     *
     * @param font font used for rendering text
     * @param maxWidth maximum width in JavaFX layout units
     * @param prefHeight preferred height in JavaFX layout units
     * @return button bound to the global mute toggle
     */
    public static Button createSoundToggleButton(Font font, double maxWidth, double prefHeight) {
        Button button = createStyledButton(getSoundToggleText(), font, maxWidth, prefHeight);
        button.setOnAction(e -> {
            SoundManager.toggleMute();
            refreshSoundToggleButtonText(button);
        });
        return button;
    }

    /**
     * Refreshes the sound toggle label to reflect the current mute state.
     *
     * @param button target button; ignored when {@code null}
     */
    public static void refreshSoundToggleButtonText(Button button) {
        if (button == null) {
            return;
        }
        button.setText(getSoundToggleText());
    }

    /**
     * Gets the appropriate text for the sound toggle button based on the current mute state.
     *
     * @return the string label for the sound toggle button
     */
    private static String getSoundToggleText() {
        return SoundManager.isMuted() ? "🔇 SOUND: OFF" : "🔊 SOUND: ON";
    }

    /**
     * Builds the CSS style string for buttons.
     *
     * @param fontFamily the font family to use
     * @param fontSize the font size to use
     * @param backgroundColor the background color to apply
     * @return the combined CSS style string
     */
    private static String buildButtonStyle(String fontFamily, double fontSize, String backgroundColor) {
        return "-fx-background-color: " + backgroundColor + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-family: '" + fontFamily + "'; " +
                "-fx-font-size: " + fontSize + "px; " +
                "-fx-background-radius: 12px; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 12px; " +
                "-fx-cursor: hand;";
    }
}
