package application;

import javafx.scene.image.Image;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AssetManager {
    private static final String RESOURCE_PREFIX = "/";

    private static final List<String> ENVIRONMENT_ASSETS = List.of(
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
            "Environment/Decoration/spr_mushroom_02.png"
    );

    private static final List<String> TOWER_ASSETS = List.of(
            "Towers/Castle/spr_castle_blue.png",
            "Towers/Castle/spr_castle_green.png",
            "Towers/Castle/spr_castle_red.png",
            "Towers/Combat Towers/spr_tower_archer.png",
            "Towers/Combat Towers/spr_tower_cannon.png",
            "Towers/Combat Towers/spr_tower_crossbow.png",
            "Towers/Combat Towers/spr_tower_ice_wizard.png",
            "Towers/Combat Towers/spr_tower_lightning_tower.png",
            "Towers/Combat Towers/spr_tower_poison_wizard.png"
    );

    private static final List<String> PROJECTILE_ASSETS = List.of(
            "Towers/Combat Towers Projectiles/spr_tower_archer_projectile.png",
            "Towers/Combat Towers Projectiles/spr_tower_cannon_projectile.png",
            "Towers/Combat Towers Projectiles/spr_tower_crossbow_projectile.png",
            "Towers/Combat Towers Projectiles/spr_tower_ice_wizard_projectile.png",
            "Towers/Combat Towers Projectiles/spr_tower_lightning_tower_projectile.png",
            "Towers/Combat Towers Projectiles/spr_tower_poison_wizard_projectile.png"
    );

    private static final List<String> ENEMY_ASSETS = List.of(
            "Enemies/spr_big_slime.png",
            "Enemies/spr_normal_slime.png",
            "Enemies/spr_king_slime.png",
            "Enemies/spr_goblin.png",
            "Enemies/spr_skeleton.png",
            "Enemies/spr_zombie.png",
            "Enemies/spr_demon.png",
            "Enemies/spr_bat.png"
    );

    private static final List<String> ALL_ASSETS = buildAssetList();

    private final Map<String, Image> images = new HashMap<>();

    private static final class Holder {
        private static final AssetManager INSTANCE = new AssetManager();
    }

    private AssetManager() {
        loadAssets();
    }

    public static AssetManager getInstance() {
        return Holder.INSTANCE;
    }

    private void loadAssets() {
        for (String assetPath : ALL_ASSETS) {
            loadAsset(assetPath);
        }
    }

    private void loadAsset(String assetPath) {
        URL resourceUrl = AssetManager.class.getResource(RESOURCE_PREFIX + assetPath);
        if (resourceUrl == null) {
            System.err.println("Failed to load asset, missing resource: " + assetPath);
            return;
        }

        Image image = new Image(resourceUrl.toExternalForm());
        if (image.isError()) {
            System.err.println("Failed to load asset, invalid image: " + assetPath);
            return;
        }
        images.put(assetPath, image);
    }

    private static List<String> buildAssetList() {
        List<String> allAssets = new ArrayList<>();
        allAssets.addAll(ENVIRONMENT_ASSETS);
        allAssets.addAll(TOWER_ASSETS);
        allAssets.addAll(PROJECTILE_ASSETS);
        allAssets.addAll(ENEMY_ASSETS);
        return List.copyOf(allAssets);
    }

    public Image getImage(String name) {
        return images.get(name);
    }
}
