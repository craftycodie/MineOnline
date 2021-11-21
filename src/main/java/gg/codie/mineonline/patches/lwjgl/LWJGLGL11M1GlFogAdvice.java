package gg.codie.mineonline.patches.lwjgl;

import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.rendering.Loader;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.nio.FloatBuffer;

public class LWJGLGL11M1GlFogAdvice {
    public final static boolean isClassic = LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().baseVersion.startsWith("c");
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 1, readOnly = false, typing = Assigner.Typing.DYNAMIC) FloatBuffer rgba) {
        try {
            boolean isClassic = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLGL11M1GlFogAdvice").getDeclaredField("isClassic").getBoolean(null);

            if (isClassic) return;

            float r = rgba.get();
            float g = rgba.get();
            float b = rgba.get();
            float a = rgba.get();

            FloatBuffer bgra = Loader.createDirectFloatBuffer(16);

            bgra.clear();
            bgra.put(b).put(g).put(r).put(a);
            bgra.flip();

            rgba = bgra;
        } catch (Exception ex) {
            //
        }
    }
}
