package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLMouseGetXAdvice {

//    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
//    static boolean intercept() {
//        System.out.println("Getting X");
//        //Mouse.setGrabbed(cursor != null);
//        return true;
//    }

    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) int returnX) {
//
        if (!LWJGLMouseSetNativeCursorAdvice.isFocused)
            return;

        System.out.println("Getting x");


        try {
            Class lwjglMouseClass = ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.input.Mouse");
            int dx = (int)lwjglMouseClass.getMethod("getDX").invoke(null);

            returnX = dx;
        } catch (Exception ex) {

        }
    }
}
