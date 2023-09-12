package com.ahnewark.mineonline.patches.minecraft;

import com.ahnewark.mineonline.client.LegacyGameManager;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class ColorizerPatch {
    public static void init() {
        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().grassColorizerClass != null) {
            try {
                new ByteBuddy()
                        .redefine(ClassLoader.getSystemClassLoader().loadClass(LegacyGameManager.getVersion().grassColorizerClass))
                        .visit(Advice.to(GrassColorizerAdvice.class).on(ElementMatchers.takesArguments(
                                double.class, double.class
                        )))
                        .make()
                        .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            } catch (ClassNotFoundException ex) {
                // If the lib isn't loaded the version must not need it, no need to patch it.
            }
        }

        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().foliageColorizerClass != null) {
            try {
                new ByteBuddy()
                        .redefine(ClassLoader.getSystemClassLoader().loadClass(LegacyGameManager.getVersion().foliageColorizerClass))
                        .visit(Advice.to(FoliageColorizerAdvice.class).on(ElementMatchers.takesArguments(
                                double.class, double.class
                        )))
                        .make()
                        .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            } catch (ClassNotFoundException ex) {
                //
            }
        }

        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().waterColorizerClass != null) {
            try {
                new ByteBuddy()
                        .redefine(ClassLoader.getSystemClassLoader().loadClass(LegacyGameManager.getVersion().waterColorizerClass))
                        .visit(Advice.to(WaterColorizerAdvice.class).on(ElementMatchers.takesArguments(
                                double.class, double.class
                        )))
                        .make()
                        .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            } catch (ClassNotFoundException ex) {
                // If the lib isn't loaded the version must not need it, no need to patch it.
            }
        }
    }

    public static void updateColorizers() {
        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().grassColorizerClass != null)
            GrassColorizerAdvice.updateColorizer();
        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().foliageColorizerClass != null)
            FoliageColorizerAdvice.updateColorizer();
        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().waterColorizerClass != null)
            WaterColorizerAdvice.updateColorizer();
    }
}
