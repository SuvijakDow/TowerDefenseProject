package logic;

import javafx.scene.paint.Color;

/**
 * Floating combat text that rises and fades over a short lifetime.
 */
public final class DamageText {
    /** Minimum allowed opacity. */
    private static final double MIN_OPACITY = 0.0;
    /** Maximum allowed opacity. */
    private static final double MAX_OPACITY = 1.0;

    /** The text to display. */
    private String text;
    /** The x-coordinate of the text. */
    private double x;
    /** The y-coordinate of the text. */
    private double y;
    /** Remaining lifetime in seconds. */
    private double lifetime;
    /** Current opacity level (0.0 to 1.0). */
    private double opacity;
    /** Vertical movement speed in pixels per second. */
    private double velocityY;
    /** Color of the text. */
    private Color color;

    /** Total duration the text stays on screen in seconds. */
    private static final double INITIAL_LIFETIME = 0.8;
    /** Upward floating speed in pixels per second. */
    private static final double FLOAT_SPEED = 30.0;
    /** Rate at which the text fades out (opacity per second). */
    private static final double FADE_SPEED = 1.0 / INITIAL_LIFETIME;

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
     * Gets the displayed text.
     *
     * @return the text string
     */
    public String getText() { return text; }

    /**
     * Gets the x-coordinate.
     *
     * @return the x-coordinate
     */
    public double getX() { return x; }

    /**
     * Gets the y-coordinate.
     *
     * @return the y-coordinate
     */
    public double getY() { return y; }

    /**
     * Gets the current opacity.
     *
     * @return the opacity (0.0 to 1.0)
     */
    public double getOpacity() { return opacity; }

    /**
     * Gets the text color.
     *
     * @return the text color
     */
    public Color getColor() { return color; }

    /**
     * Sets the displayed text.
     *
     * @param text the text string
     */
    public void setText(String text) { this.text = text; }

    /**
     * Sets the x-coordinate.
     *
     * @param x the x-coordinate
     */
    public void setX(double x) { this.x = x; }

    /**
     * Sets the y-coordinate.
     *
     * @param y the y-coordinate
     */
    public void setY(double y) { this.y = y; }

    /**
     * Sets the current opacity, clamped between 0.0 and 1.0.
     *
     * @param opacity the opacity value
     */
    public void setOpacity(double opacity) { this.opacity = clampOpacity(opacity); }

    /**
     * Sets the text color.
     *
     * @param color the text color
     */
    public void setColor(Color color) { this.color = color; }

    /**
     * Clamps the opacity value between minimum and maximum bounds.
     *
     * @param value the raw opacity
     * @return the clamped opacity
     */
    private static double clampOpacity(double value) {
        return Math.clamp(value, MIN_OPACITY, MAX_OPACITY);
    }
}
