package com.ahnewark.mineonline.patches;

import net.bytebuddy.asm.Advice;

public class ReturnTrueAdvice {
    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) boolean returnValue) {
        returnValue = true;
    }
}
