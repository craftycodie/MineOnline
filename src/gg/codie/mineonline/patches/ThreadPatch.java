package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

public class ThreadPatch {
    public static void interruptJoins() {
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(Thread.class)
                .visit(Advice.to(ThreadJoinAdvice.class).on(ElementMatchers.named("join").and(ElementMatchers.takesArguments(
                        long.class
                ))))
                .make()
                .load(Thread.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
