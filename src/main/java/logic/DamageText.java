package logic;

import javafx.scene.paint.Color;

/**
 * Floating combat text that rises and fades over a short lifetime.
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

    /**
     * Creates a new damage text entry.
     *
     * @param text displayed text (typically damage amount)
     * @param x world x-coordinate
     * @param y world y-coordinate
     * @param color text color
     */
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
     * Advances movement and opacity for one frame.
     *
     * @param deltaTime elapsed time in seconds
     * @return true when this text has expired and should be removed
     */
    public boolean update(double deltaTime) {
        y += velocityY * deltaTime;

        lifetime -= deltaTime;
        opacity = clampOpacity(lifetime * FADE_SPEED);

        return lifetime <= 0;
    }

    /**
     * getters
     */
    public String getText() { return text; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getOpacity() { return opacity; }
    public Color getColor() { return color; }

    /**
     * setters
     */
    public void setText(String text) { this.text = text; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setOpacity(double opacity) { this.opacity = clampOpacity(opacity); }
    public void setColor(Color color) { this.color = color; }

    private static double clampOpacity(double value) {
        return Math.clamp(value, MIN_OPACITY, MAX_OPACITY);
    }
}
