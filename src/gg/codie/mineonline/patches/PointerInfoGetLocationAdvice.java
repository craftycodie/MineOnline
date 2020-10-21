package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.awt.*;

public class PointerInfoGetLocationAdvice {
    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) Point returnPoint) {
        try {
            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLMouseGetDXAdvice").getField("lock").set(null, false);
            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLMouseGetDYAdvice").getField("lock").set(null, false);

            Class lwjglMouseClass = ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.input.Mouse");
            int x = -(int)lwjglMouseClass.getMethod("getDX").invoke(null);
            int y = (int)lwjglMouseClass.getMethod("getDY").invoke(null);

            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLMouseGetDXAdvice").getField("lock").set(null, true);
            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLMouseGetDYAdvice").getField("lock").set(null, true);

            Canvas mcCanvas = (Canvas) ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.opengl.Display").getMethod("getParent").invoke(null);

            x += mcCanvas.getLocationOnScreen().x + (mcCanvas.getWidth() / 2);
            y += mcCanvas.getLocationOnScreen().y + (mcCanvas.getHeight() / 2);
            returnPoint = new Point(x, y);
        } catch (Exception ex) {

        }
    }
}
