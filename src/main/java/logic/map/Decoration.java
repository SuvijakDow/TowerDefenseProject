package logic.map;

import java.util.concurrent.ThreadLocalRandom;

public final class Decoration {
    private static final double OFFSET_RANGE = 16.0;
    private static final double HALF_OFFSET_RANGE = OFFSET_RANGE / 2.0;

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

        int tileSize = GameMap.PATH_TILE_PIXEL_SIZE;
        double offsetX = randomTileOffset();
        double offsetY = randomTileOffset();
        this.x = col * tileSize + tileSize / 2.0 + offsetX;
        this.y = row * tileSize + tileSize / 2.0 + offsetY;
    }

    public String getSpriteName() { return spriteName; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getScale() { return scale; }

    private static double randomTileOffset() {
        return ThreadLocalRandom.current().nextDouble(-HALF_OFFSET_RANGE, HALF_OFFSET_RANGE);
    }
}
