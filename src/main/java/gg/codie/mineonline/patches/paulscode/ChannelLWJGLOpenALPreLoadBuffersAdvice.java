package gg.codie.mineonline.patches.paulscode;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

public class ChannelLWJGLOpenALPreLoadBuffersAdvice {
    @Advice.OnMethodEnter(skipOn = Advice.OnDefaultValue.class)
    static boolean intercept() {
        return false;
    }

    @Advice.OnMethodExit
    static void intercept(
            @Advice.Argument(value = 0, readOnly = false) LinkedList<byte[]> linkedList,
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

            if ((boolean)errorCheck.invoke(thisPatching, linkedList == null, "Buffer List null in method 'preLoadBuffers'")) {
                returnValue = false;
                return;
            }

            boolean integer4;
            if (integer4 = (boolean)playing.invoke(thisPatching)) {
                AL10.alSourceStop(ALSource.get(0));
                checkALError.invoke(thisPatching);
            }

            int integer3;
            if ((integer3 = AL10.alGetSourcei(ALSource.get(0), 4118)) > 0) {
                IntBuffer intBuffer3;
                AL10.alGenBuffers(intBuffer3 = BufferUtils.createIntBuffer(integer3));
                if ((boolean)errorCheck.invoke(thisPatching, checkALError.invoke(thisPatching), "Error clearing stream buffers in method 'preLoadBuffers'")) {
                    returnValue = false;
                    return;
                }
                AL10.alSourceUnqueueBuffers(ALSource.get(0), intBuffer3);
                if ((boolean)errorCheck.invoke(thisPatching, checkALError.invoke(thisPatching), "Error unqueuing stream buffers in method 'preLoadBuffers'")) {
                    returnValue = false;
                    return;
                }
            }

            if (integer4) {
                AL10.alSourcePlay(ALSource.get(0));
                checkALError.invoke(thisPatching);
            }
            IntBuffer intBuffer3;
            AL10.alGenBuffers(intBuffer3 = BufferUtils.createIntBuffer(linkedList.size()));
            if ((boolean)errorCheck.invoke(thisPatching, checkALError.invoke(thisPatching), "Error generating stream buffers in method 'preLoadBuffers'")) {
                returnValue = false;
                return;
            }

            for (int i = 0; i < linkedList.size(); ++i) {
                bufferBuffer.clear();
                bufferBuffer.put(linkedList.get(i), 0, linkedList.get(i).length);
                bufferBuffer.flip();
                try {
                    AL10.alBufferData(intBuffer3.get(i), ALformat, bufferBuffer, sampleRate);
                }
                catch (Exception exception) {
                    errorMessage.invoke(thisPatching, "Error creating buffers in method 'preLoadBuffers'");
                    printStackTrace.invoke(exception);
                    returnValue = false;
                    return;
                }
                if ((boolean)errorCheck.invoke(thisPatching, checkALError.invoke(thisPatching), "Error creating buffers in method 'preLoadBuffers'")) {
                    returnValue = false;
                    return;
                }
            }

            try {
                AL10.alSourceQueueBuffers(ALSource.get(0), intBuffer3);
            }
            catch (Exception exception2) {
                errorMessage.invoke(thisPatching, "Error queuing buffers in method 'preLoadBuffers'");
                printStackTrace.invoke(thisPatching, exception2);
                returnValue = false;
                return;
            }
            if ((boolean)errorCheck.invoke(thisPatching, checkALError.invoke(thisPatching), "Error queuing buffers in method 'preLoadBuffers'")) {
                returnValue = false;
                return;
            }
            AL10.alSourcePlay(ALSource.get(0));
            returnValue = !(boolean)errorCheck.invoke(thisPatching, checkALError.invoke(thisPatching), "Error playing source in method 'preLoadBuffers'");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
