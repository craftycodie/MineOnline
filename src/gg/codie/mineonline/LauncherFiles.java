package gg.codie.mineonline;

import gg.codie.utils.OSUtils;

import java.io.File;

public class LauncherFiles {

    public static final String MINEONLINE_FOLDER = getMineOnlineDirectory() + File.separator;

    public static final String MINEONLINE_PROPS_FILE = MINEONLINE_FOLDER + "mineonline.properties";

    public static final String MINEONLINE_CACHE_FOLDER = MINEONLINE_FOLDER + "cache" + File.separator;

    public static final String CACHED_SKIN_PATH = MINEONLINE_CACHE_FOLDER + "skin.png";
    public static final String CACHED_CLOAK_PATH = MINEONLINE_CACHE_FOLDER + "cloak.png";
    public static final String LAST_LOGIN_PATH = MINEONLINE_CACHE_FOLDER + "lastlogin";

    public static final String RESOURCES_FOLDER = "." + File.separator + "res" + File.separator;

    public static final String TEMPLATE_SKIN_PATH = RESOURCES_FOLDER + "skin.png";
    public static final String TEMPLATE_CLOAK_PATH = RESOURCES_FOLDER + "cloak.png";
    public static final String MISSING_TEXTURE = RESOURCES_FOLDER + "missing.png";

    public static File getMineOnlineDirectory() {
        File workingDirectory;
        String applicationData, userHome = System.getProperty("user.home", ".");

        switch (OSUtils.getPlatform()) {
            case solaris:
                workingDirectory = new File(userHome, String.valueOf('.') + "minecraft/mineonline/");
                break;
            case windows:
                applicationData = System.getenv("APPDATA");
                if (applicationData != null) {
                    workingDirectory = new File(applicationData, "." + "minecraft/mineonline/");
                    break;
                }
                workingDirectory = new File(userHome, String.valueOf('.') + "minecraft/mineonline/");
            break;
                case macos:
                workingDirectory = new File(userHome, "Library/Application Support/minecraft/mineonline");
                break;
            default:
                workingDirectory = new File(userHome, "minecraft/mineonline/"); break;
        }
        if (!workingDirectory.exists() && !workingDirectory.mkdirs())
            throw new RuntimeException("The working directory could not be created: " + workingDirectory);

        return workingDirectory;
    }

    static {
        new File(MINEONLINE_CACHE_FOLDER).mkdirs();
    }

}
