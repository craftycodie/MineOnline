package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.awt.*;

public class RobotMouseMoveAdvice {

    public static Robot minecraftRobot;

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.This Robot robot, @Advice.Argument(0) int x, @Advice.Argument(1) int y) {
//        System.out.println("Moving mouse " + x + ", " + y);

        try {
            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.RobotMouseMoveAdvice").getField("minecraftRobot").set(null, robot);
            //ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.input.Mouse").getMethod("setCursorPosition").invoke(null, x, y);
        } catch (Exception ex) {

        }

        //Mouse.setGrabbed(cursor != null);
        return false;
    }
}
