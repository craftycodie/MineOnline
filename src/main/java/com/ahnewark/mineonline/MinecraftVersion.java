package com.ahnewark.mineonline;

import com.ahnewark.common.utils.JSONUtils;
import com.ahnewark.common.utils.MD5Checksum;
import com.ahnewark.common.utils.OSUtils;
import com.ahnewark.minecraft.client.options.EMinecraftOptionsVersion;
import com.ahnewark.mineonline.client.LegacyGameManager;
import com.ahnewark.mineonline.client.LegacyMinecraftClientLauncher;
import com.ahnewark.mineonline.client.MinecraftClientLauncher;
import com.ahnewark.mineonline.client.RubyDungLauncher;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
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
    public final boolean enableFullscreenPatch;
    public final String info;
    public final boolean enableCursorPatch;
    public final boolean legacy;
    public final String assetIndex;
    public final String[] libraries;
    public final String[] natives;
    public final String guiClass;
    public final String guiScreenClass;
    public final String scaledResolutionClass;
    public final String entityRendererClass;
    public final String viewModelFunction;
    public final String hurtEffectFunction;
    public final int hurtEffectCallsPerFrame;
    public final boolean useFOVPatch;
    public final boolean useTexturepackPatch;
    public final String waterColorizerClass;
    public final String grassColorizerClass;
    public final String foliageColorizerClass;
    public final String ingameVersionString;
    public final String resourcesVersion;
    public final boolean useUsernamesPatch;
    public final boolean useGreyScreenPatch;
    public final EMinecraftOptionsVersion optionsVersion;
    public final boolean useResizePatch;
    public final boolean hasNetherPortalTexture;
    public final URL downloadURL;
    public final boolean useMineOnlineMenu;
    public final String fontClass;
    public final String colorCodePrefix;
    public final boolean usePlayerList;
    public final boolean useCustomAnimations;
    public final boolean useIndevSoundPatch;
    public final String itemRendererClass;
    public final String clockFXClass;
    public final String compassFXClass;
    public final boolean cantVerifyName;

    public MinecraftVersion(
            String sha256,
            String name,
            String baseVersion,
            String md5,
            String type,
            boolean baseURLHasNoPort,
            boolean enableScreenshotPatch,
            boolean enableFullscreenPatch,
            String info,
            boolean enableCursorPatch,
            boolean legacy,
            String assetIndex,
            String[] libraries,
            String[] nativesWindows,
            String guiClass,
            String guiScreenClass,
            String scaledResolutionClass,
            String entityRendererClass,
            String viewModelFunction,
            boolean useFOVPatch,
            boolean useTexturepackPatch,
            String ingameVersionString,
            String resourcesVersion,
            boolean useUsernamesPatch,
            boolean useGreyScreenPatch,
            EMinecraftOptionsVersion optionsVersion,
            boolean useResizePatch,
            boolean hasNetherPortalTexture,
            URL downloadURL,
            boolean useMineOnlineMenu,
            String hurtEffectFunction,
            int hurtEffectCallsPerFrame,
            String waterColorizerClass,
            String grassColorizerClass,
            String foliageColorizerClass,
            String fontClass,
            String colorCodePrefix,
            boolean usePlayerList,
            boolean useCustomAnimations,
            boolean useIndevSoundPatch,
            String itemRendererClass,
            String compassFXClass,
            String clockFXClass,
            boolean cantVerifyName
    ) {
        this.sha256 = sha256;
        this.name = name;
        this.md5 = md5;
        this.type = type;
        this.baseURLHasNoPort = baseURLHasNoPort;
        this.enableScreenshotPatch = enableScreenshotPatch;
        this.baseVersion = baseVersion;
        this.enableFullscreenPatch = enableFullscreenPatch;
        this.info = info;
        this.enableCursorPatch = enableCursorPatch;
        this.legacy = legacy;
        this.assetIndex = assetIndex;
        this.libraries = libraries;
        this.natives = nativesWindows;
        this.guiClass = guiClass;
        this.guiScreenClass = guiScreenClass;
        this.scaledResolutionClass = scaledResolutionClass;
        this.entityRendererClass = entityRendererClass;
        this.viewModelFunction = viewModelFunction;
        this.useFOVPatch = useFOVPatch;
        this.useTexturepackPatch = useTexturepackPatch;
        this.ingameVersionString = ingameVersionString;
        this.resourcesVersion = resourcesVersion;
        this.useUsernamesPatch = useUsernamesPatch;
        this.useGreyScreenPatch = useGreyScreenPatch;
        this.optionsVersion = optionsVersion;
        this.useResizePatch = useResizePatch;
        this.hasNetherPortalTexture = hasNetherPortalTexture;
        this.downloadURL = downloadURL;
        this.useMineOnlineMenu = useMineOnlineMenu;
        this.hurtEffectFunction = hurtEffectFunction;
        this.hurtEffectCallsPerFrame = hurtEffectCallsPerFrame;
        this.waterColorizerClass = waterColorizerClass;
        this.grassColorizerClass = grassColorizerClass;
        this.foliageColorizerClass = foliageColorizerClass;
        this.fontClass = fontClass;
        this.colorCodePrefix = colorCodePrefix;
        this.usePlayerList = usePlayerList;
        this.useCustomAnimations = useCustomAnimations;
        this.useIndevSoundPatch = useIndevSoundPatch;
        this.itemRendererClass = itemRendererClass;
        this.compassFXClass = compassFXClass;
        this.clockFXClass = clockFXClass;
        this.cantVerifyName = cantVerifyName;
    }

    public MinecraftVersion(JSONObject object) {
        sha256 = object.optString("sha256", null);
        name = object.getString("name");
        baseVersion = object.getString("baseVersion");
        md5 = object.getString("md5");
        type = object.getString("type");
        baseURLHasNoPort = object.optBoolean("baseURLHasNoPort", false);
        enableScreenshotPatch = object.optBoolean("enableScreenshotPatch", false);
        enableFullscreenPatch = object.optBoolean("enableFullscreenPatch", false);
        info = (object.has("info") ? object.getString("info") : null);
        enableCursorPatch = object.optBoolean("enableCursorPatch", false);
        legacy = object.optBoolean("legacy", false);
        assetIndex = (object.has("assetIndex") ? object.getString("assetIndex") : object.has("baseVersion") ? object.getString("baseVersion") : null);
        libraries = (object.has("libraries") ? JSONUtils.getStringArray(object.getJSONArray("libraries")) : new String[0]);
        natives = (object.has("natives") && object.getJSONObject("natives").has(OSUtils.getPlatform().name()) ? JSONUtils.getStringArray(object.getJSONObject("natives").getJSONArray(OSUtils.getPlatform().name())) : new String[0]);
        guiClass = object.optString("guiClass", null);
        guiScreenClass = object.optString("guiScreenClass", null);
        scaledResolutionClass = object.optString("scaledResolutionClass", null);
        entityRendererClass = object.optString("entityRendererClass", null);
        viewModelFunction = object.optString("viewModelFunction", null);
        useFOVPatch = object.optBoolean("useFOVPatch", false);
        useTexturepackPatch = object.optBoolean("useTexturepackPatch", false);
        ingameVersionString = object.optString("ingameVersionString", null);
        resourcesVersion = object.optString("resourcesVersion", "default");
        useUsernamesPatch = object.optBoolean("useUsernamesPatch", false);
        useGreyScreenPatch = object.optBoolean("useGreyScreenPatch", false);
        optionsVersion = object.optEnum(EMinecraftOptionsVersion.class, "optionsVersion", EMinecraftOptionsVersion.DEFAULT);
        useResizePatch = object.optBoolean("useResizePatch", false);
        hasNetherPortalTexture = object.optBoolean("hasNetherPortalTexture", true);
        useMineOnlineMenu = object.optBoolean("useMineOnlineMenu", true);
        hurtEffectFunction = object.optString("hurtEffectFunction", null);
        hurtEffectCallsPerFrame = object.optInt("hurtEffectCallsPerFrame", 2);
        waterColorizerClass = object.optString("waterColorizerClass", null);
        grassColorizerClass = object.optString("grassColorizerClass", null);
        foliageColorizerClass = object.optString("foliageColorizerClass", null);
        fontClass = object.optString("fontClass", null);
        colorCodePrefix = object.optString("colorCodePrefix", null);
        usePlayerList = object.optBoolean("usePlayerList", false);
        useCustomAnimations = object.optBoolean("useCustomAnimations", true);
        useIndevSoundPatch = object.optBoolean("useIndevSoundPatch", false);
        itemRendererClass = object.optString("itemRendererClass", null);
        compassFXClass = object.optString("compassFXClass", null);
        clockFXClass = object.optString("clockFXClass", null);
        cantVerifyName = object.optBoolean("cantVerifyName", false);


        URL parsedURL = null;

        if (object.has("downloadURL")) {
            try {
                parsedURL = new URL(object.getString("downloadURL"));
            } catch (MalformedURLException ex) {
                System.out.println("Bad download URL for " + name + ", " + object.getString("downloadURL"));
            }
        }
        downloadURL = parsedURL;
    }


    public String download() throws IOException {
        HttpURLConnection httpConnection = (java.net.HttpURLConnection) (downloadURL.openConnection());
        System.out.println(downloadURL.toString());
        InputStream in = httpConnection.getInputStream();

        String path = LauncherFiles.MINEONLINE_VERSIONS_FOLDER + "clients" + File.separator + name + " " + md5 + File.separator + baseVersion + ".jar";

        File clientJar = new File(path);
        clientJar.getParentFile().mkdirs();
        OutputStream out = new java.io.FileOutputStream(path, false);

        final byte[] data = new byte[1024];
        int count;
        while ((count = in.read(data, 0, 1024)) != -1) {
            out.write(data, 0, count);
        }

        MinecraftVersionRepository.getSingleton().addInstalledVersion(path);

        return path;
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
            } else if (className.equals("RubyDung")) {
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

            boolean isLegacy = versionManifest.getString("type").startsWith("old_");

            LinkedList<String> libraries = new LinkedList<>();
            LinkedList<String> natives = new LinkedList<>();

            for(Object library : versionManifest.getJSONArray("libraries")) {
                JSONObject jsonLibrary = (JSONObject) library;
                if(jsonLibrary.getJSONObject("downloads").has("artifact"))
                    libraries.add(jsonLibrary.getJSONObject("downloads").getJSONObject("artifact").getString("path"));
                if(jsonLibrary.getJSONObject("downloads").has("classifiers"))
                    switch(OSUtils.getPlatform()) {
                        case macosx:
                        case macosxm1:
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
                    null,
                    false,
                    isLegacy,
                    versionManifest.getString("assets"),
                    libraries.toArray(new String[0]),
                    natives.toArray(new String[0]),
                    null,
                    null,
                    null,
                    null,
                    null,
                    false,
                    false,
                    null,
                    "default",
                    false,
                    false,
                    EMinecraftOptionsVersion.DEFAULT,
                    false,
                    true,
                    null,
                    false,
                    null,
                    2,
                    null,
                    null,
                    null,
                    null,
                    null,
                    false,
                    false,
                    false,
                    null,
                    null,
                    null,
                    false
            );
        } catch (Exception ex) {
            System.err.println("Bad launcher JSON for version " + jarFile);
            //ex.printStackTrace();
        }
        return null;
    }

    public static void launchMinecraft(String jarPath, String serverIP, String serverPort, String mpPass) throws Exception {
        MinecraftVersion minecraftVersion = MinecraftVersionRepository.getSingleton().getVersion(jarPath);
        MinecraftVersionRepository.getSingleton().selectJar(jarPath);

        if (serverIP != null) {
            InetAddress address = InetAddress.getByName(serverIP);
            serverIP = address.getHostAddress();

            Settings.singleton.setLastServer(serverIP + (serverPort != null ? ":" + serverPort : ""));
            Settings.singleton.saveSettings();
        }

        System.out.println("Launching jar " + (minecraftVersion != null ? minecraftVersion.name : jarPath) + " MD5 " + MD5Checksum.getMD5ChecksumForFile(jarPath));
        if (!LegacyGameManager.isInGame())
            Settings.singleton.saveMinecraftOptions(minecraftVersion != null ? minecraftVersion.optionsVersion : EMinecraftOptionsVersion.DEFAULT);


        if(minecraftVersion != null) {
            if (minecraftVersion.type.equals("rubydung")) {
                RubyDungLauncher.startProcess(jarPath);
                return;
            }

            if (minecraftVersion.legacy) {
                LegacyMinecraftClientLauncher.startProcess(jarPath, serverIP, serverPort, mpPass);
            } else {
                MinecraftClientLauncher.startProcess(jarPath, serverIP, serverPort, minecraftVersion);
            }
        } else {
            if (isLegacyJar(jarPath)) {
                LegacyMinecraftClientLauncher.startProcess(jarPath, serverIP, serverPort, mpPass);
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

