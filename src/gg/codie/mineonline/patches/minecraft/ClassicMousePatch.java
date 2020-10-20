package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.patches.ComponentGetLocationOnScreenAdvice;
import gg.codie.mineonline.patches.PointerInfoGetLocationAdvice;
import gg.codie.mineonline.patches.RobotMouseMoveAdvice;
import gg.codie.mineonline.patches.lwjgl.*;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

import java.nio.IntBuffer;

public class ClassicMousePatch {
    public static void fixNativeCursorClassic() {
        try {
            new ByteBuddy()
                    .redefine(ClassicMousePatch.class.getClassLoader().loadClass("org.lwjgl.input.Mouse"))
                    .visit(Advice.to(LWJGLMouseSetNativeCursorAdvice.class).on(ElementMatchers.named("setNativeCursor")))
                    .visit(Advice.to(LWJGLMouseSetGrabbedAdvice.class).on(ElementMatchers.named("setGrabbed")))
                    .visit(Advice.to(LWJGLMouseSetCursorPositionAdvice.class).on(ElementMatchers.named("setCursorPosition")))
                    .visit(Advice.to(LWJGLMouseGetDXAdvice.class).on(ElementMatchers.named("getDX")))
                    .visit(Advice.to(LWJGLMouseGetDYAdvice.class).on(ElementMatchers.named("getDY")))
                    .visit(Advice.to(LWJGLMouseGetXAdvice.class).on(ElementMatchers.named("getX")))
                    .visit(Advice.to(LWJGLMouseGetYAdvice.class).on(ElementMatchers.named("getY")))
                    .make()
                    .load(Class.forName("org.lwjgl.input.Mouse").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

            new ByteBuddy()
                    .redefine(ClassicMousePatch.class.getClassLoader().loadClass("org.lwjgl.input.Cursor"))
                    .visit(Advice.to(LWJGLConstructCursorAdvice.class).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArgument(7, IntBuffer.class))))
                    .make()
                    .load(Class.forName("org.lwjgl.input.Cursor").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

            new ByteBuddy()
                    .redefine(ClassicMousePatch.class.getClassLoader().loadClass("java.awt.Robot"))
                    .visit(Advice.to(RobotMouseMoveAdvice.class).on(ElementMatchers.named("mouseMove")))
                    .make()
                    .load(Class.forName("java.awt.Robot").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

            new ByteBuddy()
                    .redefine(ClassicMousePatch.class.getClassLoader().loadClass("java.awt.PointerInfo"))
                    .visit(Advice.to(PointerInfoGetLocationAdvice.class).on(ElementMatchers.named("getLocation")))
                    .make()
                    .load(Class.forName("java.awt.PointerInfo").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

            new ByteBuddy()
                    .redefine(ClassicMousePatch.class.getClassLoader().loadClass("java.awt.Component"))
                    .visit(Advice.to(ComponentGetLocationOnScreenAdvice.class).on(ElementMatchers.named("getLocationOnScreen")))
                    .make()
                    .load(Class.forName("java.awt.Component").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
