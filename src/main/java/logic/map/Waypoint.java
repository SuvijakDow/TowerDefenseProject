package logic.map;

/**
 * A path node in world coordinates that enemies move toward.
 */
public class Waypoint {
    /** The X coordinate in world space. */
    private double x;
    /** The Y coordinate in world space. */
    private double y;

    /**
     * Creates a waypoint at the provided world position.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Waypoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the X coordinate.
     *
     * @return the X coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the X coordinate.
     *
     * @param x the new X coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the Y coordinate.
     *
     * @return the Y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the Y coordinate.
     *
     * @param y the new Y coordinate
     */
    public void setY(double y) {
        this.y = y;
    }
}
