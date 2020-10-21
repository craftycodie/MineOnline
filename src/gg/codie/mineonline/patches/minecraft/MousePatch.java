package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.patches.PointerInfoGetLocationAdvice;
import gg.codie.mineonline.patches.lwjgl.LWJGLCursorGetCapabilitiesAdvice;
import gg.codie.mineonline.patches.lwjgl.LWJGLMouseGetDYAdvice;
import gg.codie.mineonline.patches.lwjgl.LWJGLMouseSetNativeCursorAdvice;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class MousePatch {
    public static void fixMouseIssues() {
        try {
            new ByteBuddy()
                    .redefine(MousePatch.class.getClassLoader().loadClass("org.lwjgl.input.Mouse"))
                    // Determine whether the cursor is focused.
                    .visit(Advice.to(LWJGLMouseSetNativeCursorAdvice.class).on(ElementMatchers.named("setNativeCursor")))
                    // Lock Delta calls to MineOnline, indev calls it and throws away the result, and it can only be called once per frame.
                    .visit(Advice.to(LWJGLMouseGetDYAdvice.class).on(ElementMatchers.named("getDX")))
                    .visit(Advice.to(LWJGLMouseGetDYAdvice.class).on(ElementMatchers.named("getDY")))
                    .make()
                    .load(Class.forName("org.lwjgl.input.Mouse").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

            new ByteBuddy()
                    .redefine(MousePatch.class.getClassLoader().loadClass("org.lwjgl.input.Cursor"))
                    // Allow a cursor to be created whether compatible or not, it's never used but it's existence is queried.
                    .visit(Advice.to(LWJGLCursorGetCapabilitiesAdvice.class).on(ElementMatchers.named("getCapabilities")))
                    .make()
                    .load(Class.forName("org.lwjgl.input.Cursor").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

            new ByteBuddy()
                    .redefine(MousePatch.class.getClassLoader().loadClass("java.awt.PointerInfo"))
                    // Used in getting the mouse movements.
                    .visit(Advice.to(PointerInfoGetLocationAdvice.class).on(ElementMatchers.named("getLocation")))
                    .make()
                    .load(Class.forName("java.awt.PointerInfo").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
