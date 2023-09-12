package com.ahnewark.mineonline.patches.minecraft;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class ItemRendererPatch {
    public static void useHDItems(String itemRendererClassName) {
        try {
            new ByteBuddy()
                    .redefine(Class.forName(itemRendererClassName))
                    .visit(Advice.to(ItemRendererAdvice.class).on(ElementMatchers.isPublic().and(ElementMatchers.takesArguments(2))))
                    .visit(Advice.to(ItemRendererAdvice.class).on(ElementMatchers.isPublic().and(ElementMatchers.takesArguments(1)).and(ElementMatchers.takesArgument(0, ElementMatchers.not(ElementMatchers.isPrimitive()))).and(ElementMatchers.named("a"))))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
