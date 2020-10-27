package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Method;

public class LWJGLDisplayCreateAdvice {
    @Advice.OnMethodEnter
    static void entry(@Advice.Argument(value = 0, readOnly = false, typing = Assigner.Typing.DYNAMIC) Object pixelFormat,
        @Advice.Argument(value = 1, readOnly = false, typing = Assigner.Typing.DYNAMIC) Object contextAttribs) {
        try {
            Class pixelFormatClass = ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.opengl.PixelFormat");
            Method withDepthBuffer = pixelFormatClass.getMethod("withDepthBits", int.class);
            pixelFormat = withDepthBuffer.invoke(pixelFormat, 24);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Advice.OnMethodExit
    static void intercept() {
        if(LWJGLDisplayPatch.createListener != null)
            LWJGLDisplayPatch.createListener.onCreateEvent();
    }
}
