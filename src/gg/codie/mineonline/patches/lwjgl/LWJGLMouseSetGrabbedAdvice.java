package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLMouseSetGrabbedAdvice {

    @Advice.OnMethodEnter()
    static void intercept(@Advice.Argument(0) boolean grabbed) {
        try {
            if (((boolean) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.client.LegacyGameManager").getDeclaredMethod("mineonlineMenuOpen").invoke(null)) && !grabbed)
                ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.client.LegacyGameManager").getDeclaredMethod("setGUIScreen", ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.gui.screens.AbstractGuiScreen")).invoke(null, new Object[] { null });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
