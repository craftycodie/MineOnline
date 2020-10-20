package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.awt.*;

public class ComponentGetLocationOnScreenAdvice {
    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) Point returnPoint) {
        try {
            if (ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLMouseSetNativeCursorAdvice").getField("isFocused").getBoolean(null))
                returnPoint = new Point(0, 0);
        } catch (Exception ex) {

        }
    }
}
