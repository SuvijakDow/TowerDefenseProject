package logic.map;

public class Decoration {
    private final String spriteName;
    private final int row;
    private final int col;
    private final double x;
    private final double y;
    private final double scale;

    public Decoration(String spriteName, int row, int col, double scale) {
        this.spriteName = spriteName;
        this.row = row;
        this.col = col;
        this.scale = scale;
        int ts = logic.map.GameMap.PATH_TILE_PIXEL_SIZE;
        
        // Small random offset between -8.0 and 8.0 to keep it strictly within its block
        double offsetX = (Math.random() * 16.0) - 8.0;
        double offsetY = (Math.random() * 16.0) - 8.0;
        
        this.x = col * ts + ts / 2.0 + offsetX;
        this.y = row * ts + ts / 2.0 + offsetY;
    }

    public String getSpriteName() { return spriteName; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getScale() { return scale; }
}
