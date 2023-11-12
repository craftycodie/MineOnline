package gg.codie.mineonline.patches.lwjgl;

import gg.codie.common.utils.OSUtils;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;

public class LWJGLDisplayUpdateAdvice {
    // It's useful to know whether the game is in Minecraft or MineOnline code sometimes.
    // There might be a better place to put this though.
    public static boolean inUpdateHook;

    @Advice.OnMethodEnter
    static void intercept() throws Throwable {
        Field inUpdateHookField = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLDisplayUpdateAdvice").getField("inUpdateHook");
        inUpdateHookField.set(null, true);
        if(LWJGLDisplayPatch.updateListener != null)
            LWJGLDisplayPatch.updateListener.onUpdateEvent();
        inUpdateHookField.set(null, false);
    }
}
