package gg.codie.mineonline.patches.paulscode;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.Display;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

public class ChannelLWJGLOpenALQueueBufferAdvice {
    @Advice.OnMethodEnter(skipOn = Advice.OnDefaultValue.class)
    static boolean intercept() {
        return false;
    }

    @Advice.OnMethodExit
    static void intercept(
            @Advice.Argument(value = 0, readOnly = false) byte[] arr,
            @Advice.This(typing = Assigner.Typing.DYNAMIC) Object thisPatching,
            @Advice.FieldValue("channelType") int channelType,
            @Advice.FieldValue("sampleRate") int sampleRate,
            @Advice.FieldValue("ALformat") int ALformat,
            @Advice.FieldValue("ALSource") IntBuffer ALSource,
            @Advice.Return(readOnly = false) boolean returnValue
    ) {
        try {
            ByteBuffer bufferBuffer = (ByteBuffer) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.paulscode.PaulscodePatch").getField("bufferBuffer").get(null);

            Class channelClass = ClassLoader.getSystemClassLoader().loadClass("paulscode.sound.Channel");

            Method errorCheck = channelClass.getDeclaredMethod("errorCheck", boolean.class, String.class);
            Method errorMessage = channelClass.getDeclaredMethod("errorMessage", String.class);
            Method printStackTrace = channelClass.getDeclaredMethod("printStackTrace", Exception.class);
            Method playing = thisPatching.getClass().getMethod("playing");
            Method checkALError = thisPatching.getClass().getDeclaredMethod("checkALError");

            errorCheck.setAccessible(true);
            errorMessage.setAccessible(true);
            printStackTrace.setAccessible(true);
            playing.setAccessible(true);
            checkALError.setAccessible(true);

            if ((boolean)errorCheck.invoke(thisPatching, channelType != 1, "Buffers may only be queued for streaming sources.")) {
                returnValue = false;
                return;
            }
            bufferBuffer.clear();
            bufferBuffer.put(arr, 0, arr.length);
            bufferBuffer.flip();
            IntBuffer intBuffer2 = BufferUtils.createIntBuffer(1);
            AL10.alSourceUnqueueBuffers(ALSource.get(0), intBuffer2);
            if ((boolean)checkALError.invoke(thisPatching)) {
                returnValue = false;
                return;
            }
            AL10.alBufferData(intBuffer2.get(0), ALformat, bufferBuffer, sampleRate);
            if ((boolean)checkALError.invoke(thisPatching)) {
                returnValue = false;
                return;
            }
            AL10.alSourceQueueBuffers(ALSource.get(0), intBuffer2);
            returnValue = !(boolean)checkALError.invoke(thisPatching);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
