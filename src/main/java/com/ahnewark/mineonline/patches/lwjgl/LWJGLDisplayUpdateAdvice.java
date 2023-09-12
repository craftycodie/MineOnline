package com.ahnewark.mineonline.patches.lwjgl;

import com.ahnewark.common.utils.OSUtils;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;

public class LWJGLDisplayUpdateAdvice {
    // It's useful to know whether the game is in Minecraft or MineOnline code sometimes.
    // There might be a better place to put this though.
    public static boolean inUpdateHook;

    @Advice.OnMethodEnter
    static void intercept() throws Throwable {
        Field inUpdateHookField = ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.lwjgl.LWJGLDisplayUpdateAdvice").getField("inUpdateHook");
        inUpdateHookField.set(null, true);
        if(LWJGLDisplayPatch.updateListener != null)
            LWJGLDisplayPatch.updateListener.onUpdateEvent();
        inUpdateHookField.set(null, false);

        if (OSUtils.isM1System())
            M1Fix.drawM1Quad();
    }
}
