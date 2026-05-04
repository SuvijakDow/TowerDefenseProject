package application;

import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.input.MouseButton;

/**
 * Utility class for shared UI components and styling
 */
public class UIUtils {
    
    /**
     * Create a styled button with default size
     */
    public static Button createStyledButton(String text, Font font) {
        return createStyledButton(text, font, 300, 60);
    }
    
    /**
     * Create a styled button with custom size
     */
    public static Button createStyledButton(String text, Font font, double maxWidth, double prefHeight) {
        Button button = new Button(text);
        button.setFont(font);
        button.setMaxWidth(maxWidth);
        button.setPrefHeight(prefHeight);
        
        // Get font family and size from the actual font
        String fontFamily = font.getFamily();
        double fontSize = font.getSize();
        
        // Base CSS - use actual font family and size
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
        
        // Hover effect - use actual font family and size
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
}
