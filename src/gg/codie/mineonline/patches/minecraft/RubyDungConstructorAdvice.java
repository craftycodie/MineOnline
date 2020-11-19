package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.lwjgl.opengl.Display;

public class RubyDungConstructorAdvice {
    public static Object rubyDung;

    @Advice.OnMethodExit
    static void intercept(@Advice.This(typing = Assigner.Typing.DYNAMIC) Object rubyDung) {
        try {
            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.RubyDungConstructorAdvice").getField("rubyDung").set(null, rubyDung);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
