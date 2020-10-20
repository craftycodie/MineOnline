package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.awt.*;

public class InfdevPointerInfoGetLocationAdvice {
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

            //Frame mcFrame = (Frame) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.gui.rendering.DisplayManager").getMethod("getFrame").invoke(null);
            Canvas mcCanvas = (Canvas) ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.opengl.Display").getMethod("getParent").invoke(null);

//            System.out.println("DELTA " + x + ", " + y);
//            System.out.println("CANVAS " + mcCanvas);

            //returnPoint = new Point(x, y);
//            x += mcFrame.getLocationOnScreen().x + mcCanvas.getX() + mcFrame.getInsets().top + (mcFrame.getWidth() / 2);
//            y += mcFrame.getLocationOnScreen().y + mcCanvas.getY() + mcFrame.getInsets().left + (mcFrame.getHeight() / 2);

            x += mcCanvas.getLocationOnScreen().x + (mcCanvas.getWidth() / 2);
            y += mcCanvas.getLocationOnScreen().y + (mcCanvas.getHeight() / 2);
            returnPoint = new Point(x, y);

//            System.out.println("CURSOR " + x + ", " + y);

        } catch (Exception ex) {

        }
    }
}
