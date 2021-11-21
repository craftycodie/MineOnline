package gg.codie.mineonline.patches.lwjgl;

import gg.codie.mineonline.gui.rendering.Loader;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.nio.FloatBuffer;

public class LWJGLGL11M1GlFogAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 1, readOnly = false, typing = Assigner.Typing.DYNAMIC) FloatBuffer rgba) {
        float a = rgba.get();
        float b = rgba.get();
        float g = rgba.get();
        float r = rgba.get();

        FloatBuffer bgra = Loader.createDirectFloatBuffer(16);

        bgra.clear();
        bgra.put(b).put(g).put(r).put(a);
        bgra.flip();

        rgba = bgra;
    }
}
