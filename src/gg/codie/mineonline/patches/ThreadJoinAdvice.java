package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

public class ThreadJoinAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Thread thisThread) throws InterruptedException {
        try {
            if (thisThread == ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLDisplayCreateAdvice").getField("minecraftThread").get(null))
                throw new InterruptedException("Interrupted by MineOnline");
        } catch (ReflectiveOperationException ex) {
            // ignore
        }
    }
}