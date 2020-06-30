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
    public static final String REDIRECTED_DOMAINS = "redirectedDomains";
    public static final String JAVA_COMMAND = "javaCommand";
    public static final String SELECTED_JAR = "selectedJar";
    public static final String MINECRAFT_JARS = "minecraftJars";
    public static final String FULLSCREEN = "fullscreen";
    public static final String PROXY_LOGGING = "proxyLogging";


    static {
        if(new File(LauncherFiles.MINEONLINE_PROPS_FILE).exists()) {
            loadSettings();

            // Check .minecraft\versions for new jars.

            String[] knownJars = settings.has(MINECRAFT_JARS) ? JSONUtils.getStringArray(settings.getJSONArray(MINECRAFT_JARS)) : new String[0];

            LinkedList<String> officialLauncherVersions = getOfficialLauncherJars(null, null);
            LinkedList<String> newJars = new LinkedList<>();

            officialJarsLoop:
            for (String path : officialLauncherVersions) {
                for (String jar : knownJars) {
                    if(jar.equals(path)) {
                        continue officialJarsLoop;
                    }
                }

                File file = new File(path);



                MinecraftVersionInfo.MinecraftVersion minecraftVersion = MinecraftVersionInfo.getVersion(path);

                try {
                    if (!MinecraftVersionInfo.isRunnableJar(file.getPath())) {
                        continue;
                    }
                } catch (IOException ex) {
                    continue;
                }

                // Only pay attention to versions in versions manifest.
                // Ignore incompatible versions.
                if(minecraftVersion != null) {
                    newJars.add(path);
                } else { }
            }

            settings.put(MINECRAFT_JARS, ArrayUtils.concatenate(knownJars, newJars.toArray(new String[0])));

            saveSettings();

        } else {
            resetSettings();
        }
    }

    private static LinkedList<String> getOfficialLauncherJars(LinkedList<String> fileNames, Path dir) {
        if(fileNames == null)
            fileNames = new LinkedList<>();

        if(dir == null)
            dir = Paths.get(LauncherFiles.MINECRAFT_VERSIONS_PATH);

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if(path.toFile().isDirectory()) {
                    getOfficialLauncherJars(fileNames, path);
                } else if(path.toAbsolutePath().toString().endsWith(".jar")) {
                    String fileName = path.getFileName().toString();
                    if(fileName.startsWith("a") || fileName.startsWith("b") || fileName.startsWith("c") || fileName.startsWith("rd") || fileName.startsWith("inf"))
                        fileNames.add(path.toAbsolutePath().toString());
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return fileNames;
    }

    public static void resetSettings() {
        settings = new JSONObject();
        settings.put(SETTINGS_VERSION, 2);
        settings.put(IS_PREMIUM, true);
        settings.put(REDIRECTED_DOMAINS, new String[] {"www.minecraft.net:-1", "skins.minecraft.net", "session.minecraft.net", "mineraft.net", "www.minecraft.net", "s3.amazonaws.com", "banshee.alex231.com", "mcauth-alex231.rhcloud.com"} );
        settings.put(MINECRAFT_JARS, new String[0]);
        settings.put(JAVA_COMMAND, "java");
        settings.put(SELECTED_JAR, "");
        settings.put(FULLSCREEN, false);
        settings.put(PROXY_LOGGING, false);

        // Check .minecraft\versions for new jars.

        LinkedList<String> officialLauncherVersions = getOfficialLauncherJars(null, null);
        LinkedList<String> newJars = new LinkedList<>();

        officialJarsLoop:
        for (String path : officialLauncherVersions) {
            File file = new File(path);

            MinecraftVersionInfo.MinecraftVersion minecraftVersion = MinecraftVersionInfo.getVersion(path);

            try {
                if (!MinecraftVersionInfo.isRunnableJar(file.getPath())) {
                    continue;
                }
            } catch (IOException ex) {
                continue;
            }

            // Only pay attention to versions in versions manifest.
            // Ignore incompatible versions.
            if(minecraftVersion != null) {
                newJars.add(path);
            } else { }
        }

        settings.put(MINECRAFT_JARS, newJars.toArray(new String[0]));

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
                        //REDIRECTED_DOMAINS, handle manually
                        SELECTED_JAR,
                        MINECRAFT_JARS,
                        FULLSCREEN,
                        PROXY_LOGGING
                };

                resetSettings();

                for (String key : oldKeys) {
                    if(oldSettings.has(key))
                        settings.put(key, oldSettings.get(key));
                }

                if(oldSettings.has(REDIRECTED_DOMAINS)) {
                    JSONArray domains = oldSettings.getJSONArray(REDIRECTED_DOMAINS);
                    domains.put("session.minecraft.net");
                    settings.put(REDIRECTED_DOMAINS, domains);
                }

                saveSettings();
            } else {
                switch (settings.getInt(SETTINGS_VERSION)) {
                    // Upgrading goes here.
                }
            }
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
