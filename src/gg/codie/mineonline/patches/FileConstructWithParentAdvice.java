package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.io.File;

public class FileConstructWithParentAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0, readOnly = false) File parent, @Advice.Argument(value = 1, optional = true, readOnly = false) String child) {
        if (parent == null)
            return;
        if (child == null || child.isEmpty())
            return;

        String path = parent.getPath() + File.separator + child;

        try {
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

        parent = null;
        child = path;
    }
}