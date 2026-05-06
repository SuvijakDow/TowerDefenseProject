package application;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.text.Font;

public final class UIUtils {
    private static final String BASE_BUTTON_BG = "rgba(0, 0, 0, 0.75)";
    private static final String HOVER_BUTTON_BG = "rgba(50, 50, 50, 0.9)";

    private UIUtils() {
    }

    public static Button createStyledButton(String text, Font font) {
        return createStyledButton(text, font, 300, 60);
    }

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

    public static void attachClickSfx(Button button) {
        if (button == null) {
            return;
        }
        button.addEventHandler(ActionEvent.ACTION, e -> SoundManager.playClickSfx());
    }

    public static Button createSoundToggleButton(Font font) {
        return createSoundToggleButton(font, 220, 48);
    }

    public static Button createSoundToggleButton(Font font, double maxWidth, double prefHeight) {
        Button button = createStyledButton(getSoundToggleText(), font, maxWidth, prefHeight);
        button.setOnAction(e -> {
            SoundManager.toggleMute();
            refreshSoundToggleButtonText(button);
        });
        return button;
    }

    public static void refreshSoundToggleButtonText(Button button) {
        if (button == null) {
            return;
        }
        button.setText(getSoundToggleText());
    }

    private static String getSoundToggleText() {
        return SoundManager.isMuted() ? "🔇 SOUND: OFF" : "🔊 SOUND: ON";
    }

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
