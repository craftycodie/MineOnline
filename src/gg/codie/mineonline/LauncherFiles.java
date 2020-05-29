package gg.codie.mineonline;

import gg.codie.utils.OSUtils;

import java.io.File;
import java.net.URL;

public class LauncherFiles {

    public static final String MINEONLINE_FOLDER = getMinecraftDirectory() + File.separator + "mineonline" + File.separator;


    public static final String MINEONLINE_PROPS_FILE = MINEONLINE_FOLDER + "settings.json";

    public static final String MINEONLINE_LIBRARY_FOLDER = MINEONLINE_FOLDER + "lib" + File.separator;
  
    public static final String LWJGL_JAR = MINEONLINE_LIBRARY_FOLDER + "lwjgl-modded.jar";
    public static final String LWJGL_UTIL_JAR = MINEONLINE_LIBRARY_FOLDER + "lwjgl_util.jar";

    public static final String MINEONLNE_NATIVES_FOLDER = MINEONLINE_LIBRARY_FOLDER + "native" + File.separator + OSUtils.getPlatform().toString();

    public static final String MINEONLINE_CACHE_FOLDER = MINEONLINE_FOLDER + "cache" + File.separator;

    public static final String CACHED_SKIN_PATH = MINEONLINE_CACHE_FOLDER + "skin.png";
    public static final String CACHED_CLOAK_PATH = MINEONLINE_CACHE_FOLDER + "cloak.png";

    public static final String LAST_LOGIN_PATH = MINEONLINE_CACHE_FOLDER + "lastlogin";

    public static final String MINECRAFT_RESOURCES_PATH = getMinecraftDirectory() + File.separator + "resources" + File.separator;
    public static final String MINECRAFT_SCREENSHOTS_PATH = getMinecraftDirectory() + File.separator + "screenshots" + File.separator;

    public static final URL TEMPLATE_SKIN_PATH = LauncherFiles.class.getResource("/img/skin.png");
    public static final URL TEMPLATE_CLOAK_PATH = LauncherFiles.class.getResource("/img/cloak.png");
    public static final URL MISSING_TEXTURE = LauncherFiles.class.getResource("/img/missing.png");
    public static final URL VERSION_INFO_PATH = LauncherFiles.class.getResource("/version-info.json");


    public static File getMinecraftDirectory() {
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
                case macos:
                workingDirectory = new File(userHome, "Library/Application Support/minecraft/");
                break;
            default:
                workingDirectory = new File(userHome, "minecraft/"); break;
        }
        if (!workingDirectory.exists() && !workingDirectory.mkdirs())
            throw new RuntimeException("The working directory could not be created: " + workingDirectory);

        return workingDirectory;
    }

    static {
        new File(MINEONLINE_CACHE_FOLDER).mkdirs();
    }

}
