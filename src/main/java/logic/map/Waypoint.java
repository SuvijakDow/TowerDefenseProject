package logic.map;

/**
 * A path node in world coordinates that enemies move toward.
 */
public class Waypoint {
    private double x;
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

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
