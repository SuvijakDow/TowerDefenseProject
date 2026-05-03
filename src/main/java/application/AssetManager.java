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
                "Environment/Grass/spr_grass_01.png",
                "Environment/Grass/spr_grass_02.png",
                "Environment/Grass/spr_grass_03.png",
                "Environment/Tile Set/spr_tile_set_ground.png",
                "Environment/Tile Set/spr_tile_set_stone.png",
                "Towers/Castle/spr_castle_blue.png",
                "Towers/Castle/spr_castle_green.png",
                "Towers/Castle/spr_castle_red.png",
                "Environment/Decoration/spr_rock_01.png",
                "Environment/Decoration/spr_rock_02.png",
                "Environment/Decoration/spr_rock_03.png",
                "Environment/Decoration/spr_tree_01_normal.png",
                "Environment/Decoration/spr_tree_01_autumn.png",
                "Environment/Decoration/spr_tree_01_cherry_blossom.png",
                "Environment/Decoration/spr_tree_02_normal.png",
                "Environment/Decoration/spr_tree_02_autumn.png",
                "Environment/Decoration/spr_tree_02_spruce.png",
                "Environment/Decoration/spr_mushroom_01.png",
                "Environment/Decoration/spr_mushroom_02.png"
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
