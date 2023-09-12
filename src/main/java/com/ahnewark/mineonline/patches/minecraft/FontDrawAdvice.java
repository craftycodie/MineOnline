package com.ahnewark.mineonline.patches.minecraft;

import net.bytebuddy.asm.Advice;

public class FontDrawAdvice {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean intercept(@Advice.Argument(0) String string, @Advice.Argument(1) int x, @Advice.Argument(2) int y, @Advice.Argument(3) int color, @Advice.Argument(4) boolean darken) {
        try {
            Class fontClass = ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.gui.rendering.Font");
            Object minecraftFont = fontClass.getDeclaredField("minecraftFont").get(null);
            fontClass.getDeclaredMethod("renderString", String.class, int.class, int.class, int.class, boolean.class).invoke(minecraftFont, string, x, y, color, darken);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
