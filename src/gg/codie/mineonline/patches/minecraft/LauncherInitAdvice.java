package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.asm.Advice;

public class LauncherInitAdvice {
    public static String latestVersion;

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 1, readOnly = false) String latestVersion) {
            try {
                String patchedVersion = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.LauncherInitAdvice").getField("latestVersion").get(null);
                if (patchedVersion != null)
                    latestVersion = patchedVersion;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

    }

}
