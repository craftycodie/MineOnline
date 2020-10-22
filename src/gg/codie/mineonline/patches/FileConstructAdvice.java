package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import gg.codie.mineonline.LauncherFiles;
import net.bytebuddy.asm.Advice;

import java.io.File;
import java.lang.reflect.Method;

public class FileConstructAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0, readOnly = false) String path) {
        try {
            if (path == null || path.isEmpty() || path.startsWith("file:")) {
                return;
            }

            boolean DEV = (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.Globals").getField("DEV").get(null);
            String MINECRAFT_RESOURCES_PATH = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.LauncherFiles").getField("MINECRAFT_RESOURCES_PATH").get(null);
            String MINEONLINE_RESOURCES_PATH = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.LauncherFiles").getField("MINEONLINE_RESOURCES_PATH").get(null);

            String resourcesVersion = (String) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.FilePatch").getField("resourcesVersion").get(null);



            if (path.startsWith(MINECRAFT_RESOURCES_PATH) || path.substring(1).startsWith(MINECRAFT_RESOURCES_PATH)) {
//                if(DEV) {
//                    System.out.println("Old Path: " + path);
//                }

                path = path.replace(MINECRAFT_RESOURCES_PATH, MINEONLINE_RESOURCES_PATH + resourcesVersion + File.separator);

//                if(DEV) {
//                    System.out.println("New Path: " + path);
//                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}