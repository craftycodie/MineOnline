package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;
import org.lwjgl.input.Mouse;

public class LWJGLMouseSetCursorPositionAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.Argument(0) int x, @Advice.Argument(1) int y) {
        System.out.println("Setting cursor positon " + x + ", " + y);
        //Mouse.setGrabbed(cursor != null);

        LWJGLMouseSetNativeCursorAdvice.isFocused = true;
        Mouse.setGrabbed(LWJGLMouseSetNativeCursorAdvice.isFocused);
        return true;
    }
}
