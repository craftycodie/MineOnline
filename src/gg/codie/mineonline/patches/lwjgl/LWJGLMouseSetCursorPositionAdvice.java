package gg.codie.mineonline.patches.lwjgl;

import gg.codie.mineonline.gui.rendering.DisplayManager;
import net.bytebuddy.asm.Advice;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class LWJGLMouseSetCursorPositionAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.Argument(value = 0, readOnly = false) int x, @Advice.Argument(value = 1, readOnly = false) int y) {
        x += DisplayManager.getFrame().getLocationOnScreen().x;
        y += DisplayManager.getFrame().getLocationOnScreen().y;

        //System.out.println("Setting cursor positon " + x + ", " + y);

        Mouse.setGrabbed(true);

        try {
            Robot minecraftRobot = (Robot) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.RobotMouseMoveAdvice").getField("minecraftRobot").get(null);

            if (minecraftRobot != null) {
                minecraftRobot.mouseMove(x, y);
            }
        } catch (Exception ex) {
            // ignore
        }

        LWJGLMouseSetNativeCursorAdvice.isFocused = true;

        return true;
    }
}
