package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.awt.*;

public class ClassicPointerInfoGetLocationAdvice {
    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) Point returnPoint) {
        try {
            Class lwjglMouseClass = ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.input.Mouse");
            int x = (int)lwjglMouseClass.getMethod("getX").invoke(null);
            int y = (int)lwjglMouseClass.getMethod("getY").invoke(null);
            returnPoint = new Point(x, y);
        } catch (Exception ex) {

        }
    }
}
