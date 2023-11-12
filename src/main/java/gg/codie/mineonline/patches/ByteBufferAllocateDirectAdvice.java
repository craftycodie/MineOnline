package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

public class ByteBufferAllocateDirectAdvice {
    @Advice.OnMethodEnter
    public static void intercept(@Advice.Argument(value = 0, readOnly = false) int size) {
        // Texture buffer size.
        if (size == 0x100000)
            size = size * 16;
    }
}
