package gg.codie.mineonline.patches;

import gg.codie.mineonline.LauncherFiles;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedList;
import java.util.zip.ZipFile;

public class PaulscodeCodecJOrbisConstructorAdvice {

    public static Object soundSystem;

    public static void resetCodecs() {
        try {
            if (soundSystem != null) {
                soundSystem.getClass().getDeclaredMethod("cleanup").invoke(soundSystem);
                Method init = soundSystem.getClass().getDeclaredMethod("init", Class.class);
                init.setAccessible(true);
                init.invoke(soundSystem, ClassLoader.getSystemClassLoader().loadClass("paulscode.sound.libraries.LibraryLWJGLOpenAL"));
            }
        } catch (Exception ex) {
            System.err.println("Failed to reset sounds.");
            ex.printStackTrace();
        }
    }

    @Advice.OnMethodExit
    static void intercept(@Advice.This(typing = Assigner.Typing.DYNAMIC) Object thisObj) {
        try {
            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.PaulscodeCodecJOrbisConstructorAdvice").getDeclaredField("soundSystem").set(null, thisObj);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
