package com.ahnewark.mineonline.patches.paulscode;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

public class PaulscodePatch {
    // Used with reflection.
    public static ByteBuffer bufferBuffer = BufferUtils.createByteBuffer(5242880);

    public static void fixIndevAudio() {
        try {
            new ByteBuddy()
                    .redefine(Class.forName("paulscode.sound.libraries.ChannelLWJGLOpenAL"))
                    .visit(Advice.to(ChannelLWJGLOpenALPreLoadBuffersAdvice.class).on(ElementMatchers.named("preLoadBuffers")))
                    .visit(Advice.to(ChannelLWJGLOpenALQueueBufferAdvice.class).on(ElementMatchers.named("queueBuffer")))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

            new ByteBuddy()
                    .redefine(Class.forName("paulscode.sound.libraries.LibraryLWJGLOpenAL"))
                    .visit(Advice.to(LibraryLWJGLOpenALLoadSoundAdvice.class).on(ElementMatchers.named("loadSound")))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
