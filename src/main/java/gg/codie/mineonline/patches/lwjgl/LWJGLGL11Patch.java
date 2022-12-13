package gg.codie.mineonline.patches.lwjgl;

import gg.codie.mineonline.Settings;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class LWJGLGL11Patch {
    public static void init() {
        try {
            LWJGLGL11GLOrthoAdvice.guiScale = Settings.singleton.getGUIScale().getIntValue();

            new ByteBuddy()
                    .redefine(LWJGLGL11Patch.class.getClassLoader().loadClass("org.lwjgl.opengl.GL11"))
                    .visit(Advice.to(LWJGLGL11GLOrthoAdvice.class).on(ElementMatchers.named("glOrtho")))
                    .visit(Advice.to(LWJGLGL11GLTexSubImageAdvice.class).on(ElementMatchers.named("glTexSubImage2D").and(ElementMatchers.takesArguments(
                            int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, ByteBuffer.class
                    ))))
                    .make()
                    .load(Class.forName("org.lwjgl.opengl.GL11").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
