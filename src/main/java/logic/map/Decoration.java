package logic.map;

public class Decoration {
    private final String spriteName;
    private final double x;
    private final double y;
    private final double scale;

    public Decoration(String spriteName, double x, double y, double scale) {
        this.spriteName = spriteName;
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    public String getSpriteName() { return spriteName; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getScale() { return scale; }
}
