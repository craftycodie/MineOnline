package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLDisplayUpdateAdvice {
    @Advice.OnMethodEnter
    static void intercept() throws Throwable {
        if(LWJGLDisplayPatch.updateListener != null)
            LWJGLDisplayPatch.updateListener.onUpdateEvent();
    }
}
