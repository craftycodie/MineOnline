package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

public class ClassLoaderCheckCertsAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept() {
        return true;
    }
}
