package gg.codie.mineonline.patches.lwjgl;

import gg.codie.mineonline.Settings;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

import java.nio.ByteBuffer;

public class LWJGLGL11Patch {
    public static void init(boolean m1Fix) {
        try {
            LWJGLGL11GLOrthoAdvice.guiScale = Settings.singleton.getGUIScale().getIntValue();

            if (m1Fix) {
                new ByteBuddy()
                        .redefine(LWJGLGL11Patch.class.getClassLoader().loadClass("org.lwjgl.opengl.GL11"))
                        .visit(Advice.to(LWJGLGL11GLOrthoAdvice.class).on(ElementMatchers.named("glOrtho")))
                        .visit(Advice.to(LWJGLGL11GLTexSubImageAdvice.class).on(ElementMatchers.named("glTexSubImage2D").and(ElementMatchers.takesArguments(
                                int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, ByteBuffer.class
                        ))))
                        .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glClearColor").and(ElementMatchers.takesArguments(
                                float.class, float.class, float.class, float.class
                        ))))
                        .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glColor4f").and(ElementMatchers.takesArguments(
                                float.class, float.class, float.class, float.class
                        ))))
                        .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glColor4d").and(ElementMatchers.takesArguments(
                                double.class, double.class, double.class, double.class
                        ))))
                        .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glColor4b").and(ElementMatchers.takesArguments(
                                byte.class, byte.class, byte.class, byte.class
                        ))))
                        .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glColor3f").and(ElementMatchers.takesArguments(
                                float.class, float.class, float.class
                        ))))
                        .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glColor3d").and(ElementMatchers.takesArguments(
                                double.class, double.class, double.class
                        ))))
                        .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glColor3b").and(ElementMatchers.takesArguments(
                                byte.class, byte.class, byte.class
                        ))))
                        .make()
                        .load(Class.forName("org.lwjgl.opengl.GL11").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            } else {
                new ByteBuddy()
                        .redefine(LWJGLGL11Patch.class.getClassLoader().loadClass("org.lwjgl.opengl.GL11"))
                        .visit(Advice.to(LWJGLGL11GLOrthoAdvice.class).on(ElementMatchers.named("glOrtho")))
                        .visit(Advice.to(LWJGLGL11GLTexSubImageAdvice.class).on(ElementMatchers.named("glTexSubImage2D").and(ElementMatchers.takesArguments(
                                int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, ByteBuffer.class
                        ))))
                        .make()
                        .load(Class.forName("org.lwjgl.opengl.GL11").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void m1FixOnly() {
        try {
            new ByteBuddy()
                    .redefine(LWJGLGL11Patch.class.getClassLoader().loadClass("org.lwjgl.opengl.GL11"))
                    .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glClearColor").and(ElementMatchers.takesArguments(
                            float.class, float.class, float.class, float.class
                    ))))
                    .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glColor4f").and(ElementMatchers.takesArguments(
                            float.class, float.class, float.class, float.class
                    ))))
                    .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glColor4d").and(ElementMatchers.takesArguments(
                            double.class, double.class, double.class, double.class
                    ))))
                    .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glColor4b").and(ElementMatchers.takesArguments(
                            byte.class, byte.class, byte.class, byte.class
                    ))))
                    .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glColor3f").and(ElementMatchers.takesArguments(
                            float.class, float.class, float.class
                    ))))
                    .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glColor3d").and(ElementMatchers.takesArguments(
                            double.class, double.class, double.class
                    ))))
                    .visit(Advice.to(LWJGLGL11M1Advice.class).on(ElementMatchers.named("glColor3b").and(ElementMatchers.takesArguments(
                            byte.class, byte.class, byte.class
                    ))))
                    .make()
                    .load(Class.forName("org.lwjgl.opengl.GL11").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
