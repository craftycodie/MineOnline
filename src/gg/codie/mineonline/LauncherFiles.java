package gg.codie.mineonline;

import gg.codie.utils.OSUtils;

import java.io.File;
import java.net.URL;

public class LauncherFiles {

    public static final String MINEONLINE_FOLDER = getOldMinecraftDirectory() + File.separator + "mineonline" + File.separator;


    public static final String MINEONLINE_PROPS_FILE = MINEONLINE_FOLDER + "settings.json";

    public static final String MINEONLINE_LIBRARY_FOLDER = MINEONLINE_FOLDER + "lib" + File.separator;
  
    public static final String LWJGL_JAR = MINEONLINE_LIBRARY_FOLDER + "lwjgl.jar";
    public static final String LWJGL_UTIL_JAR = MINEONLINE_LIBRARY_FOLDER + "lwjgl_util.jar";
    public static final String PATCH_AGENT_JAR = MINEONLINE_LIBRARY_FOLDER + "byte-buddy-agent.jar";
    public static final String JSON_JAR = MINEONLINE_LIBRARY_FOLDER + "json.jar";
    public static final String BYTEBUDDY_JAR = MINEONLINE_LIBRARY_FOLDER + "byte-buddy-1.10.14.jar";
    public static final String ASM_JAR = MINEONLINE_LIBRARY_FOLDER + "asm-8.0.1.jar";
    public static final String ASM_COMMONS_JAR = MINEONLINE_LIBRARY_FOLDER + "asm-commons-8.0.1.jar";

    public static final String MINEONLNE_NATIVES_FOLDER = MINEONLINE_LIBRARY_FOLDER + "native" + File.separator + OSUtils.getPlatform().toString();

    public static final String MINEONLINE_CACHE_FOLDER = MINEONLINE_FOLDER + "cache" + File.separator;

    public static final String MINEONLINE_TEMP_FOLDER = MINEONLINE_FOLDER + "temp" + File.separator;
    public static final String MINEONLINE_RUNTIME_NATIVES_FOLDER = MINEONLINE_TEMP_FOLDER + "natives" + File.separator;

    public static final String CACHED_SKIN_PATH = MINEONLINE_CACHE_FOLDER + "skin.png";
    public static final String CACHED_SKIN_METADATA_PATH = MINEONLINE_CACHE_FOLDER + "skin.json";
    public static final String CACHED_CLOAK_PATH = MINEONLINE_CACHE_FOLDER + "cloak.png";

    public static final String LAST_LOGIN_PATH = MINEONLINE_CACHE_FOLDER + "lastlogin";
    public static final String MINEONLINE_CUSTOM_VERSIONS_FOLDER = MINEONLINE_FOLDER + "custom-versions" + File.separator;
    public static final String MINEONLINE_VERSIONS_FOLDER = MINEONLINE_FOLDER + "versions" + File.separator;

    public static final String MINECRAFT_RESOURCES_PATH = getOldMinecraftDirectory() + File.separator + "resources" + File.separator;
    public static final String MINECRAFT_ASSETS_PATH = getNewMinecraftDirectory() + File.separator + "assets" + File.separator;
    public static final String MINECRAFT_SCREENSHOTS_PATH = getOldMinecraftDirectory() + File.separator + "screenshots" + File.separator;
    public static final String MINECRAFT_OPTIONS_PATH = getOldMinecraftDirectory() + File.separator + "options.txt";
    public static final String MINECRAFT_VERSIONS_PATH = getNewMinecraftDirectory() + File.separator + "versions" + File.separator;
    public static final String MINECRAFT_LIBRARIES_PATH = getNewMinecraftDirectory() + File.separator + "libraries" + File.separator;

    public static final URL TEMPLATE_SKIN_PATH = LauncherFiles.class.getResource("/img/skin.png");
    public static final URL TEMPLATE_CLOAK_PATH = LauncherFiles.class.getResource("/img/cloak.png");
    public static final URL MISSING_TEXTURE = LauncherFiles.class.getResource("/img/missing.png");

    public static File getOldMinecraftDirectory() {
        File workingDirectory;
        String applicationData, userHome = System.getProperty("user.home", ".");

        switch (OSUtils.getPlatform()) {
            case solaris:
                workingDirectory = new File(userHome, String.valueOf('.') + "minecraft/");
                break;
            case windows:
                applicationData = System.getenv("APPDATA");
                if (applicationData != null) {
                    workingDirectory = new File(applicationData, "." + "minecraft/");
                    break;
                }
                workingDirectory = new File(userHome, String.valueOf('.') + "minecraft/");
            break;
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
    }

}
