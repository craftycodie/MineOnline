package com.ahnewark.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

import java.awt.*;

public class LWJGLMouseGetYAdvice {
    @Advice.OnMethodExit()
    static void ignoreMovement(@Advice.Return(readOnly = false) int returnY) {
        try {
            if (
                    (boolean) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.client.LegacyGameManager").getMethod("mineonlineMenuOpen").invoke(null)
                            && !(boolean) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.lwjgl.LWJGLDisplayUpdateAdvice").getField("inUpdateHook").get(null)
            ) {
                Canvas mcCanvas = (Canvas) ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.opengl.Display").getMethod("getParent").invoke(null);

                returnY = mcCanvas.getHeight() / 2;
            }
        } catch (Exception ex) {
            try {
                boolean DEV = (boolean)ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.Globals").getField("DEV").get(null);
                if (DEV)
                    ex.printStackTrace();
            } catch (Exception ex2) { }
        }
    }
}
