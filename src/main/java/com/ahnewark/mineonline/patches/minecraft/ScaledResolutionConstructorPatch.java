package com.ahnewark.mineonline.patches.minecraft;

import com.ahnewark.mineonline.Settings;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class ScaledResolutionConstructorPatch {
    public static void useGUIScale(String scaledResolutionClassName) {
        try {
            ScaledResolutionConstructorAdvice.guiScale = Settings.singleton.getGUIScale().getIntValue();

            new ByteBuddy()
                    .redefine(Class.forName(scaledResolutionClassName))
                    .visit(Advice.to(ScaledResolutionConstructorAdvice.class).on(ElementMatchers.isConstructor()))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            // If the lib isn't loaded the version must not need it, no need to patch it.
        }
    }
}
