package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;

public class LWJGLDisplayDestroyAdvice {
    @Advice.OnMethodEnter()
    static boolean intercept() throws Throwable {
        if (LWJGLDisplayPatch.destroyListener != null)
            LWJGLDisplayPatch.destroyListener.onDestroyEvent();

        return (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLDisplayIsCloseRequestedAdvice").getField("isCloseRequested").get(null);
    }
}
