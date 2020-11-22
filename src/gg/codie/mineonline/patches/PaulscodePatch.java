package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class PaulscodePatch {
    public static void watchCodecs() {
        try {
            new ByteBuddy()
                    .redefine(ClassLoader.getSystemClassLoader().loadClass("paulscode.sound.SoundSystem"))
                    .visit(Advice.to(PaulscodeCodecJOrbisConstructorAdvice.class).on(ElementMatchers.isConstructor()))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            // If the lib isn't loaded the version must not need it, no need to patch it.
        }
    }
}
