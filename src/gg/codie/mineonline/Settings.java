package gg.codie.mineonline;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Settings {

    public static JSONObject settings;

    public static final String SETTINGS_VERSION = "settingsVersion";
    public static final String FULLSCREEN = "fullscreen";
    public static final String MINECRAFT_UPDATE_URL = "minecraftUpdateURL";
    public static final String JAVA_HOME = "javaHome";
    public static final String CLIENT_LAUNCH_ARGS = "clientLaunchArgs";
    public static final String FOV = "fov";
    public static final String GUI_SCALE = "guiScale";
    public static final String TEXTURE_PACK = "texturePack";
    public static final String HIDE_VERSION_STRING = "hideVersionString";
    public static final String GAME_WIDTH = "gameWidth";
    public static final String GAME_HEIGHT = "gameHeight";
    public static final String CUSTOM_CAPES = "customCapes";
    public static final String LEFT_HANDED = "leftHanded";

    public static final String SAMPLE_COUNT = "sampleCount";
    public static final String STENCIL_COUNT = "stencilCount";
    public static final String COVERAGE_SAMPLE_COUNT = "coverageSampleCount";

    private static final int SETTINGS_VERSION_NUMBER = 10;

    private static boolean readonly = true;

    static {
        try {
            Class.forName("org.json.JSONObject");

            readonly = false;

            if (new File(LauncherFiles.MINEONLINE_SETTINGS_FILE).exists()) {
                loadSettings();
            } else {
                resetSettings();
            }
        } catch (ClassNotFoundException ex) {
            resetSettings();
        }
    }

    public static void resetSettings() {
        settings = new JSONObject();
        settings.put(SETTINGS_VERSION, SETTINGS_VERSION_NUMBER);
        settings.put(FULLSCREEN, false);
        settings.put(MINECRAFT_UPDATE_URL, "");
        settings.put(JAVA_HOME, "");
        settings.put(CLIENT_LAUNCH_ARGS, "");
        settings.put(FOV, 70);
        settings.put(GUI_SCALE, 3);
        settings.put(TEXTURE_PACK, "");
        settings.put(HIDE_VERSION_STRING, false);
        settings.put(GAME_WIDTH, 854);
        settings.put(GAME_HEIGHT, 480);
        settings.put(CUSTOM_CAPES, true);
        settings.put(LEFT_HANDED, false);
        settings.put(SAMPLE_COUNT, 0);
        settings.put(STENCIL_COUNT, 0);
        settings.put(COVERAGE_SAMPLE_COUNT, 0);

        saveSettings();
        loadSettings();
    }

    public static void loadSettings() {
        try (FileInputStream input = new FileInputStream(LauncherFiles.MINEONLINE_SETTINGS_FILE)) {
            // load a settings file
            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytes_read = input.read(buffer, 0, 8096)) != -1) {
                for(int i = 0; i < bytes_read; i++) {
                    stringBuffer.append((char)buffer[i]);
                }
            }

            settings = new JSONObject(stringBuffer.toString());

            // Assume V1, reset settings.
            if(!settings.has(SETTINGS_VERSION)) {
                resetSettings();
                saveSettings();
            } else {
                switch (settings.getInt(SETTINGS_VERSION)) {
                    case 3:
                        settings.put(MINECRAFT_UPDATE_URL, "");
                        settings.put(JAVA_HOME, "");
                        settings.put(CLIENT_LAUNCH_ARGS, "");
                    case 4:
                        settings.put(FOV, 70);
                        settings.put(GUI_SCALE, 3);
                    case 5:
                        settings.put(TEXTURE_PACK, "");
                    case 6:
                        settings.put(HIDE_VERSION_STRING, false);
                    case 7:
                        settings.put(GAME_WIDTH, 854);
                        settings.put(GAME_HEIGHT, 480);
                        settings.put(CUSTOM_CAPES, true);
                    case 8:
                        settings.put(LEFT_HANDED, false);
                    case 9:
                        settings.put(SAMPLE_COUNT, 0);
                        settings.put(STENCIL_COUNT, 0);
                        settings.put(COVERAGE_SAMPLE_COUNT, 0);
                }
                settings.put(SETTINGS_VERSION, SETTINGS_VERSION_NUMBER);
            }

            if (settings.has("redirectedDomains"))
                settings.remove("redirectedDomains");

            saveSettings();
        } catch (IOException ex) {
            saveSettings();
        }
    }

    public  static void saveSettings() {
        if (readonly)
            return;

        try {
            FileWriter fileWriter = new FileWriter(LauncherFiles.MINEONLINE_SETTINGS_FILE, false);
            fileWriter.write(settings.toString(2));
            fileWriter.close();

            FileInputStream input = new FileInputStream(LauncherFiles.MINEONLINE_SETTINGS_FILE);
            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytes_read = input.read(buffer, 0, 8096)) != -1) {
                for(int i = 0; i < bytes_read; i++) {
                    stringBuffer.append((char)buffer[i]);
                }
            }

            input.close();

            settings = new JSONObject(stringBuffer.toString());
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
