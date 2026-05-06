package application;

import java.net.URL;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public final class SoundManager {
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

    private static boolean initialized = false;

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

        menuBgmPlayer = loadLoopingBgmPlayer(MENU_BGM_PATH, "startupMenu", 0.45);
        inGameBgmPlayer = loadLoopingBgmPlayer(IN_GAME_BGM_PATH, "inGame", 0.5);

        initialized = true;
    }

    public static void playClickSfx() {
        if (!initialized || clickSfx == null) {
            return;
        }
        clickSfx.play();
    }

    public static void playCastleIsAttackedSfx() {
        if (!initialized || castleAttackedSfx == null) {
            return;
        }
        castleAttackedSfx.play();
    }

    public static void playEnemyIsAttackedSfx() {
        if (!initialized || enemyAttackedSfx == null) {
            return;
        }
        enemyAttackedSfx.play();
    }

    public static void playDefeatSfx() {
        if (!initialized || defeatSfx == null) {
            return;
        }
        defeatSfx.play();
    }

    public static void playVictorySfx() {
        if (!initialized || victorySfx == null) {
            return;
        }
        victorySfx.play();
    }

    public static void playMenuBgm() {
        if (!initialized || menuBgmPlayer == null) {
            return;
        }
        stopPlayer(inGameBgmPlayer);
        menuBgmPlayer.stop();
        menuBgmPlayer.play();
    }

    public static void stopMenuBgm() {
        if (!initialized) {
            return;
        }
        stopPlayer(menuBgmPlayer);
    }

    public static void playInGameBgm() {
        if (!initialized || inGameBgmPlayer == null) {
            return;
        }
        stopPlayer(menuBgmPlayer);
        inGameBgmPlayer.stop();
        inGameBgmPlayer.play();
    }

    public static void stopInGameBgm() {
        if (!initialized) {
            return;
        }
        stopPlayer(inGameBgmPlayer);
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
