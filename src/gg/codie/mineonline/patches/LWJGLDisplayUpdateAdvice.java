package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

public class LWJGLDisplayUpdateAdvice {
    @Advice.OnMethodEnter
    static void intercept() {
        if(LWJGLDisplayPatch.updateListener != null)
            LWJGLDisplayPatch.updateListener.onUpdateEvent();
    }
}
