package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

public class LWJGLMouseSetNativeCursorAdvice {

    public static boolean isFocused = true;

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.Argument(0) Cursor cursor) {
        //Mouse.setGrabbed(cursor != null);
        //isFoucused = cursor != null;
        isFocused = false;
        Mouse.setGrabbed(isFocused);
        return true;
    }
}
