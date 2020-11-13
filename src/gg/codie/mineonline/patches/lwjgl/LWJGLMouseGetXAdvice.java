package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

import java.awt.*;

public class LWJGLMouseGetXAdvice {
    @Advice.OnMethodExit()
    static void ignoreMovement(@Advice.Return(readOnly = false) int returnX) {
        try {
            if (
                    (boolean) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.client.LegacyGameManager").getMethod("mineonlineMenuOpen").invoke(null)
                            && !(boolean) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLDisplayUpdateAdvice").getField("inUpdateHook").get(null)
            ) {
                Canvas mcCanvas = (Canvas) ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.opengl.Display").getMethod("getParent").invoke(null);

                returnX = (mcCanvas.getWidth() / 2);
            }
        } catch (Exception ex) {
            try {
                boolean DEV = (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.Globals").getField("DEV").get(null);
                if (DEV)
                    ex.printStackTrace();
            } catch (Exception ex2) { }
        }
    }
}
