package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.util.UUID;

public class PropertiesIsSignatureValidAdvice {
    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) boolean returnId) {
        returnId = true;
    }
}
