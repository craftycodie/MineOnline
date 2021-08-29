package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.client.LegacyGameManager;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class FontPatch {
    public static void init() {
        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().fontClass != null) {
            try {
                new ByteBuddy()
                        .redefine(ClassLoader.getSystemClassLoader().loadClass(LegacyGameManager.getVersion().fontClass))
                        .visit(Advice.to(FontDrawAdvice.class).on(ElementMatchers.takesArguments(
                                String.class, int.class, int.class, int.class, boolean.class
                        )))
                        .visit(Advice.to(FontWidthAdvice.class).on(ElementMatchers.takesArguments(
                                String.class
                        ).and(ElementMatchers.returns(int.class))))
                        .make()
                        .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            } catch (ClassNotFoundException ex) {
                // If the lib isn't loaded the version must not need it, no need to patch it.
            }
        }
    }
}
