package gg.codie.mineonline.patches;

import gg.codie.mineonline.Settings;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class LWJGLPerspectivePatch {
    public static void useCustomFOV() {
        try {
            LWJGLPerspectiveAdvice.customFOV = Settings.settings.optFloat(Settings.FOV, 70);

            if (LWJGLPerspectiveAdvice.customFOV == 70)
                return;

            new ByteBuddy()
                    .redefine(LWJGLPerspectivePatch.class.getClassLoader().loadClass("org.lwjgl.util.glu.GLU"))
                    .visit(Advice.to(LWJGLPerspectiveAdvice.class).on(ElementMatchers.named("gluPerspective")))
                    .make()
                    .load(Class.forName("org.lwjgl.util.glu.GLU").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
