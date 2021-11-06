package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

public class LWJGLGL11M1Advice {
    public static int guiScale;

    @Advice.OnMethodEnter
    static void intercept(
            @Advice.Argument(value = 0, readOnly = false, typing = Assigner.Typing.DYNAMIC) Object r,
            @Advice.Argument(value = 1, readOnly = false, typing = Assigner.Typing.DYNAMIC) Object g,
            @Advice.Argument(value = 2, readOnly = false, typing = Assigner.Typing.DYNAMIC) Object b) {
        Object temp = r;
        r = b;
        b = temp;
    }
}
