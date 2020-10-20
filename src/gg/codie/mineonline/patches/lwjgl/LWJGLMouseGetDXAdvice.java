package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLMouseGetDXAdvice {
    public static boolean lock;

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean lockCalls() {
        return lock;
    }
}
