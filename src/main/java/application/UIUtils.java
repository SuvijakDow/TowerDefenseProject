package application;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.text.Font;

public class UIUtils {
    
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
        
        String baseStyle = "-fx-background-color: rgba(0, 0, 0, 0.75); " +
                          "-fx-text-fill: white; " +
                          "-fx-font-family: '" + fontFamily + "'; " +
                          "-fx-font-size: " + fontSize + "px; " +
                          "-fx-background-radius: 12px; " +
                          "-fx-border-color: white; " +
                          "-fx-border-width: 2px; " +
                          "-fx-border-radius: 12px; " +
                          "-fx-cursor: hand;";
        button.setStyle(baseStyle);
        
        String hoverStyle = "-fx-background-color: rgba(50, 50, 50, 0.9); " +
                           "-fx-text-fill: white; " +
                           "-fx-font-family: '" + fontFamily + "'; " +
                           "-fx-font-size: " + fontSize + "px; " +
                           "-fx-background-radius: 12px; " +
                           "-fx-border-color: white; " +
                           "-fx-border-width: 2px; " +
                           "-fx-border-radius: 12px; " +
                           "-fx-cursor: hand;";
        
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
}
