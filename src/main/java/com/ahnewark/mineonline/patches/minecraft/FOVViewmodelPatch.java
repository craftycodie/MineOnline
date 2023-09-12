package com.ahnewark.mineonline.patches.minecraft;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class FOVViewmodelPatch {
    public static void fixViewmodelFOV(String entityRendererClassName, String viewModelFunctionName, String hurtEffectFunction, boolean leftHanded) {
        FOVViewmodelAdvice.leftHanded = leftHanded;
        try {
            if (hurtEffectFunction != null) {
                ClassicFOVViewmodelAdvice.callCount = -(2 - ClassicFOVViewmodelAdvice.callCount);

                new ByteBuddy()
                        .redefine(Class.forName(entityRendererClassName))
                        .visit(Advice.to(ClassicFOVViewmodelAdvice.class).on(ElementMatchers.named(hurtEffectFunction).and(ElementMatchers.takesArguments(
                                float.class
                        ))))
                        .make()
                        .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            } else {
                new ByteBuddy()
                        .redefine(Class.forName(entityRendererClassName))
                        .visit(Advice.to(FOVViewmodelAdvice.class).on(ElementMatchers.named(viewModelFunctionName).and(ElementMatchers.takesArguments(
                                float.class,
                                int.class
                        ))))
                        .make()
                        .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            }
        } catch (ClassNotFoundException ex) {
            // If the lib isn't loaded the version must not need it, no need to patch it.
        }
    }
}
