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
import java.util.Arrays;
import java.util.LinkedList;

public class Properties {

    public static JSONObject properties;

    static {
        if(new File(LauncherFiles.MINEONLINE_PROPS_FILE).exists()) {
            loadProperties();

            // Check .minecraft\versions for new jars.

            String[] knownJars = JSONUtils.getStringArray(properties.getJSONArray("minecraftJars"));

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

            properties.put("minecraftJars", ArrayUtils.concatenate(knownJars, newJars.toArray(new String[0])));

            saveProperties();

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
                    if(path.getFileName().startsWith("a") || path.getFileName().startsWith("b") || path.getFileName().startsWith("c") || path.getFileName().startsWith("rd") || path.getFileName().startsWith("inf"))
                        fileNames.add(path.toAbsolutePath().toString());
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return fileNames;
    }

    public static void resetSettings() {
        properties = new JSONObject();
        properties.put("isPremium", true);
        properties.put("apiDomainName", "mineonline.codie.gg");
        properties.put("redirectedDomains", new String[] {"www.minecraft.net:-1", "skins.minecraft.net", "mineraft.net", "www.minecraft.net", "s3.amazonaws.com", "banshee.alex231.com", "mcauth-alex231.rhcloud.com"} );
        properties.put("javaCommand", "java");
        properties.put("seletedJar", "");
        properties.put("lastServer", "");
        properties.put("fullscreen", false);

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

        properties.put("minecraftJars", newJars.toArray(new String[0]));

        saveProperties();
        loadProperties();
    }

    public static void loadProperties() {
        try (FileInputStream input = new FileInputStream(LauncherFiles.MINEONLINE_PROPS_FILE)) {
            // load a properties file
            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytes_read = input.read(buffer, 0, 8096)) != -1) {
                for(int i = 0; i < bytes_read; i++) {
                    stringBuffer.append((char)buffer[i]);
                }
            }

            properties = new JSONObject(stringBuffer.toString());
        } catch (IOException ex) {
            saveProperties();
        }
    }

    public  static void saveProperties() {
        try {
            FileWriter fileWriter = new FileWriter(LauncherFiles.MINEONLINE_PROPS_FILE, false);
            fileWriter.write(properties.toString());
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

            properties = new JSONObject(stringBuffer.toString());
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
