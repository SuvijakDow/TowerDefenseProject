package application;

import java.net.URL;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public final class SoundManager {
    private enum BgmScene {
        NONE, MENU, IN_GAME
    }

    private static final String CLICK_SFX_PATH = "/Audio/click.mp3";
    private static final String CASTLE_ATTACKED_SFX_PATH = "/Audio/castleIsAttacked.mp3";
    private static final String ENEMY_ATTACKED_SFX_PATH = "/Audio/enemyIsAttacked.mp3";
    private static final String DEFEAT_SFX_PATH = "/Audio/defeatSound.mp3";
    private static final String VICTORY_SFX_PATH = "/Audio/victorySound.mp3";
    private static final String MENU_BGM_PATH = "/Audio/startupMenu.mp3";
    private static final String IN_GAME_BGM_PATH = "/Audio/inGame.mp3";

    private static AudioClip clickSfx;
    private static AudioClip castleAttackedSfx;
    private static AudioClip enemyAttackedSfx;
    private static AudioClip defeatSfx;
    private static AudioClip victorySfx;

    private static MediaPlayer menuBgmPlayer;
    private static MediaPlayer inGameBgmPlayer;

    private static boolean isMuted = false;
    private static boolean initialized = false;
    private static BgmScene activeBgmScene = BgmScene.NONE;

    private SoundManager() {
    }

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

    public static void playClickSfx() {
        playSfx(clickSfx);
    }

    public static void playCastleIsAttackedSfx() {
        playSfx(castleAttackedSfx);
    }

    public static void playEnemyIsAttackedSfx() {
        playSfx(enemyAttackedSfx);
    }

    public static void playDefeatSfx() {
        playSfx(defeatSfx);
    }

    public static void playVictorySfx() {
        playSfx(victorySfx);
    }

    public static void playMenuBgm() {
        activateBgm(BgmScene.MENU, menuBgmPlayer, inGameBgmPlayer);
    }

    public static void stopMenuBgm() {
        if (!initialized) {
            return;
        }
        if (activeBgmScene == BgmScene.MENU) {
            activeBgmScene = BgmScene.NONE;
        }
        stopPlayer(menuBgmPlayer);
    }

    public static void playInGameBgm() {
        activateBgm(BgmScene.IN_GAME, inGameBgmPlayer, menuBgmPlayer);
    }

    public static void stopInGameBgm() {
        if (!initialized) {
            return;
        }
        if (activeBgmScene == BgmScene.IN_GAME) {
            activeBgmScene = BgmScene.NONE;
        }
        stopPlayer(inGameBgmPlayer);
    }

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

    public static boolean isMuted() {
        return isMuted;
    }

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

    private static void playSfx(AudioClip clip) {
        if (!initialized || isMuted || clip == null) {
            return;
        }
        clip.play();
    }

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

    private static AudioClip loadAudioClip(String resourcePath, String label) {
        URL url = SoundManager.class.getResource(resourcePath);
        if (url == null) {
            System.err.println("Missing audio resource (" + label + "): " + resourcePath);
            return null;
        }
        return new AudioClip(url.toExternalForm());
    }

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

    private static void configureAudioClip(AudioClip clip, double volume, Double rate) {
        if (clip == null) {
            return;
        }
        clip.setVolume(volume);
        if (rate != null) {
            clip.setRate(rate);
        }
    }

    private static void restartPlayer(MediaPlayer player) {
        player.stop();
        player.play();
    }

    private static void stopPlayer(MediaPlayer player) {
        if (player != null) {
            player.stop();
        }
    }
}
