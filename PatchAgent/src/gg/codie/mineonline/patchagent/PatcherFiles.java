package gg.codie.mineonline.patchagent;

import java.io.File;
import java.net.URL;

public class PatcherFiles {

    public static final String MINEONLINE_FOLDER = getMinecraftDirectory() + File.separator + "mineonline" + File.separator;

    public static final String MINEONLINE_LIBRARY_FOLDER = MINEONLINE_FOLDER + "lib" + File.separator;

    public static final String PATCH_AGENT_JAR = MINEONLINE_LIBRARY_FOLDER + "PatchAgent.jar";


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
}
