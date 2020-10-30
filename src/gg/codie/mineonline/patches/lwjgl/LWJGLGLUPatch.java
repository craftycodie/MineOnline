package gg.codie.mineonline.patches.lwjgl;

import gg.codie.mineonline.Settings;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class LWJGLGLUPatch {
    public static void useCustomFOV() {
        try {
            LWJGLPerspectiveAdvice.customFOV = Settings.singleton.getFOV();

            new ByteBuddy()
                    .redefine(LWJGLGLUPatch.class.getClassLoader().loadClass("org.lwjgl.util.glu.GLU"))
                    .visit(Advice.to(LWJGLPerspectiveAdvice.class).on(ElementMatchers.named("gluPerspective")))
                    .make()
                    .load(Class.forName("org.lwjgl.util.glu.GLU").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void zoom(){
        try {
            LWJGLPerspectiveAdvice.customFOV = 20;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void unZoom(){
        LWJGLPerspectiveAdvice.customFOV = Settings.singleton.getFOV();
    }
}
