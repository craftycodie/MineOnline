package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLMouseSetNativeCursorAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept() {
        return true;
    }
}
