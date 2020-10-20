package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLMouseGetDYAdvice {
    public static boolean lock;

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean lockCalls() {
        return lock;
    }

    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) int dy) {
        //System.out.println("Getting DY " + dy);
        dy = - dy;
    }
}
