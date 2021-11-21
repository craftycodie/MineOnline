package gg.codie.mineonline;

import gg.codie.common.utils.OSUtils;

import java.io.File;
import java.net.URL;

public class LauncherFiles {
    public static final String OLD_MINECRAFT_FOLDER = getOldMinecraftDirectory().getPath();
    public static final String NEW_MINECRAFT_FOLDER = getNewMinecraftDirectory().getPath();
    public static final String MINEONLINE_FOLDER = getMineOnlineDirectory().getPath() + File.separator;

    public static final String MINEONLINE_SETTINGS_FILE = MINEONLINE_FOLDER + "settings.json";
    public static final String MINEONLINE_SERVERS_FILE = MINEONLINE_FOLDER + "servers.json";

    public static final String MINEONLINE_LIBRARY_FOLDER = MINEONLINE_FOLDER + "libraries" + File.separator;

    public static final String LWJGL_JAR = MINEONLINE_LIBRARY_FOLDER + "org" + File.separator + "lwjgl" + File.separator + (OSUtils.isM1JVM() ? "lwjgl-mac-m1" : (OSUtils.isMac() ? "lwjgl-mac" : "lwjgl")) + File.separator + "2.9.3" + File.separator + (OSUtils.isM1JVM() ? "lwjgl-mac-m1-2.9.3.jar" : (OSUtils.isMac() ? "lwjgl-mac-2.9.3.jar" : "lwjgl-2.9.3.jar"));
    public static final String LWJGL_UTIL_JAR = MINEONLINE_LIBRARY_FOLDER + "org" + File.separator + "lwjgl" + File.separator + (OSUtils.isM1JVM() ? "lwjgl_util-mac-m1" : (OSUtils.isMac() ? "lwjgl_util-mac" : "lwjgl_util")) + File.separator + "2.9.3" + File.separator + (OSUtils.isM1JVM() ? "lwjgl_util-mac-m1-2.9.3.jar" : (OSUtils.isMac() ? "lwjgl_util-mac-2.9.3.jar" : "lwjgl_util-2.9.3.jar"));
    public static final String JINPUT_JAR = MINEONLINE_LIBRARY_FOLDER + "org" + File.separator + "lwjgl" + File.separator + "jinput" + File.separator + "2.9.3" + File.separator + "jinput-2.9.3.jar";
    public static final String PATCH_AGENT_JAR = MINEONLINE_LIBRARY_FOLDER + "net" + File.separator + "bytebuddy" + File.separator + "byte-buddy-agent" + File.separator + "1.10.14" + File.separator + "byte-buddy-agent-1.10.14.jar";
    public static final String JSON_JAR = MINEONLINE_LIBRARY_FOLDER + "org" + File.separator + "json" + File.separator + "json" + File.separator + "20200518" + File.separator +  "json-20200518.jar";
    public static final String BYTEBUDDY_JAR = MINEONLINE_LIBRARY_FOLDER + "net" + File.separator + "bytebuddy" + File.separator + "byte-buddy-dep" + File.separator + "1.10.14" + File.separator +  "byte-buddy-dep-1.10.14.jar";
    public static final String ASM_JAR = MINEONLINE_LIBRARY_FOLDER + "org" + File.separator + "ow2" + File.separator + "asm" + File.separator + "8.0.1" + File.separator +  "asm-8.0.1.jar";
    public static final String ASM_COMMONS_JAR = MINEONLINE_LIBRARY_FOLDER + "org" + File.separator + "ow2" + File.separator + "asm-commons" + File.separator + "8.0.1" + File.separator +  "asm-commons-8.0.1.jar";
    public static final String DISCORD_RPC_JAR = MINEONLINE_LIBRARY_FOLDER + "com" + File.separator + "github" + File.separator + "Vatuu" + File.separator + "discord-rpc" + File.separator + "1.6.2" + File.separator + "discord-rpc-1.6.2.jar";

    public static final String MINEONLINE_NATIVES_FOLDER = MINEONLINE_FOLDER + "natives" + File.separator + OSUtils.getPlatform().toString();
    public static final String MINEONLINE_CACHE_FOLDER = MINEONLINE_FOLDER + "cache" + File.separator;
    public static final String MINEONLINE_TEMP_FOLDER = MINEONLINE_FOLDER + "temp" + File.separator;
    public static final String MINEONLINE_RUNTIME_NATIVES_FOLDER = MINEONLINE_TEMP_FOLDER + "natives" + File.separator;
    public static final String LAST_LOGIN_PATH = MINEONLINE_CACHE_FOLDER + "lastlogin";
    public static final String MINEONLINE_CUSTOM_VERSION_INFO_FOLDER = MINEONLINE_FOLDER + "custom-version-info" + File.separator;
    public static final String MINEONLINE_VERSION_INFO_FOLDER = MINEONLINE_FOLDER + "version-info" + File.separator;
    public static final String MINEONLINE_VERSIONS_FOLDER = MINEONLINE_FOLDER + "versions" + File.separator;
    public static final String MINEONLINE_WORLDS_FOLDER = MINEONLINE_FOLDER + "worlds" + File.separator;
    public static final String MINEONLINE_LATEST_LOG = MINEONLINE_FOLDER + "latest.log";
    public static final String MINEONLINE_RESOURCES_PATH = MINEONLINE_FOLDER + "resources" + File.separator;
    public static final String MINEONLINE_OPTIONS_PATH = MINEONLINE_FOLDER + "options.txt";

    public static final String MINECRAFT_RESOURCES_PATH = OLD_MINECRAFT_FOLDER + File.separator + "resources" + File.separator; // used with reflection
    public static final String MINECRAFT_TEXTURE_PACKS_PATH = OLD_MINECRAFT_FOLDER + File.separator + "texturepacks" + File.separator;
    public static final String MINECRAFT_ASSETS_PATH = NEW_MINECRAFT_FOLDER + File.separator + "assets" + File.separator;
    public static final String MINECRAFT_SCREENSHOTS_PATH = OLD_MINECRAFT_FOLDER + File.separator + "screenshots" + File.separator;
    public static final String MINECRAFT_VERSIONS_PATH = NEW_MINECRAFT_FOLDER + File.separator + "versions" + File.separator;
    public static final String MINECRAFT_LIBRARIES_PATH = NEW_MINECRAFT_FOLDER + File.separator + "libraries" + File.separator;

    public static final URL MISSING_TEXTURE = LauncherFiles.class.getResource("/img/missing.png");

    private static File getMineOnlineDirectory() {
        File workingDirectory;
        String applicationData, userHome = System.getProperty("user.home", ".");

        switch (OSUtils.getPlatform()) {
            case solaris:
                workingDirectory = new File(userHome, ".mineonline/");
                break;
            case windows:
                applicationData = System.getenv("APPDATA");
                if (applicationData != null) {
                    workingDirectory = new File(applicationData, "." + "mineonline/");
                    break;
                }
                workingDirectory = new File(userHome, ".mineonline/");
                break;
            case macosxm1:
            case macosx:
                workingDirectory = new File(userHome, "Library/Application Support/mineonline/");
                break;
            default:
                workingDirectory = new File(userHome, "mineonline/"); break;
        }
        if (!workingDirectory.exists() && !workingDirectory.mkdirs())
            throw new RuntimeException("The working directory could not be created: " + workingDirectory);

        return workingDirectory;
    }

    private static File getOldMinecraftDirectory() {
        File workingDirectory;
        String applicationData, userHome = System.getProperty("user.home", ".");

        switch (OSUtils.getPlatform()) {
            case solaris:
                workingDirectory = new File(userHome, ".minecraft/");
                break;
            case windows:
                applicationData = System.getenv("APPDATA");
                if (applicationData != null) {
                    workingDirectory = new File(applicationData, ".minecraft/");
                    break;
                }
                workingDirectory = new File(userHome, ".minecraft/");
                break;
            case macosxm1:
            case macosx:
                workingDirectory = new File(userHome, "Library/Application Support/minecraft/");
                break;
            default:
                workingDirectory = new File(userHome, "minecraft/"); break;
        }
        if (!workingDirectory.exists() && !workingDirectory.mkdirs())
            throw new RuntimeException("The working directory could not be created: " + workingDirectory);

        return workingDirectory;
    }

    public static File getNewMinecraftDirectory() {
        File workingDirectory;
        String userHome = System.getProperty("user.home", ".");

        switch (OSUtils.getPlatform()) {
            case solaris:
            case windows:
            case macosx:
            case macosxm1:
                workingDirectory = getOldMinecraftDirectory();
                break;
            default:
                workingDirectory = new File(userHome, ".minecraft/"); break;
        }
        if (!workingDirectory.exists() && !workingDirectory.mkdirs())
            throw new RuntimeException("The working directory could not be created: " + workingDirectory);

        return workingDirectory;
    }

    static {
        new File(MINEONLINE_CACHE_FOLDER).mkdirs();
        new File(MINEONLINE_WORLDS_FOLDER).mkdirs();
    }

}
