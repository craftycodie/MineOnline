package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLMouseGetDYAdvice {
    // Used by PointerInfoGetLocationAdvice.
    public static boolean lock;

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean lockCalls() {
        try {
            if ((boolean) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.client.LegacyGameManager").getMethod("mineonlineMenuOpen").invoke(null))
                return true;
        } catch (Exception ex) {
            try {
                boolean DEV = (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.Globals").getField("DEV").get(null);
                if (DEV)
                    ex.printStackTrace();
            } catch (Exception ex2) { }
        }

        return lock;
    }

    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) int dy) {
        try {
            if (!(boolean) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.InputPatch").getField("enableClassicFixes").get(null))
                return;
        } catch (Exception ex) { }

        dy = - dy;
    }
}
