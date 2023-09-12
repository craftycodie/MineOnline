package com.ahnewark.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.awt.*;

public class RobotMouseMoveAdvice {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean lockCalls(@Advice.Argument(0) int x, @Advice.Argument(1) int y) {
        try {
            if (!(boolean) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.client.LegacyGameManager").getMethod("mineonlineMenuOpen").invoke(null)) {
                Canvas mcCanvas = (Canvas) ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.opengl.Display").getMethod("getParent").invoke(null);
                ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.input.Mouse").getDeclaredMethod("setCursorPosition", int.class, int.class).invoke(null, x - mcCanvas.getLocationOnScreen().x, y - mcCanvas.getLocationOnScreen().y);
            }

            return true;
        } catch (Exception ex) {
            try {
                boolean DEV = (boolean)ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.Globals").getField("DEV").get(null);
                if (DEV)
                    ex.printStackTrace();
            } catch (Exception ex2) { }
        }

        return false;
    }
}
