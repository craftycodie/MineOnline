package gg.codie.mineonline.patches.lwjgl;

import gg.codie.mineonline.gui.rendering.DisplayManager;
import net.bytebuddy.asm.Advice;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class LWJGLMouseSetNativeCursorAdvice {

    public static boolean isFocused = true;

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.Argument(0) Cursor cursor) {
        System.out.println("cursor " + cursor);

        isFocused = cursor != null;

        if (isFocused) {
            try {
                Robot minecraftRobot = (Robot) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.RobotMouseMoveAdvice").getField("minecraftRobot").get(null);

                if (minecraftRobot != null) {
                    minecraftRobot.mouseMove(DisplayManager.getCanvas().getWidth() / 2, DisplayManager.getCanvas().getHeight() / 2);
                }
            } catch (Exception ex) {
                // ignore
            }

            Mouse.setGrabbed(true);
        }


        return true;
    }
}
