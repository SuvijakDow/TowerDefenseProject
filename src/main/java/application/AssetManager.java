package application;

import javafx.scene.image.Image;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton image repository that preloads and serves game asset textures.
 */
public final class AssetManager {
    /** Prefix used to locate resources within the classpath. */
    private static final String RESOURCE_PREFIX = "/";

    /** List of file paths for environment-related image assets. */
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

    /** List of file paths for tower-related image assets. */
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

    /** List of file paths for projectile-related image assets. */
    private static final List<String> PROJECTILE_ASSETS = List.of(
            "Towers/Combat Towers Projectiles/spr_tower_archer_projectile.png",
            "Towers/Combat Towers Projectiles/spr_tower_cannon_projectile.png",
            "Towers/Combat Towers Projectiles/spr_tower_crossbow_projectile.png",
            "Towers/Combat Towers Projectiles/spr_tower_ice_wizard_projectile.png",
            "Towers/Combat Towers Projectiles/spr_tower_lightning_tower_projectile.png",
            "Towers/Combat Towers Projectiles/spr_tower_poison_wizard_projectile.png"
    );

    /** List of file paths for enemy-related image assets. */
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

    /** A combined list of all predefined asset paths to be loaded. */
    private static final List<String> ALL_ASSETS = buildAssetList();

    /** A map storing the preloaded {@link Image} instances, keyed by their relative path. */
    private final Map<String, Image> images = new HashMap<>();

    /** Lazy initialization holder class for the singleton instance. */
    private static final class Holder {
        /** The single instance of {@link AssetManager}. */
        private static final AssetManager INSTANCE = new AssetManager();
    }

    /**
     * Private constructor to enforce the Singleton pattern.
     * Automatically triggers the loading of all predefined assets.
     */
    private AssetManager() {
        loadAssets();
    }

    /**
     * Returns the singleton asset manager instance.
     *
     * @return global asset manager
     */
    public static AssetManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Iterates through the {@link #ALL_ASSETS} list and loads each asset.
     */
    private void loadAssets() {
        for (String assetPath : ALL_ASSETS) {
            loadAsset(assetPath);
        }
    }

    /**
     * Loads a single image asset from the given path and stores it in the {@link #images} map.
     * Prints an error to standard error stream if the resource is missing or invalid.
     *
     * @param assetPath The relative path of the image asset to load.
     */
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

    /**
     * Helper method to build a unified list of all assets that need to be preloaded.
     *
     * @return An unmodifiable list containing all asset paths.
     */
    private static List<String> buildAssetList() {
        List<String> allAssets = new ArrayList<>();
        allAssets.addAll(ENVIRONMENT_ASSETS);
        allAssets.addAll(TOWER_ASSETS);
        allAssets.addAll(PROJECTILE_ASSETS);
        allAssets.addAll(ENEMY_ASSETS);
        return List.copyOf(allAssets);
    }

    /**
     * Looks up a preloaded image by asset key.
     *
     * <p>Expected key format matches project-relative resource paths such as
     * {@code Environment/Grass/spr_grass_01.png}.</p>
     *
     * @param name asset key used during preload
     * @return image for the key, or {@code null} when no image is available
     */
    public Image getImage(String name) {
        return images.get(name);
    }
}
