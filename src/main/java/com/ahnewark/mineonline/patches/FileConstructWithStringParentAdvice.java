package com.ahnewark.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.io.File;

public class FileConstructWithStringParentAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0, readOnly = false) String parent, @Advice.Argument(value = 1, optional = true, readOnly = false) String child) {
        if (parent == null)
            return;
        if (child == null || child.isEmpty())
            return;
        // Warning: This stops the hook from recurring, the class load below uses this same constructor!
        if ((!parent.toString().contains("resources") && !child.contains("options") && !parent.toString().contains(".minecraft")) || parent.toString().contains(".jar") || parent.toString().contains(".exe"))
            return;

        String path = parent + File.separator + child;

        try {
            boolean DEV = (boolean)ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.Globals").getField("DEV").get(null);
            String MINECRAFT_RESOURCES_PATH = (String)ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.LauncherFiles").getField("MINECRAFT_RESOURCES_PATH").get(null);
            String MINEONLINE_RESOURCES_PATH = (String)ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.LauncherFiles").getField("MINEONLINE_RESOURCES_PATH").get(null);

            String MINEONLINE_OPTIONS_PATH = (String)ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.LauncherFiles").getField("MINEONLINE_OPTIONS_PATH").get(null);

            String resourcesVersion = (String) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.FilePatch").getField("resourcesVersion").get(null);
            String customGameFolder = (String) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.FilePatch").getField("gameFolder").get(null);

            String OLD_MINECRAFT_FOLDER = (String)ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.LauncherFiles").getField("OLD_MINECRAFT_FOLDER").get(null);
            String NEW_MINECRAFT_FOLDER = (String)ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.LauncherFiles").getField("NEW_MINECRAFT_FOLDER").get(null);

//            if(DEV) {
//                System.out.println("Old Path: " + path);
//            }

            if (path.startsWith(MINECRAFT_RESOURCES_PATH) || path.substring(1).startsWith(MINECRAFT_RESOURCES_PATH)) {
                path = path.replace(MINECRAFT_RESOURCES_PATH, MINEONLINE_RESOURCES_PATH + resourcesVersion + File.separator);
            } else if ((path.startsWith(OLD_MINECRAFT_FOLDER) || path.startsWith(NEW_MINECRAFT_FOLDER)) && child.endsWith("options.txt")) {
                path = MINEONLINE_OPTIONS_PATH;
            }

            if (!customGameFolder.isEmpty()) {
                if (path.startsWith(OLD_MINECRAFT_FOLDER) || path.substring(1).startsWith(OLD_MINECRAFT_FOLDER)) {
                    path = path.replace(OLD_MINECRAFT_FOLDER, customGameFolder);
                } else if (path.startsWith(NEW_MINECRAFT_FOLDER) || path.substring(1).startsWith(NEW_MINECRAFT_FOLDER)) {
                    path = path.replace(NEW_MINECRAFT_FOLDER, customGameFolder);
                }
            }

//            if(DEV) {
//                System.out.println(customGameFolder);
//                System.out.println("New Path: " + path);
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        parent = null;
        child = path;
    }
}