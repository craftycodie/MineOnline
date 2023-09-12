package com.ahnewark.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLMouseGetDXYAdvice {
    // Used by PointerInfoGetLocationAdvice.
    public static boolean lock;

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean lockCalls() {
        try {
            if ((boolean) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.client.LegacyGameManager").getMethod("mineonlineMenuOpen").invoke(null))
                return true;
        } catch (Exception ex) {
            try {
                boolean DEV = (boolean)ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.Globals").getField("DEV").get(null);
                if (DEV)
                    ex.printStackTrace();
            } catch (Exception ex2) { }
        }

        return lock;
    }

    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) int dxy) {
        try {
            if (!(boolean) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.minecraft.InputPatch").getField("enableClassicFixes").get(null))
                return;
        } catch (Exception ex) { }

        dxy = - dxy;
    }
}
