package gg.codie.mineonline;

import jdk.nashorn.api.scripting.URLReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MinecraftVersionInfo {
    private static JSONObject versionsJson;
    private static LinkedList<MinecraftVersion> versions = new LinkedList();

    static {
        loadVersions();
    }

    public static MinecraftVersion getVersionByMD5(String md5) {
        List<MinecraftVersion> matches = versions.stream().filter(version -> version.md5.equals(md5)).collect(Collectors.toList());
        if(matches.size() < 1) {
            return null;
        }
        return matches.get(0);
    }

    public static class MinecraftVersion {
        public final String sha256;
        public final String name;
        public final String md5;
        public final boolean baseURLHasNoPort;

        private MinecraftVersion(String sha256, String name, String md5, boolean baseURLHasNoPort) {
            this.sha256 = sha256;
            this.name = name;
            this.md5 = md5;
            this.baseURLHasNoPort = baseURLHasNoPort;
        }
    }

    private static void loadVersions() {
        try (URLReader input = new URLReader(LauncherFiles.VERSION_INFO_PATH)) {
            // load a properties file
            char[] buffer = new char[8096];
            int bytes_read = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytes_read = input.read(buffer, 0, 8096)) != -1) {
                for(int i = 0; i < bytes_read; i++) {
                    stringBuffer.append(buffer[i]);
                }
            }

            versionsJson = new JSONObject(stringBuffer.toString());

            Iterator keyIterator = versionsJson.keys();

            while (keyIterator.hasNext()) {
                String versionSha256 = (String) keyIterator.next();
                JSONObject object = versionsJson.getJSONObject(versionSha256);
                versions.add(new MinecraftVersion(
                        versionSha256,
                        object.getString("name"),
                        object.getString("md5"),
                        (object.has("baseURLHasNoPort") && object.getBoolean("baseURLHasNoPort"))
                ));
            }
        } catch (IOException ex) {
            System.err.println("Failed to load version information!");
            versions = new LinkedList<>();
        }
    }
}
