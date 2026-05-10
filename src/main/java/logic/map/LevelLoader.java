package logic.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads map grids from classpath text resources.
 *
 * <p>Input format is whitespace-separated integers per row.
 * Invalid files return a 1x1 fallback grid containing {@code 0}.</p>
 */
public final class LevelLoader {
    /** Fallback 1x1 grid used when map loading fails. */
    private static final int[][] FALLBACK_GRID = {{0}};

    private LevelLoader() {
    }

    /**
     * Loads a grid from a classpath resource path.
     *
     * @param filePath resource path (for example {@code /Paths/path1.txt})
     * @return loaded grid, or fallback grid if loading/parsing fails
     */
    public static int[][] loadMapGrid(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            System.err.println("LevelLoader: filePath is null or blank.");
            return fallbackGrid();
        }

        InputStream stream = LevelLoader.class.getResourceAsStream(filePath);
        if (stream == null) {
            System.err.println("LevelLoader: map not found: " + filePath);
            return fallbackGrid();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return parseGrid(filePath, reader);
        } catch (IOException e) {
            System.err.println("LevelLoader: failed to load " + filePath + " (" + e.getMessage() + ")");
            return fallbackGrid();
        }
    }

    /**
     * Parses the grid from a BufferedReader.
     *
     * @param filePath the resource path being read
     * @param reader the reader containing the file content
     * @return the parsed 2D array, or a fallback grid on failure
     * @throws IOException if an I/O error occurs
     */
    private static int[][] parseGrid(String filePath, BufferedReader reader) throws IOException {
        List<int[]> rows = new ArrayList<>();
        String line;
        int expectedCols = -1;

        while ((line = reader.readLine()) != null) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String[] parts = trimmed.split("\\s+");
            if (expectedCols < 0) {
                expectedCols = parts.length;
            } else if (parts.length != expectedCols) {
                System.err.println("LevelLoader: inconsistent column count in " + filePath);
                return fallbackGrid();
            }

            int[] row = parseRow(parts, filePath);
            if (row == null) {
                return fallbackGrid();
            }
            rows.add(row);
        }

        if (rows.isEmpty()) {
            System.err.println("LevelLoader: map is empty: " + filePath);
            return fallbackGrid();
        }

        int[][] grid = new int[rows.size()][expectedCols];
        for (int row = 0; row < rows.size(); row++) {
            grid[row] = rows.get(row);
        }
        return grid;
    }

    /**
     * Parses a single row of the grid.
     *
     * @param parts the string array of numeric tokens
     * @param filePath the resource path being read (for error logging)
     * @return the parsed integer array representing a row, or null on failure
     */
    private static int[] parseRow(String[] parts, String filePath) {
        int[] row = new int[parts.length];
        for (int col = 0; col < parts.length; col++) {
            try {
                row[col] = Integer.parseInt(parts[col]);
            } catch (NumberFormatException e) {
                System.err.println("LevelLoader: invalid number in " + filePath + " (" + parts[col] + ")");
                return null;
            }
        }
        return row;
    }

    /**
     * Creates and returns a copy of the fallback grid.
     *
     * @return a safe fallback grid array
     */
    private static int[][] fallbackGrid() {
        return new int[][]{{FALLBACK_GRID[0][0]}};
    }
}
