package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {
    private static final int[][] FALLBACK_GRID = new int[][]{{0}};

    public static int[][] loadMapGrid(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            System.err.println("LevelLoader: filePath is null or blank.");
            return FALLBACK_GRID;
        }

        InputStream stream = LevelLoader.class.getResourceAsStream(filePath);
        if (stream == null) {
            System.err.println("LevelLoader: map not found: " + filePath);
            return FALLBACK_GRID;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            List<int[]> rows = new ArrayList<>();
            String line;
            int expectedCols = -1;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                if (expectedCols == -1) {
                    expectedCols = parts.length;
                } else if (parts.length != expectedCols) {
                    System.err.println("LevelLoader: inconsistent column count in " + filePath);
                    return FALLBACK_GRID;
                }

                int[] row = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    row[i] = Integer.parseInt(parts[i]);
                }
                rows.add(row);
            }

            if (rows.isEmpty()) {
                System.err.println("LevelLoader: map is empty: " + filePath);
                return FALLBACK_GRID;
            }

            int[][] grid = new int[rows.size()][expectedCols];
            for (int r = 0; r < rows.size(); r++) {
                grid[r] = rows.get(r);
            }
            return grid;
        } catch (IOException | NumberFormatException | NullPointerException e) {
            System.err.println("LevelLoader: failed to load " + filePath + " (" + e.getMessage() + ")");
            return FALLBACK_GRID;
        }
    }
}
