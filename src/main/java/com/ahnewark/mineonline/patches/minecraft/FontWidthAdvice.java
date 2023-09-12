package com.ahnewark.mineonline.patches.minecraft;

import net.bytebuddy.asm.Advice;

public class FontWidthAdvice {
    @Advice.OnMethodExit()
    public static void intercept(@Advice.Argument(0) String string, @Advice.Return(readOnly = false) int len) {
        try {
            Class fontClass = ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.gui.rendering.Font");
            Object minecraftFont = fontClass.getDeclaredField("minecraftFont").get(null);
            len = (int)fontClass.getDeclaredMethod("width", String.class).invoke(minecraftFont, string);
        } catch (Exception ex) {
            // ignore.
        }
    }
}
