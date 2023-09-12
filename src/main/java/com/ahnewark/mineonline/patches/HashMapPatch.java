package com.ahnewark.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.HashMap;

public class HashMapPatch {
    public static void init() {
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(HashMap.class)
                .visit(Advice.to(HashMapPutAdvice.class).on(ElementMatchers.named("put")))
                .make()
                .load(HashMap.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
