package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLKeyboardGetEventKeyStateAdvice {
    @Advice.OnMethodExit()
    static void lockCalls(@Advice.Return(readOnly = false) boolean returnState) {
        try {
            if (
                    (boolean) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.client.LegacyGameManager").getMethod("mineonlineMenuOpen").invoke(null)
                            && !(boolean) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLDisplayUpdateAdvice").getField("inUpdateHook").get(null)
            )
                returnState = false;
        } catch (Exception ex) {
            try {
                boolean DEV = (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.Globals").getField("DEV").get(null);
                if (DEV)
                    ex.printStackTrace();
            } catch (Exception ex2) { }
        }
    }
}
