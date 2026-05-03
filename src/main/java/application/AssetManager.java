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
                // Environment
                "Environment/Grass/spr_grass_01.png",
                "Environment/Grass/spr_grass_02.png",
                "Environment/Grass/spr_grass_03.png",
                "Environment/Tile Set/spr_tile_set_ground.png",
                "Environment/Tile Set/spr_tile_set_stone.png",
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
                "Environment/Decoration/spr_mushroom_02.png",
                
                // Towers
                "Towers/Castle/spr_castle_blue.png",
                "Towers/Castle/spr_castle_green.png",
                "Towers/Castle/spr_castle_red.png",
                "Towers/Combat Towers/spr_tower_archer.png",
                "Towers/Combat Towers/spr_tower_cannon.png",
                "Towers/Combat Towers/spr_tower_crossbow.png",
                "Towers/Combat Towers/spr_tower_ice_wizard.png",
                "Towers/Combat Towers/spr_tower_lightning_tower.png",
                "Towers/Combat Towers/spr_tower_poison_wizard.png",
                
                // Projectiles
                "Towers/Combat Towers Projectiles/spr_tower_archer_projectile.png",
                "Towers/Combat Towers Projectiles/spr_tower_cannon_projectile.png",
                "Towers/Combat Towers Projectiles/spr_tower_crossbow_projectile.png",
                "Towers/Combat Towers Projectiles/spr_tower_ice_wizard_projectile.png",
                "Towers/Combat Towers Projectiles/spr_tower_lightning_tower_projectile.png",
                "Towers/Combat Towers Projectiles/spr_tower_poison_wizard_projectile.png",
                
                // Enemies
                "Enemies/spr_big_slime.png",
                "Enemies/spr_normal_slime.png",
                "Enemies/spr_king_slime.png",
                "Enemies/spr_goblin.png",
                "Enemies/spr_skeleton.png",
                "Enemies/spr_zombie.png",
                "Enemies/spr_ghost.png",
                "Enemies/spr_demon.png",
                "Enemies/spr_bat.png"
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
