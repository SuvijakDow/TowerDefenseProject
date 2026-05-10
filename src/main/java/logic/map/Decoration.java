package logic.map;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Decorative map object placed on a specific grid tile.
 *
 * <p>World coordinates include a small random offset to avoid uniform visuals.</p>
 */
public final class Decoration {
    /** Total range of the random offset applied to decorations. */
    private static final double OFFSET_RANGE = 16.0;
    /** Half of the offset range for centered random distribution. */
    private static final double HALF_OFFSET_RANGE = OFFSET_RANGE / 2.0;

    /** Asset path for the decoration sprite. */
    private final String spriteName;
    /** Grid row index where the decoration is anchored. */
    private final int row;
    /** Grid column index where the decoration is anchored. */
    private final int col;
    /** X-coordinate in the game world with applied offset. */
    private final double x;
    /** Y-coordinate in the game world with applied offset. */
    private final double y;
    /** Scaling factor applied to the sprite rendering. */
    private final double scale;

    /**
     * Creates a decoration anchored to a grid tile.
     *
     * @param spriteName sprite resource path
     * @param row tile row
     * @param col tile column
     * @param scale render scale multiplier
     */
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

    /**
     * Gets the sprite asset path.
     *
     * @return the sprite name
     */
    public String getSpriteName() { return spriteName; }

    /**
     * Gets the grid row.
     *
     * @return the row index
     */
    public int getRow() { return row; }

    /**
     * Gets the grid column.
     *
     * @return the column index
     */
    public int getCol() { return col; }

    /**
     * Gets the X coordinate in the game world.
     *
     * @return the X coordinate
     */
    public double getX() { return x; }

    /**
     * Gets the Y coordinate in the game world.
     *
     * @return the Y coordinate
     */
    public double getY() { return y; }

    /**
     * Gets the scale multiplier.
     *
     * @return the scale factor
     */
    public double getScale() { return scale; }

    /**
     * Generates a random offset to apply to the world coordinates.
     *
     * @return the random offset
     */
    private static double randomTileOffset() {
        return ThreadLocalRandom.current().nextDouble(-HALF_OFFSET_RANGE, HALF_OFFSET_RANGE);
    }
}
