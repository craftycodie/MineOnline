package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.awt.*;

public class PointerInfoGetLocationAdvice {

//    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
//    static boolean intercept() {
//        System.out.println("Getting location ");
//        //Mouse.setGrabbed(cursor != null);
//        return true;
//    }

    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) Point returnPoint) {
        try {
            Class lwjglMouseClass = ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.input.Mouse");
            int x = (int)lwjglMouseClass.getMethod("getX").invoke(null);
            int y = (int)lwjglMouseClass.getMethod("getY").invoke(null);

            //System.out.println("Getting location: " + x + ", " + y);

            returnPoint = new Point(x, y);
            //System.out.println("Getting location " + x + ", " + y);
            //Mouse.setGrabbed(cursor != null);
            //return true;
        } catch (Exception ex) {

        }
    }
}
