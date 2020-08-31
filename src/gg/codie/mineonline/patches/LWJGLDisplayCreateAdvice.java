package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

public class LWJGLDisplayCreateAdvice {
    @Advice.OnMethodEnter
    static void intercept() {
        if(LWJGLDisplayPatch.createListener != null)
            LWJGLDisplayPatch.createListener.onCreateEvent();
    }
}
