package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

public class StringPatch {
    public static void hideVersionStrings(String versionString) {
        StringCharAtAdvice.versionString = versionString;
        StringToCharArrayAdvice.versionString = versionString;
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(String.class)
                .visit(Advice.to(StringCharAtAdvice.class).on(ElementMatchers.named("charAt").and(ElementMatchers.takesArguments(
                        int.class
                ))))
                .visit(Advice.to(StringToCharArrayAdvice.class).on(ElementMatchers.named("toCharArray")))
                .make()
                .load(String.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }

    public static void checkservResponsePatch() {
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(String.class)
                .visit(Advice.to(StringEqualsAdvice.class).on(ElementMatchers.named("equals").and(ElementMatchers.takesArguments(Object.class))))
                .make()
                .load(String.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
