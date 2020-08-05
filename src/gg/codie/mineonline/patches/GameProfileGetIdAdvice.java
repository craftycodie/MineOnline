package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.util.UUID;

public class GameProfileGetIdAdvice {
    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) UUID returnId) {
        returnId = UUID.fromString(System.getProperty("mineonline.uuid"));
    }
}
