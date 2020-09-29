package gg.codie.mineonline;

import gg.codie.utils.ArrayUtils;
import gg.codie.utils.JSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class Settings {

    public static JSONObject settings;

    public static final String SETTINGS_VERSION = "settingsVersion";
    public static final String IS_PREMIUM = "isPremium";
    public static final String JAVA_COMMAND = "javaCommand";
    public static final String FULLSCREEN = "fullscreen";
    public static final String MINECRAFT_UPDATE_URL = "minecraftUpdateURL";
    private static final int SETTINGS_VERSION_NUMBER = 4;

    static {
        if(new File(LauncherFiles.MINEONLINE_PROPS_FILE).exists()) {
            loadSettings();
        } else {
            resetSettings();
        }
    }

    public static void resetSettings() {
        settings = new JSONObject();
        settings.put(SETTINGS_VERSION, SETTINGS_VERSION_NUMBER);
        settings.put(IS_PREMIUM, true);
        settings.put(JAVA_COMMAND, "java");
        settings.put(FULLSCREEN, false);
        settings.put(MINECRAFT_UPDATE_URL, "");

        saveSettings();
        loadSettings();
    }

    public static void loadSettings() {
        try (FileInputStream input = new FileInputStream(LauncherFiles.MINEONLINE_PROPS_FILE)) {
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
                JSONObject oldSettings = settings;
                String[] oldKeys = new String[] {
                        IS_PREMIUM,
                        JAVA_COMMAND,
                        FULLSCREEN,
                };

                resetSettings();

                for (String key : oldKeys) {
                    if(oldSettings.has(key))
                        settings.put(key, oldSettings.get(key));
                }

                saveSettings();
            } else {
                switch (settings.getInt(SETTINGS_VERSION)) {
                    case 3:
                        settings.put(MINECRAFT_UPDATE_URL, "");
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
        try {
            FileWriter fileWriter = new FileWriter(LauncherFiles.MINEONLINE_PROPS_FILE, false);
            fileWriter.write(settings.toString());
            fileWriter.close();

            FileInputStream input = new FileInputStream(LauncherFiles.MINEONLINE_PROPS_FILE);
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
