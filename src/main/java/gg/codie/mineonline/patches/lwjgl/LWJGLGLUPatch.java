package gg.codie.mineonline.patches.lwjgl;

import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class LWJGLGLUPatch {
    public static void useCustomFOV() {
        try {
            if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().useFOVPatch)
                LWJGLGLUPerspectiveAdvice.customFOV = Settings.singleton.getFOV();

            new ByteBuddy()
                    .redefine(LWJGLGLUPatch.class.getClassLoader().loadClass("org.lwjgl.util.glu.GLU"))
                    .visit(Advice.to(LWJGLGLUPerspectiveAdvice.class).on(ElementMatchers.named("gluPerspective")))
                    .make()
                    .load(Class.forName("org.lwjgl.util.glu.GLU").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void zoom(){
        try {
            LWJGLGLUPerspectiveAdvice.zoom = true;

            if (Settings.singleton.getZoomToast()) {
                Settings.singleton.setZoomToast(false);
                Settings.singleton.saveSettings();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void unZoom(){
        LWJGLGLUPerspectiveAdvice.zoom = false;
    }
}
