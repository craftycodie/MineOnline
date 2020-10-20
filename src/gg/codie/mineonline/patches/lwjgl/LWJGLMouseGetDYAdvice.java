package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLMouseGetDYAdvice {
    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) int dy) {
        //System.out.println("Getting DY");
        //Mouse.setGrabbed(cursor != null);
        //return false;
        dy = - dy;
    }
}
