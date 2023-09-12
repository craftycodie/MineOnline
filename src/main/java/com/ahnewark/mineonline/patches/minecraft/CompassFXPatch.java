package com.ahnewark.mineonline.patches.minecraft;

import com.ahnewark.mineonline.client.LegacyGameManager;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class CompassFXPatch {
    public static void init() {
        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().compassFXClass != null) {
            try {
                new ByteBuddy()
                        .redefine(ClassLoader.getSystemClassLoader().loadClass(LegacyGameManager.getVersion().compassFXClass))
                        .visit(Advice.to(FontDrawAdvice.class).on(ElementMatchers.takesArguments(
                                String.class, int.class, int.class, int.class, boolean.class
                        )))
                        .visit(Advice.to(CompassFXAdvice.class).on(ElementMatchers.returns(void.class).and(ElementMatchers.takesArguments(0))))
                        .make()
                        .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            } catch (ClassNotFoundException ex) {
                // If the lib isn't loaded the version must not need it, no need to patch it.
            }
        }
    }
}
