package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLMouseGetYAdvice {

//    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
//    static boolean intercept() {
//        System.out.println("Getting Y");
//        //Mouse.setGrabbed(cursor != null);
//        return true;
//    }

    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) int returnY) {
//
//
        if (!LWJGLMouseSetNativeCursorAdvice.isFocused)
            return;

        System.out.println("Getting y");


        try {
            Class lwjglMouseClass = ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.input.Mouse");
            int dy = (int)lwjglMouseClass.getMethod("getDY").invoke(null);

            returnY = -dy;
        } catch (Exception ex) {

        }
    }
}
