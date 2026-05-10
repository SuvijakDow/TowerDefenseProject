package application;

import java.net.URL;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Centralized audio controller for sound effects, background music, and mute state.
 */
public final class SoundManager {
    /**
     * Enum representing the different contexts for background music.
     */
    private enum BgmScene {
        /** No background music. */
        NONE, 
        /** Main menu background music. */
        MENU, 
        /** In-game background music. */
        IN_GAME
    }

    /** Path to the generic click sound effect. */
    private static final String CLICK_SFX_PATH = "/Audio/click.mp3";
    /** Path to the sound effect played when the castle is attacked. */
    private static final String CASTLE_ATTACKED_SFX_PATH = "/Audio/castleIsAttacked.mp3";
    /** Path to the sound effect played when an enemy is attacked. */
    private static final String ENEMY_ATTACKED_SFX_PATH = "/Audio/enemyIsAttacked.mp3";
    /** Path to the defeat sound effect. */
    private static final String DEFEAT_SFX_PATH = "/Audio/defeatSound.mp3";
    /** Path to the victory sound effect. */
    private static final String VICTORY_SFX_PATH = "/Audio/victorySound.mp3";
    /** Path to the main menu background music. */
    private static final String MENU_BGM_PATH = "/Audio/startupMenu.mp3";
    /** Path to the in-game background music. */
    private static final String IN_GAME_BGM_PATH = "/Audio/inGame.mp3";

    /** Audio clip for the click sound effect. */
    private static AudioClip clickSfx;
    /** Audio clip for the castle attacked sound effect. */
    private static AudioClip castleAttackedSfx;
    /** Audio clip for the enemy attacked sound effect. */
    private static AudioClip enemyAttackedSfx;
    /** Audio clip for the defeat sound effect. */
    private static AudioClip defeatSfx;
    /** Audio clip for the victory sound effect. */
    private static AudioClip victorySfx;

    /** Media player for the main menu background music. */
    private static MediaPlayer menuBgmPlayer;
    /** Media player for the in-game background music. */
    private static MediaPlayer inGameBgmPlayer;

    /** Global flag indicating if all sound is muted. */
    private static boolean isMuted = false;
    /** Flag indicating if the audio assets have been successfully initialized. */
    private static boolean initialized = false;
    /** The currently active background music scene. */
    private static BgmScene activeBgmScene = BgmScene.NONE;

    /**
     * Private constructor to prevent instantiation.
     */
    private SoundManager() {
    }

    /**
     * Loads all audio assets and prepares BGM players.
     *
     * <p>This method is idempotent and only performs work on first call.</p>
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        clickSfx = loadAudioClip(CLICK_SFX_PATH, "click");
        configureAudioClip(clickSfx, 0.4, 0.9);

        castleAttackedSfx = loadAudioClip(CASTLE_ATTACKED_SFX_PATH, "castleIsAttacked");
        configureAudioClip(castleAttackedSfx, 0.55, null);

        enemyAttackedSfx = loadAudioClip(ENEMY_ATTACKED_SFX_PATH, "enemyIsAttacked");
        configureAudioClip(enemyAttackedSfx, 0.22, null);

        defeatSfx = loadAudioClip(DEFEAT_SFX_PATH, "defeatSound");
        configureAudioClip(defeatSfx, 0.7, null);

        victorySfx = loadAudioClip(VICTORY_SFX_PATH, "victorySound");
        configureAudioClip(victorySfx, 0.7, null);

        menuBgmPlayer = loadLoopingBgmPlayer(MENU_BGM_PATH, "startupMenu", 0.7);
        inGameBgmPlayer = loadLoopingBgmPlayer(IN_GAME_BGM_PATH, "inGame", 0.5);

        initialized = true;
    }

    /**
     * Internal helper to play an audio clip if initialized and not muted.
     *
     * @param clip the {@link AudioClip} to play
     */
    private static void playSfx(AudioClip clip) {
        if (!initialized || isMuted || clip == null) {
            return;
        }
        clip.play();
    }

    /**
     * Plays the button click sound effect.
     *
     * <p>No-op when audio is not initialized or currently muted.</p>
     */
    public static void playClickSfx() {
        playSfx(clickSfx);
    }

    /**
     * Plays the castle-damaged sound effect.
     *
     * <p>No-op when audio is not initialized or currently muted.</p>
     */
    public static void playCastleIsAttackedSfx() {
        playSfx(castleAttackedSfx);
    }

    /**
     * Plays the enemy-damaged sound effect.
     *
     * <p>No-op when audio is not initialized or currently muted.</p>
     */
    public static void playEnemyIsAttackedSfx() {
        playSfx(enemyAttackedSfx);
    }

    /**
     * Plays the defeat sound effect.
     *
     * <p>No-op when audio is not initialized or currently muted.</p>
     */
    public static void playDefeatSfx() {
        playSfx(defeatSfx);
    }

    /**
     * Plays the victory sound effect.
     *
     * <p>No-op when audio is not initialized or currently muted.</p>
     */
    public static void playVictorySfx() {
        playSfx(victorySfx);
    }

    /**
     * Internal helper to activate a specific background music scene.
     *
     * @param scene the scene to activate
     * @param targetPlayer the player associated with the new scene
     * @param playerToStop the player associated with the old scene
     */
    private static void activateBgm(BgmScene scene, MediaPlayer targetPlayer, MediaPlayer playerToStop) {
        if (!initialized) {
            return;
        }
        activeBgmScene = scene;
        stopPlayer(playerToStop);
        if (isMuted || targetPlayer == null) {
            return;
        }
        restartPlayer(targetPlayer);
    }

    /**
     * Activates looping menu background music and stops in-game BGM.
     *
     * <p>When muted, only active scene state is updated.</p>
     */
    public static void playMenuBgm() {
        activateBgm(BgmScene.MENU, menuBgmPlayer, inGameBgmPlayer);
    }

    /**
     * Activates looping in-game background music and stops menu BGM.
     *
     * <p>When muted, only active scene state is updated.</p>
     */
    public static void playInGameBgm() {
        activateBgm(BgmScene.IN_GAME, inGameBgmPlayer, menuBgmPlayer);
    }

    /**
     * Stops menu background music and clears active scene when it was selected.
     */
    public static void stopMenuBgm() {
        if (!initialized) {
            return;
        }
        if (activeBgmScene == BgmScene.MENU) {
            activeBgmScene = BgmScene.NONE;
        }
        stopPlayer(menuBgmPlayer);
    }

    /**
     * Stops in-game background music and clears active scene when it was selected.
     */
    public static void stopInGameBgm() {
        if (!initialized) {
            return;
        }
        if (activeBgmScene == BgmScene.IN_GAME) {
            activeBgmScene = BgmScene.NONE;
        }
        stopPlayer(inGameBgmPlayer);
    }

    /**
     * Safely stops a media player if it is not null.
     *
     * @param player the {@link MediaPlayer} to stop
     */
    private static void stopPlayer(MediaPlayer player) {
        if (player != null) {
            player.stop();
        }
    }

    /**
     * Restarts a media player from the beginning.
     *
     * @param player the {@link MediaPlayer} to restart
     */
    private static void restartPlayer(MediaPlayer player) {
        player.stop();
        player.play();
    }

    /**
     * Toggles global mute state.
     *
     * <p>Muting stops both BGM players; unmuting resumes the currently active scene BGM.</p>
     */
    public static void toggleMute() {
        if (!initialized) {
            return;
        }
        isMuted = !isMuted;
        if (isMuted) {
            stopPlayer(menuBgmPlayer);
            stopPlayer(inGameBgmPlayer);
            return;
        }
        resumeActiveBgm();
    }

    /**
     * Resumes the background music of the currently active scene if not muted.
     */
    private static void resumeActiveBgm() {
        if (isMuted) {
            return;
        }
        if (activeBgmScene == BgmScene.MENU && menuBgmPlayer != null) {
            restartPlayer(menuBgmPlayer);
            return;
        }
        if (activeBgmScene == BgmScene.IN_GAME && inGameBgmPlayer != null) {
            restartPlayer(inGameBgmPlayer);
        }
    }

    /**
     * Loads an {@link AudioClip} from the given resource path.
     *
     * @param resourcePath the relative path to the audio file
     * @param label a descriptive label for error logging
     * @return the loaded {@link AudioClip}, or {@code null} if loading fails
     */
    private static AudioClip loadAudioClip(String resourcePath, String label) {
        URL url = SoundManager.class.getResource(resourcePath);
        if (url == null) {
            System.err.println("Missing audio resource (" + label + "): " + resourcePath);
            return null;
        }
        return new AudioClip(url.toExternalForm());
    }

    /**
     * Loads a {@link MediaPlayer} for looping background music.
     *
     * @param resourcePath the relative path to the audio file
     * @param label a descriptive label for error logging
     * @param volume the volume level (0.0 to 1.0)
     * @return the initialized {@link MediaPlayer}, or {@code null} if loading fails
     */
    private static MediaPlayer loadLoopingBgmPlayer(String resourcePath, String label, double volume) {
        URL url = SoundManager.class.getResource(resourcePath);
        if (url == null) {
            System.err.println("Missing BGM resource (" + label + "): " + resourcePath);
            return null;
        }
        MediaPlayer mediaPlayer = new MediaPlayer(new Media(url.toExternalForm()));
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(volume);
        return mediaPlayer;
    }

    /**
     * Configures volume and optional playback rate for an {@link AudioClip}.
     *
     * @param clip the clip to configure
     * @param volume the volume level (0.0 to 1.0)
     * @param rate the playback rate multiplier (optional)
     */
    private static void configureAudioClip(AudioClip clip, double volume, Double rate) {
        if (clip == null) {
            return;
        }
        clip.setVolume(volume);
        if (rate != null) {
            clip.setRate(rate);
        }
    }

    /**
     * Indicates whether audio output is currently muted.
     *
     * @return {@code true} when muted; otherwise {@code false}
     */
    public static boolean isMuted() {
        return isMuted;
    }
}
