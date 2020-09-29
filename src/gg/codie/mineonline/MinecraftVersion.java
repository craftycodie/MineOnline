package gg.codie.mineonline;

import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.client.LegacyMinecraftClientLauncher;
import gg.codie.mineonline.client.MinecraftClientLauncher;
import gg.codie.mineonline.discord.DiscordPresence;
import gg.codie.utils.JSONUtils;
import gg.codie.utils.MD5Checksum;
import gg.codie.utils.OSUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MinecraftVersion {
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
    public final String clientName;

    public MinecraftVersion(
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
            String minecraftImplClass,
            String clientName
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
        this.clientName = clientName;
    }

    public MinecraftVersion(JSONObject object) {
            sha256 = (object.has("sha256") ? object.getString("sha256") : null);
            name = object.getString("name");
            baseVersion = object.getString("baseVersion");
            md5 = object.getString("md5");
            type = object.getString("type");
            baseURLHasNoPort = (object.has("baseURLHasNoPort") && object.getBoolean("baseURLHasNoPort"));
            enableScreenshotPatch = (object.has("enableScreenshotPatch") && object.getBoolean("enableScreenshotPatch"));
            hasHeartbeat = (object.has("hasHeartbeat") && object.getBoolean("hasHeartbeat"));
            enableFullscreenPatch = (object.has("enableFullscreenPatch") && object.getBoolean("enableFullscreenPatch"));
            info = (object.has("info") ? object.getString("info") : null);
            clientVersions = (object.has("clientVersions") ? JSONUtils.getStringArray(object.getJSONArray("clientVersions")) : new String[] { object.getString("baseVersion")});
            forceFullscreenMacos = (object.has("forceFullscreenMacos") && object.getBoolean("forceFullscreenMacos"));
            enableMacosCursorPatch = (object.has("enableMacosCursorPatch") && object.getBoolean("enableMacosCursorPatch"));
            legacy = (object.has("legacy") && object.getBoolean("legacy"));
            assetIndex = (object.has("assetIndex") ? object.getString("assetIndex") : object.has("baseVersion") ? object.getString("baseVersion") : null);
            libraries = (object.has("libraries") ? JSONUtils.getStringArray(object.getJSONArray("libraries")) : new String[0]);
            natives = (object.has("natives") && object.getJSONObject("natives").has(OSUtils.getPlatform().name()) ? JSONUtils.getStringArray(object.getJSONObject("natives").getJSONArray(OSUtils.getPlatform().name())) : new String[0]);
            useMinecraftImpl = (object.has("useMinecraftImpl") && object.getBoolean("useMinecraftImpl"));
            minecraftImplClass = (object.has("minecraftImplClass") ? object.getString("minecraftImplClass") : null);
            clientName = (object.has("clientName") ? object.getString("clientName") : object.getString("name"));
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

    public static MinecraftVersion fromLauncherVersion(File jarFile) throws Exception {
        File jsonFile = new File(jarFile.getParentFile().getPath() + File.separator + jarFile.getName().replace(".jar", ".json"));
        if (!jarFile.exists() || !jsonFile.exists()) {
            System.out.println("Could not find minecraft version " + jarFile.getName() + " md5: " + MD5Checksum.getMD5ChecksumForFile(jarFile.getPath()));
            return null;
        }

        try (FileInputStream input = new FileInputStream(jsonFile)) {
            // load a settings file
            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytes_read = input.read(buffer, 0, 8096)) != -1) {
                for (int i = 0; i < bytes_read; i++) {
                    stringBuffer.append((char) buffer[i]);
                }
            }

            JSONObject versionManifest = new JSONObject(stringBuffer.toString());

            String typeName = versionManifest.getString("type").replace("old_", "");
            typeName = typeName.substring(0, 1).toUpperCase() + typeName.substring(1);
            String versionNumber = versionManifest.getString("id");
            if (versionNumber.startsWith("a") || versionNumber.startsWith("b"))
                versionNumber = versionNumber.substring(1);
            else if (versionNumber.startsWith("c")) {
                typeName = "Classic ";
                versionNumber = versionNumber.substring(1);
            } else if (versionNumber.startsWith("rd-")) {
                typeName = "RubyDung ";
                versionNumber = versionNumber.substring(3);
            }

            LinkedList<String> libraries = new LinkedList<>();
            LinkedList<String> natives = new LinkedList<>();

            for(Object library : versionManifest.getJSONArray("libraries")) {
                JSONObject jsonLibrary = (JSONObject) library;
                if(jsonLibrary.getJSONObject("downloads").has("artifact"))
                    libraries.add(jsonLibrary.getJSONObject("downloads").getJSONObject("artifact").getString("path"));
                if(jsonLibrary.getJSONObject("downloads").has("classifiers"))
                    switch(OSUtils.getPlatform()) {
                        case macosx:
                            if (jsonLibrary.getJSONObject("downloads").getJSONObject("classifiers").has("natives-macos"))
                                natives.add(jsonLibrary.getJSONObject("downloads").getJSONObject("classifiers").getJSONObject("natives-macos").getString("path"));
                            if (jsonLibrary.getJSONObject("downloads").getJSONObject("classifiers").has("natives-osx"))
                                natives.add(jsonLibrary.getJSONObject("downloads").getJSONObject("classifiers").getJSONObject("natives-osx").getString("path"));
                            break;
                        case windows:
                            if (jsonLibrary.getJSONObject("downloads").getJSONObject("classifiers").has("natives-windows"))
                                natives.add(jsonLibrary.getJSONObject("downloads").getJSONObject("classifiers").getJSONObject("natives-windows").getString("path"));
                            break;
                        case linux:
                            if (jsonLibrary.getJSONObject("downloads").getJSONObject("classifiers").has("natives-linux"))
                                natives.add(jsonLibrary.getJSONObject("downloads").getJSONObject("classifiers").getJSONObject("natives-linux").getString("path"));
                            break;
                        case solaris:
                        case unknown:
                        default:
                            break;
                    }
            }

            return new MinecraftVersion(
                null,
                typeName + " " + versionNumber,
                versionManifest.getString("id"),
                MD5Checksum.getMD5ChecksumForFile(jarFile.getPath()),
                "client",
                false,
                false,
                false,
                false,
                null,
                new String[] { versionManifest.getString("id")},
                false,
                false,
                false,
                versionManifest.getString("assets"),
                libraries.toArray(new String[0]),
                natives.toArray(new String[0]),
                false,
                null,
                typeName + " " + versionNumber);
        } catch (Exception ex) {
            System.err.println("Bad launcher JSON for version " + jarFile);
            //ex.printStackTrace();
        }
        return null;
    }

    public static void launchMinecraft(String jarPath, String serverIP, String serverPort, String mpPass) throws Exception {
        MinecraftVersion minecraftVersion = MinecraftVersionRepository.getSingleton().getVersion(jarPath);

        if(minecraftVersion != null)
            DiscordPresence.play(minecraftVersion.name, serverIP, serverPort);
        else
            DiscordPresence.play(Paths.get(jarPath).getFileName().toString(), serverIP, serverPort);

        if (serverIP != null) {
            InetAddress address = InetAddress.getByName(serverIP);
            serverIP = address.getHostAddress();

            String externalIP = MineOnlineAPI.getExternalIP();

            if (serverIP != null && serverIP.equals(externalIP)) {
                serverIP = InetAddress.getLocalHost().getHostAddress();
            }
        }

        if(minecraftVersion != null) {
            if (minecraftVersion.legacy) {
                new LegacyMinecraftClientLauncher(jarPath, serverIP, serverPort, mpPass).startMinecraft();
            } else {
                MinecraftClientLauncher.startProcess(jarPath, serverIP, serverPort, minecraftVersion);
            }
        } else {
            if (isLegacyJar(jarPath)) {
                new LegacyMinecraftClientLauncher(jarPath, serverIP, serverPort, mpPass).startMinecraft();
            } else {
                URLClassLoader classLoader = new URLClassLoader(new URL[] { Paths.get(jarPath).toUri().toURL() });
                Class clazz = classLoader.loadClass("net.minecraft.client.main.Main");
                if (clazz != null) {
                    MinecraftClientLauncher.startProcess(jarPath, serverIP, serverPort, minecraftVersion);
                } else {
                    System.out.println("This jar file seems unsupported :(");
                }
            }
        }
    }
}

