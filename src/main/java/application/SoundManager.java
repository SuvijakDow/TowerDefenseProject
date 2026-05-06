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

    public static boolean isMuted = false;
    private static boolean initialized = false;
    private static BgmScene activeBgmScene = BgmScene.NONE;

    private SoundManager() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        clickSfx = loadAudioClip(CLICK_SFX_PATH, "click");
        if (clickSfx != null) {
            clickSfx.setVolume(0.4);
            clickSfx.setRate(0.9);
        }

        castleAttackedSfx = loadAudioClip(CASTLE_ATTACKED_SFX_PATH, "castleIsAttacked");
        if (castleAttackedSfx != null) {
            castleAttackedSfx.setVolume(0.55);
        }

        enemyAttackedSfx = loadAudioClip(ENEMY_ATTACKED_SFX_PATH, "enemyIsAttacked");
        if (enemyAttackedSfx != null) {
            enemyAttackedSfx.setVolume(0.22);
        }

        defeatSfx = loadAudioClip(DEFEAT_SFX_PATH, "defeatSound");
        if (defeatSfx != null) {
            defeatSfx.setVolume(0.7);
        }

        victorySfx = loadAudioClip(VICTORY_SFX_PATH, "victorySound");
        if (victorySfx != null) {
            victorySfx.setVolume(0.7);
        }

        menuBgmPlayer = loadLoopingBgmPlayer(MENU_BGM_PATH, "startupMenu", 0.7);
        inGameBgmPlayer = loadLoopingBgmPlayer(IN_GAME_BGM_PATH, "inGame", 0.5);

        initialized = true;
    }

    public static void playClickSfx() {
        if (isMuted || !initialized || clickSfx == null) {
            return;
        }
        clickSfx.play();
    }

    public static void playCastleIsAttackedSfx() {
        if (isMuted || !initialized || castleAttackedSfx == null) {
            return;
        }
        castleAttackedSfx.play();
    }

    public static void playEnemyIsAttackedSfx() {
        if (isMuted || !initialized || enemyAttackedSfx == null) {
            return;
        }
        enemyAttackedSfx.play();
    }

    public static void playDefeatSfx() {
        if (isMuted || !initialized || defeatSfx == null) {
            return;
        }
        defeatSfx.play();
    }

    public static void playVictorySfx() {
        if (isMuted || !initialized || victorySfx == null) {
            return;
        }
        victorySfx.play();
    }

    public static void playMenuBgm() {
        if (!initialized) {
            return;
        }
        activeBgmScene = BgmScene.MENU;
        stopPlayer(inGameBgmPlayer);
        if (isMuted || menuBgmPlayer == null) {
            return;
        }
        menuBgmPlayer.stop();
        menuBgmPlayer.play();
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
        if (!initialized) {
            return;
        }
        activeBgmScene = BgmScene.IN_GAME;
        stopPlayer(menuBgmPlayer);
        if (isMuted || inGameBgmPlayer == null) {
            return;
        }
        inGameBgmPlayer.stop();
        inGameBgmPlayer.play();
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

    private static void resumeActiveBgm() {
        if (isMuted) {
            return;
        }
        if (activeBgmScene == BgmScene.MENU && menuBgmPlayer != null) {
            menuBgmPlayer.stop();
            menuBgmPlayer.play();
            return;
        }
        if (activeBgmScene == BgmScene.IN_GAME && inGameBgmPlayer != null) {
            inGameBgmPlayer.stop();
            inGameBgmPlayer.play();
        }
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

    private static void stopPlayer(MediaPlayer player) {
        if (player != null) {
            player.stop();
        }
    }
}
