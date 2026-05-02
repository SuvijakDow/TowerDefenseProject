package application;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private static AssetManager instance;
    private Map<String, Image> images;

    private AssetManager() {
        images = new HashMap<>();
        loadAssets();
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    private void loadAssets() {
        String[] assets = {
                "spr_grass_02.png",
                "spr_tile_set_ground.png",
                "spr_castle_blue.png",
                "spr_rock_01.png",
                "spr_tree_01_normal.png",
                "spr_rock_01.png",
                "spr_rock_02.png",
                "spr_rock_03.png",
                "spr_tree_01_normal.png",
                "spr_tree_02_normal.png",
                "spr_tree_01_autumn.png",
                "spr_mushroom_01.png",
                "spr_mushroom_02.png"
        };

        for (String asset : assets) {
            try {
                String path = getClass().getResource("/" + asset).toExternalForm();
                Image img = new Image(path);
                images.put(asset, img);
            } catch (Exception e) {
                System.err.println("Failed to load asset: " + asset);
                e.printStackTrace();
            }
        }
    }

    public Image getImage(String name) {
        return images.get(name);
    }
}
