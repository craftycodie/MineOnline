package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

import java.nio.IntBuffer;

public class LWJGLConstructCursorAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.Argument(6)IntBuffer intBuffer) {
        if (intBuffer == null)
            return true;

        return false;
    }
}
