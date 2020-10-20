package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLMouseGetDXAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept() {
        //System.out.println("Getting DX");
        //Mouse.setGrabbed(cursor != null);
        return false;
    }
}
