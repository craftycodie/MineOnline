package gg.codie.mineonline.patches.lwjgl;

import gg.codie.mineonline.Settings;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class LWJGLMousePatch {
    public static void fixNativeCursorClassic() {
        try {
            new ByteBuddy()
                    .redefine(LWJGLMousePatch.class.getClassLoader().loadClass("org.lwjgl.input.Mouse"))
                    .visit(Advice.to(LWJGLMouseSetNativeCursorAdvice.class).on(ElementMatchers.named("setNativeCursor")))
                    .make()
                    .load(Class.forName("org.lwjgl.input.Mouse").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
