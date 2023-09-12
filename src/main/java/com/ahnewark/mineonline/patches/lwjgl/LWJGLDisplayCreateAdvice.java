package com.ahnewark.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.lang.reflect.Method;

public class LWJGLDisplayCreateAdvice {
    public static int sampleCount = 0;
    public static int stencilCount = 0;
    public static int coverageSampleCount = 0;
    public static Thread minecraftThread;

    @Advice.OnMethodEnter
    static void entry(@Advice.Argument(value = 0, readOnly = false, typing = Assigner.Typing.DYNAMIC) Object pixelFormat,
        @Advice.Argument(value = 1, readOnly = false, typing = Assigner.Typing.DYNAMIC) Object contextAttribs) {
        try {
            Class pixelFormatClass = ClassLoader.getSystemClassLoader().loadClass("org.lwjgl.opengl.PixelFormat");
            Method withDepthBuffer = pixelFormatClass.getMethod("withDepthBits", int.class);
            pixelFormat = withDepthBuffer.invoke(pixelFormat, 24);

            Class thisClass = ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.lwjgl.LWJGLDisplayCreateAdvice");
            int sampleCount = (int)thisClass.getField("sampleCount").get(null);
            int stencilCount = (int)thisClass.getField("stencilCount").get(null);
            int coverageSampleCount = (int)thisClass.getField("coverageSampleCount").get(null);
            thisClass.getField("minecraftThread").set(null, Thread.currentThread());

            Method withSamples = pixelFormatClass.getMethod("withSamples", int.class);
            pixelFormat = withSamples.invoke(pixelFormat, sampleCount);
            Method withStencil = pixelFormatClass.getMethod("withStencilBits", int.class);
            pixelFormat = withStencil.invoke(pixelFormat, stencilCount);
            Method withCoverageSamples = pixelFormatClass.getMethod("withCoverageSamples", int.class);
            pixelFormat = withCoverageSamples.invoke(pixelFormat, coverageSampleCount);
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
