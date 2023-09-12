package com.ahnewark.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLDisplayIsCloseRequestedAdvice {
    public static boolean isCloseRequested = false;

    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) boolean returnIsCloseRequested) {
        try {
            Class thisClass = ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.lwjgl.LWJGLDisplayIsCloseRequestedAdvice");
            returnIsCloseRequested =  (boolean)thisClass.getField("isCloseRequested").get(null);
        } catch (Exception ex) {
            //System.out.println("Failed to check close!");
        }
    }
}
