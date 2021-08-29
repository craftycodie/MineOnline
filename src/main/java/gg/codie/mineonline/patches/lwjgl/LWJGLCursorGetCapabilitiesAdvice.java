package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLCursorGetCapabilitiesAdvice {
    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) int capabilities) {
        capabilities |= 1;
    }
}
