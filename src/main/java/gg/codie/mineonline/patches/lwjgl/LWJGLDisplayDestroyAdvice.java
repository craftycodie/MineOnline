package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLDisplayDestroyAdvice {
    @Advice.OnMethodExit()
    static void intercept() throws Throwable {
        if (LWJGLDisplayPatch.destroyListener != null)
            LWJGLDisplayPatch.destroyListener.onDestroyEvent();
    }
}
