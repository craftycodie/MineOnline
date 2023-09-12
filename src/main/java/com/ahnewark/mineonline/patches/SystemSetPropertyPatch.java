package com.ahnewark.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

public class SystemSetPropertyPatch {
    public static void banNativeChanges() {
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(System.class)
                .visit(Advice.to(SystemSetPropertyAdvice.class).on(ElementMatchers.named("setProperty").and(ElementMatchers.takesArguments(
                        String.class,
                        String.class
                ))))
                .make()
                .load(System.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
