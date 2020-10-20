package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLMouseSetGrabbedAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.Argument(0) boolean grabbed) {
        System.out.println("Setting grabbed: " + grabbed);
//        LWJGLMouseSetNativeCursorAdvice.isFoucused = grabbed;
//        Mouse.setGrabbed(cursor != null);
//        return true;
        return false;
    }
}
