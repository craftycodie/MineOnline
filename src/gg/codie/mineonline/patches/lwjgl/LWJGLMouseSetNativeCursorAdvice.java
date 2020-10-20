package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;
import org.lwjgl.input.Cursor;

public class LWJGLMouseSetNativeCursorAdvice {

    public static boolean isFocused = true;

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.Argument(0) Cursor cursor) {
        isFocused = false;

        return true;
    }
}
