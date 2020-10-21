package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;
import org.lwjgl.input.Cursor;

public class LWJGLMouseSetNativeCursorAdvice {

    public static boolean isFocused = false;

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.Argument(0) Cursor cursor) {
        System.out.println("cursor " + cursor);

        isFocused = cursor != null;

        return true;
    }
}
