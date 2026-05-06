package logic;

import javafx.scene.paint.Color;

/**
 * Represents floating damage text that appears when an enemy takes damage.
 * Text floats upward and fades out over time.
 */
public final class DamageText {
    private static final double MIN_OPACITY = 0.0;
    private static final double MAX_OPACITY = 1.0;

    private String text;
    private double x;
    private double y;
    private double lifetime;
    private double opacity;
    private double velocityY;
    private Color color;

    private static final double INITIAL_LIFETIME = 0.8; // seconds
    private static final double FLOAT_SPEED = 30.0; // pixels per second
    private static final double FADE_SPEED = 1.0 / INITIAL_LIFETIME; // opacity per second

    public DamageText(String text, double x, double y, Color color) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.lifetime = INITIAL_LIFETIME;
        this.opacity = 1.0;
        this.velocityY = -FLOAT_SPEED; // Negative for upward movement
        this.color = color;
    }
    
    /**
     * Updates the damage text position and opacity.
     * @param deltaTime Time elapsed since last update in seconds
     * @return true if the text should be removed, false otherwise
     */
    public boolean update(double deltaTime) {
        // Move upward
        y += velocityY * deltaTime;

        // Fade out
        lifetime -= deltaTime;
        opacity = clampOpacity(lifetime * FADE_SPEED);

        // Return true when lifetime expires
        return lifetime <= 0;
    }

    // Getters
    public String getText() { return text; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getOpacity() { return opacity; }
    public Color getColor() { return color; }

    // Setters for flexibility
    public void setText(String text) { this.text = text; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setOpacity(double opacity) { this.opacity = clampOpacity(opacity); }
    public void setColor(Color color) { this.color = color; }

    private static double clampOpacity(double value) {
        return Math.clamp(value, MIN_OPACITY, MAX_OPACITY);
    }
}
