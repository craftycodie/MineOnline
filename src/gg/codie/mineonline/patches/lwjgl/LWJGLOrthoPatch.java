package gg.codie.mineonline.patches.lwjgl;

import gg.codie.mineonline.Settings;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class LWJGLOrthoPatch {
    public static void useGuiScale() {
        try {
            LWJGLOrthoAdvice.guiScale = Settings.settings.optInt(Settings.GUI_SCALE, 0);

            new ByteBuddy()
                    .redefine(LWJGLOrthoPatch.class.getClassLoader().loadClass("org.lwjgl.opengl.GL11"))
                    .visit(Advice.to(LWJGLOrthoAdvice.class).on(ElementMatchers.named("glOrtho")))
                    .make()
                    .load(Class.forName("org.lwjgl.opengl.GL11").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
