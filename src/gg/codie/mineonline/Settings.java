package gg.codie.mineonline;

import gg.codie.utils.ArrayUtils;
import gg.codie.utils.JSONUtils;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class Settings {

    public static JSONObject settings;

    static {
        if(new File(LauncherFiles.MINEONLINE_PROPS_FILE).exists()) {
            loadSettings();

            // Check .minecraft\versions for new jars.

            String[] knownJars = settings.has("minecraftJars") ? JSONUtils.getStringArray(settings.getJSONArray("minecraftJars")) : new String[0];

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

            settings.put("minecraftJars", ArrayUtils.concatenate(knownJars, newJars.toArray(new String[0])));

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
        settings.put("isPremium", true);
        settings.put("redirectedDomains", new String[] {"www.minecraft.net:-1", "skins.minecraft.net", "mineraft.net", "www.minecraft.net", "s3.amazonaws.com", "banshee.alex231.com", "mcauth-alex231.rhcloud.com"} );
        settings.put("javaCommand", "java");
        settings.put("seletedJar", "");
        settings.put("fullscreen", false);
        settings.put("proxyLogging", false);

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

        settings.put("minecraftJars", newJars.toArray(new String[0]));

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
