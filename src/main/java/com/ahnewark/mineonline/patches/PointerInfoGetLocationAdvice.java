package com.ahnewark.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.awt.*;

public class PointerInfoGetLocationAdvice {
    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) Point returnPoint) {
        try {
            if (!(boolean) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.minecraft.InputPatch").getField("enableClassicFixes").get(null))
                return;

            Canvas mcCanvas = (Canvas) ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.opengl.Display").getMethod("getParent").invoke(null);

            // If the MineOnline menu is open, just return center screen.
            if (
                    (boolean) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.client.LegacyGameManager").getMethod("mineonlineMenuOpen").invoke(null)
                            && !(boolean) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.lwjgl.LWJGLDisplayUpdateAdvice").getField("inUpdateHook").get(null)
            ) {
                returnPoint = new Point(
                        mcCanvas.getLocationOnScreen().x + (mcCanvas.getWidth() / 2),
                        mcCanvas.getLocationOnScreen().y + (mcCanvas.getHeight() / 2)
                );
                return;
            }

            ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.lwjgl.LWJGLMouseGetDXYAdvice").getField("lock").set(null, false);

            Class lwjglMouseClass = ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.input.Mouse");
            int x = -(int)lwjglMouseClass.getMethod("getDX").invoke(null);
            int y = (int)lwjglMouseClass.getMethod("getDY").invoke(null);

            ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.lwjgl.LWJGLMouseGetDXYAdvice").getField("lock").set(null, true);

            x += mcCanvas.getLocationOnScreen().x + (mcCanvas.getWidth() / 2);
            y += mcCanvas.getLocationOnScreen().y + (mcCanvas.getHeight() / 2);
            returnPoint = new Point(x, y);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
