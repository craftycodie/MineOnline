package gg.codie.mineonline;

import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.utils.ArrayUtils;
import gg.codie.utils.JSONUtils;
import gg.codie.utils.MD5Checksum;
import gg.codie.utils.OSUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class MinecraftVersionInfo {
    private static JSONArray versionsJson;
    private static MinecraftVersion[] versions = new MinecraftVersion[0];
    private static MinecraftVersion[] customVersions = new MinecraftVersion[0];


    static {
        loadVersions();
    }

    public static MinecraftVersion getVersionByMD5(String md5) {
        return getVersionByMD5(md5, ArrayUtils.concatenate(versions, customVersions));
    }

    private static MinecraftVersion getVersionByMD5(String md5, MinecraftVersion[] versions) {
        List<MinecraftVersion> matches = Arrays.stream(versions).filter(version -> version.md5.equals(md5)).collect(Collectors.toList());
        if(matches.size() < 1) {
            return null;
        }
        return matches.get(0);
    }

    private static MinecraftVersion readVersionFile(File versionFile) throws IOException {
        return fromJSONObject(new JSONObject(String.join("\n", Files.readAllLines(Paths.get(versionFile.getPath())))));
    }

    private static MinecraftVersion[] getVersions(String versionsFolder) {
        LinkedList versions = new LinkedList();

        File[] directories = new File(versionsFolder).listFiles(file -> file.isDirectory());

        if (directories == null)
            return new MinecraftVersion[0];

        for (File directory : directories) {
            File[] versionFiles = directory.listFiles();
            if(versions == null)
                continue;

            for (File versionFile : versionFiles) {
                if(versionFile.getName().length() < 37 || versionFile.isDirectory())
                    continue;

                try {
                    MinecraftVersion version = readVersionFile(versionFile);
                    versions.add(version);
                } catch (IOException ex) {
                    System.out.println("Bad version file " + versionFile.getPath());
                }
            }
        }

        return (MinecraftVersion[])versions.toArray(new MinecraftVersion[0]);
    }

    private static MinecraftVersion[] getResourceVersions() {
        LinkedList versions = new LinkedList();

        try {
            File jarFile = new File(LibraryManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            if(!jarFile.exists() || jarFile.isDirectory())
                return new MinecraftVersion[0];

            java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile.getPath());
            java.util.Enumeration enumEntries = jar.entries();

            while (enumEntries.hasMoreElements()) {
                java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
                if (!file.getName().startsWith("versions")) {
                    continue;
                }

                if (versions == null)
                    continue;

                if (file.getName().length() < 37 || file.isDirectory())
                    continue;

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(file)));
                    StringBuffer sb = new StringBuffer();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        sb.append(str);
                    }

                    MinecraftVersion version = fromJSONObject(new JSONObject(str));
                    versions.add(version);
                } catch (IOException ex) {
                    System.out.println("Bad version file " + file.getName());
                }

            }
        } catch (Exception ioe) {
            System.out.println("Failed to load resource versions.");
            return new MinecraftVersion[0];
        }

        return (MinecraftVersion[])versions.toArray(new MinecraftVersion[0]);
    }

    public static class MinecraftVersion {
        public final String sha256;
        public final String name;
        public final String md5;
        public final String type;
        public final boolean baseURLHasNoPort;
        public final boolean enableScreenshotPatch;
        public final String baseVersion;
        public final boolean hasHeartbeat;
        public final boolean enableFullscreenPatch;
        public final String info;
        public final String[] clientVersions;
        public final boolean forceFullscreenMacos;
        public final boolean enableMacosCursorPatch;
        public final boolean legacy;
        public final String assetIndex;
        public final String[] libraries;
        public final String[] natives;
        public final boolean useMinecraftImpl;
        public final String minecraftImplClass;

        private MinecraftVersion(
                String sha256,
                String name,
                String baseVersion,
                String md5,
                String type,
                boolean baseURLHasNoPort,
                boolean enableScreenshotPatch,
                boolean hasHeartbeat,
                boolean enableFullscreenPatch,
                String info,
                String[] clientVersions,
                boolean forceFullscreenMacos,
                boolean enableMacosCursorPatch,
                boolean legacy,
                String assetIndex,
                String[] libraries,
                String[] nativesWindows,
                boolean useMinecraftImpl,
                String minecraftImplClass
        ) {
            this.sha256 = sha256;
            this.name = name;
            this.md5 = md5;
            this.type = type;
            this.baseURLHasNoPort = baseURLHasNoPort;
            this.enableScreenshotPatch = enableScreenshotPatch;
            this.hasHeartbeat = hasHeartbeat;
            this.baseVersion = baseVersion;
            this.enableFullscreenPatch = enableFullscreenPatch;
            this.info = info;
            this.forceFullscreenMacos = forceFullscreenMacos;
            this.clientVersions = clientVersions;
            this.enableMacosCursorPatch = enableMacosCursorPatch;
            this.useMinecraftImpl = useMinecraftImpl;
            this.minecraftImplClass = minecraftImplClass;
            this.legacy = legacy;
            this.assetIndex = assetIndex;
            this.libraries = libraries;
            this.natives = nativesWindows;
        }
    }

    private static void fetchVersions() {
        MinecraftVersion[] cachedVersions = getVersions(LauncherFiles.MINEONLINE_VERSIONS_FOLDER);
        try {
            JSONObject index = MineOnlineAPI.getVersionIndex();
            JSONArray versionsPaths = index.getJSONArray("versions");
            for(Object versionPathObject : versionsPaths) {
                try {
                    String filename = ((JSONObject) versionPathObject).getString("name");
                    String jarMd5 = filename.substring(filename.length() - 37, filename.length() - 5);

                    boolean alreadyDownloaded = false;

                    MinecraftVersion existingVersion = getVersionByMD5(jarMd5, cachedVersions);
                    for (MinecraftVersion cachedVersion : cachedVersions) {
                        if (cachedVersion != null && cachedVersion.md5.equals(jarMd5)) {
                            String infoMd5 = ((JSONObject) versionPathObject).getString("md5").toUpperCase();
                            File cachedInfo = new File(LauncherFiles.MINEONLINE_VERSIONS_FOLDER + existingVersion.type + File.separator + existingVersion.name + " " + existingVersion.md5 + ".json");

                            if (cachedInfo.exists()) {
                                String cachedInfoMd5 = MD5Checksum.getMD5ChecksumForString(String.join("\n", Files.readAllLines(Paths.get(cachedInfo.getPath()))));

                                if (!infoMd5.equals(cachedInfoMd5)) {
                                    System.out.println("Download " + infoMd5);
                                    System.out.println("Cached " + cachedInfoMd5);
                                    cachedInfo.delete();
                                } else {
                                    alreadyDownloaded = true;
                                }
                            }
                        }

                    }

                    if (alreadyDownloaded)
                        continue;


                    System.out.println("Downloaded new version info " + ((JSONObject) versionPathObject).getString("name"));

                    String downloadVersionText = MineOnlineAPI.getVersionInfo(((JSONObject) versionPathObject).getString("url"));
                    MinecraftVersion downloadVersion = fromJSONObject(new JSONObject(downloadVersionText));

                    Path target = Paths.get(LauncherFiles.MINECRAFT_VERSIONS_PATH + downloadVersion.type + File.separator + downloadVersion.name + " " + downloadVersion.md5 + ".json");
                    new File(target.toUri()).getParentFile().mkdirs();
                    new File(target.toUri()).createNewFile();
                    Files.write(target, downloadVersionText.getBytes(), StandardOpenOption.WRITE);

                } catch (Exception ex) {
                    System.out.println("Bad version " + versionPathObject);
                    ex.printStackTrace();
                }
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void loadVersions() {
        // If there's a resource version that's not in the cache, extract it.
        MinecraftVersion[] cachedVersions = getVersions(LauncherFiles.MINEONLINE_VERSIONS_FOLDER);
        try {
            for (MinecraftVersion version : getResourceVersions()) {
                if(getVersionByMD5(version.md5, cachedVersions) == null) {
                    try {
                        File resource = new File(Paths.get(LauncherFiles.VERSIONS_RESOURCE.toURI()).toString() + File.separator + version.type + File.separator + version.name + " " + version.md5 + ".json");
                        File target = new File(LauncherFiles.MINEONLINE_VERSIONS_FOLDER + version.type + File.separator + version.name + " " + version.md5 + ".json");
                        target.getParentFile().mkdirs();
                        Files.copy(Paths.get(resource.toURI()), Paths.get(target.toURI()));
                    } catch (IOException ex) {
                        System.out.println("Failed to extract version " + version.md5);
                        ex.printStackTrace();
                    }
                }
            }
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        // Fetch latest versions from the API
        fetchVersions();
        // Load cached versions
        versions = getVersions(LauncherFiles.MINEONLINE_VERSIONS_FOLDER);
        // Load custom versions
        customVersions = getVersions(LauncherFiles.MINEONLINE_CUSTOM_VERSIONS_FOLDER);
    }

    public static String getAppletClass(String path) throws IOException {
        JarFile jarFile = new JarFile(path);
        Enumeration allEntries = jarFile.entries();
        while (allEntries.hasMoreElements()) {
            JarEntry entry = (JarEntry) allEntries.nextElement();
            String classCanonicalName = entry.getName();

            if(!classCanonicalName.contains(".class"))
                continue;

            classCanonicalName = classCanonicalName.replace("/", ".");
            classCanonicalName = classCanonicalName.replace(".class", "");

            String className = classCanonicalName;
            if(classCanonicalName.lastIndexOf(".") > -1) {
                className = classCanonicalName.substring(classCanonicalName.lastIndexOf(".") + 1);
            }

            if(className.equals("MinecraftApplet")) {
                return classCanonicalName;
            }
        }
        return null;
    }

    public static boolean isLegacyJar(String path) throws IOException {
        JarFile jarFile = new JarFile(path);
        Enumeration allEntries = jarFile.entries();
        while (allEntries.hasMoreElements()) {
            JarEntry entry = (JarEntry) allEntries.nextElement();
            String classCanonicalName = entry.getName();

            if(!classCanonicalName.contains(".class"))
                continue;

            classCanonicalName = classCanonicalName.replace("/", ".");
            classCanonicalName = classCanonicalName.replace(".class", "");

            String className = classCanonicalName;
            if(classCanonicalName.lastIndexOf(".") > -1) {
                className = classCanonicalName.substring(classCanonicalName.lastIndexOf(".") + 1);
            }

            if (className.equals("MinecraftApplet")) {
                return true;
            } else if (className.equals("Minecraft")) {
                return true;
            }
            else if (className.equals("LauncherFrame")) {
                return true;
            }
            else if (className.equals("RubyDung")) {
                return true;
            }
        }
        
        try {
            System.out.println("Incompatible Jar MD5: " + MD5Checksum.getMD5ChecksumForFile(path));
        } catch (Exception ex) {

        }

        return false;
    }

    public static boolean isPlayableJar(String path) throws IOException {
        if(isLegacyJar(path))
            return true;

        JarFile jarFile = new JarFile(path);
        Enumeration allEntries = jarFile.entries();
        while (allEntries.hasMoreElements()) {
            JarEntry entry = (JarEntry) allEntries.nextElement();
            String classCanonicalName = entry.getName();

            if(!classCanonicalName.contains(".class"))
                continue;

            classCanonicalName = classCanonicalName.replace("/", ".");
            classCanonicalName = classCanonicalName.replace(".class", "");

            String className = classCanonicalName;
            if(classCanonicalName.lastIndexOf(".") > -1) {
                className = classCanonicalName.substring(classCanonicalName.lastIndexOf(".") + 1);
            }

//            else if (className.equals("MinecraftLauncher")) {
//                return true;
//            }
            if (className.equals("Main")) {
                return true;
            }
        }

        try {
            System.out.println("Incompatible Jar MD5: " + MD5Checksum.getMD5ChecksumForFile(path));
        } catch (Exception ex) {

        }

        return false;
    }

    private static MinecraftVersion fromJSONObject(JSONObject object) {
        return new MinecraftVersion(
                (object.has("sha256") ? object.getString("sha256") : null),
                object.getString("name"),
                object.getString("baseVersion"),
                object.getString("md5"),
                object.getString("type"),
                (object.has("baseURLHasNoPort") && object.getBoolean("baseURLHasNoPort")),
                (object.has("enableScreenshotPatch") && object.getBoolean("enableScreenshotPatch")),
                (object.has("hasHeartbeat") && object.getBoolean("hasHeartbeat")),
                (object.has("enableFullscreenPatch") && object.getBoolean("enableFullscreenPatch")),
                (object.has("info") ? object.getString("info") : null),
                (object.has("clientVersions") ? JSONUtils.getStringArray(object.getJSONArray("clientVersions")) : new String[] { object.getString("baseVersion")}),
                (object.has("forceFullscreenMacos") && object.getBoolean("forceFullscreenMacos")),
                (object.has("enableMacosCursorPatch") && object.getBoolean("enableMacosCursorPatch")),
                (object.has("legacy") && object.getBoolean("legacy")),
                (object.has("assetIndex") ? object.getString("assetIndex") : object.has("baseVersion") ? object.getString("baseVersion") : null),
                (object.has("libraries") ? JSONUtils.getStringArray(object.getJSONArray("libraries")) : new String[0]),
                (object.has("natives") && object.getJSONObject("natives").has(OSUtils.getPlatform().name()) ? JSONUtils.getStringArray(object.getJSONObject("natives").getJSONArray(OSUtils.getPlatform().name())) : new String[0]),
                (object.has("useMinecraftImpl") && object.getBoolean("useMinecraftImpl")),
                (object.has("minecraftImplClass") ? object.getString("minecraftImplClass") : null)
        );
    }

    public static MinecraftVersion getVersion(String path) {
        try {
            String md5 = MD5Checksum.getMD5ChecksumForFile(path);
            return getVersionByMD5(md5);
        } catch (Exception e) {
            return null;
        }
    }

    public static void launchMinecraft(String jarPath, String serverIP, String serverPort, String mpPass) throws Exception {
        MinecraftVersion minecraftVersion = getVersion(jarPath);

        if(minecraftVersion != null) {
            if (minecraftVersion.legacy) {
                new LegacyMinecraftClientLauncher(jarPath, serverIP, serverPort, mpPass).startMinecraft();
            } else {
                MinecraftClientLauncher.startProcess(jarPath, serverIP, serverPort);
            }
        } else {
            if (isLegacyJar(jarPath)) {
                new LegacyMinecraftClientLauncher(jarPath, serverIP, serverPort, mpPass).startMinecraft();
            } else {
                LibraryManager.addJarToClasspath(Paths.get(jarPath).toUri().toURL());
                Class clazz = Class.forName("net.minecraft.client.main.Main");
                if (clazz != null) {
                    MinecraftClientLauncher.startProcess(jarPath, serverIP, serverPort);
                } else {
                    System.out.println("This jar file seems unsupported :(");
                }
            }
        }
    }
}
