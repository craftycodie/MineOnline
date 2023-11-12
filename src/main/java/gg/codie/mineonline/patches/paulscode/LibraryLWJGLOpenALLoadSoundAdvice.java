package gg.codie.mineonline.patches.paulscode;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.AudioFormat;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

public class LibraryLWJGLOpenALLoadSoundAdvice {
    @Advice.OnMethodEnter(skipOn = Advice.OnDefaultValue.class)
    static boolean intercept() {
        return false;
    }

    @Advice.OnMethodExit
    static void intercept(
            @Advice.Argument(value = 0, readOnly = false, typing = Assigner.Typing.DYNAMIC) Object filenameURL,
            @Advice.This(typing = Assigner.Typing.DYNAMIC) Object thisPatching,
            @Advice.FieldValue(value = "bufferMap", readOnly = false) HashMap bufferMap,
            @Advice.FieldValue(value = "ALBufferMap", readOnly = false) HashMap ALBufferMap,
            @Advice.Return(readOnly = false) boolean returnValue
    ) {
        try {
            String filename = (String)filenameURL.getClass().getMethod("getFilename").invoke(filenameURL);
            URL url = (URL)filenameURL.getClass().getMethod("getURL").invoke(filenameURL);

            Class libraryClass = ClassLoader.getSystemClassLoader().loadClass("paulscode.sound.Library");

            Method importantMessage = libraryClass.getDeclaredMethod("importantMessage", String.class);
            Method errorMessage = libraryClass.getDeclaredMethod("errorMessage", String.class);
            Method errorCheck = libraryClass.getDeclaredMethod("errorCheck", boolean.class, String.class);
            Method printStackTrace = libraryClass.getDeclaredMethod("printStackTrace", Exception.class);

            errorCheck.setAccessible(true);
            importantMessage.setAccessible(true);
            errorMessage.setAccessible(true);
            printStackTrace.setAccessible(true);

            if (bufferMap == null) {
                bufferMap = new HashMap();
                importantMessage.invoke(thisPatching, "Buffer Map was null in method 'loadSound'");
            }
            if (ALBufferMap == null) {
                ALBufferMap = new HashMap();
                importantMessage.invoke(thisPatching, "Open AL Buffer Map was null in method'loadSound'");
            }
            if ((boolean)errorCheck.invoke(thisPatching, filenameURL == null, "Filename/URL not specified in method 'loadSound'")) {
                returnValue = false;
                return;
            }
            if (bufferMap.get(filename) != null) {
                returnValue = false;
                return;
            }
            Object iCodec3 = ClassLoader.getSystemClassLoader().loadClass("paulscode.sound.SoundSystemConfig").getDeclaredMethod("getCodec", String.class).invoke(null, filename);
            if ((boolean)errorCheck.invoke(thisPatching, iCodec3 == null, "No codec found for file '" + filename + "' in method 'loadSound'")) {
                returnValue = false;
                return;
            }
            iCodec3.getClass().getDeclaredMethod("initialize", URL.class).invoke(iCodec3, url);
            Object soundBuffer4 = iCodec3.getClass().getDeclaredMethod("readAll").invoke(iCodec3);
            iCodec3.getClass().getDeclaredMethod("cleanup").invoke(iCodec3);
            if ((boolean)errorCheck.invoke(thisPatching, soundBuffer4 == null, "Sound buffer null in method 'loadSound'")) {
                returnValue = false;
                return;
            }
            bufferMap.put(filename, soundBuffer4);
            AudioFormat audioFormat3;
            int integer5;
            if ((audioFormat3 = (AudioFormat)soundBuffer4.getClass().getDeclaredField("audioFormat").get(soundBuffer4)).getChannels() == 1) {
                if (audioFormat3.getSampleSizeInBits() == 8) {
                    integer5 = 4352;
                }
                else {
                    if (audioFormat3.getSampleSizeInBits() != 16) {
                        errorMessage.invoke(thisPatching, "Illegal sample size in method 'loadSound'");
                        returnValue = false;
                        return;
                    }
                    integer5 = 4353;
                }
            }
            else {
                if (audioFormat3.getChannels() != 2) {
                    errorMessage.invoke(thisPatching, "File neither mono nor stereo in method 'loadSound'");
                    returnValue = false;
                    return;
            }
                if (audioFormat3.getSampleSizeInBits() == 8) {
                    integer5 = 4354;
                }
                else {
                    if (audioFormat3.getSampleSizeInBits() != 16) {
                        errorMessage.invoke(thisPatching, "Illegal sample size in method 'loadSound'");
                        returnValue = false;
                        return;
                    }
                    integer5 = 4355;
                }
            }

            IntBuffer intBuffer6;
            AL10.alGenBuffers(intBuffer6 = BufferUtils.createIntBuffer(1));
            if ((boolean)errorCheck.invoke(thisPatching, AL10.alGetError() != 0, "alGenBuffers error when loading " + filename)) {
                returnValue = false;
                return;
            }
            ByteBuffer byteBuffer7;
            (byteBuffer7 = BufferUtils.createByteBuffer(((byte[])soundBuffer4.getClass().getDeclaredField("audioData").get(soundBuffer4)).length)).clear();
            byteBuffer7.put((byte[])soundBuffer4.getClass().getDeclaredField("audioData").get(soundBuffer4));
            byteBuffer7.flip();
            AL10.alBufferData(intBuffer6.get(0), integer5, byteBuffer7, (int)audioFormat3.getSampleRate());
            if ((boolean)errorCheck.invoke(thisPatching, AL10.alGetError() != 0, "alBufferData error when loading " + filename) && (boolean)errorCheck.invoke(thisPatching, intBuffer6 == null, "Sound buffer was not created for " + filename)) {
                returnValue = false;
                return;
            }
            ALBufferMap.put(filename, intBuffer6);
            returnValue = true;
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
