package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

public class StringPatch {
    public static void enableStringPatch(String versionString) {
        StringCharAtAdvice.versionString = versionString;
        StringToCharArrayAdvice.versionString = versionString;
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(String.class)
                // Replace version strings with whitespace.
                .visit(Advice.to(StringCharAtAdvice.class).on(ElementMatchers.named("charAt").and(ElementMatchers.takesArguments(
                        int.class
                ))))
                // Replace emoji names with emojis.
                .visit(Advice.to(StringToCharArrayAdvice.class).on(ElementMatchers.named("toCharArray")))
                .make()
                .load(String.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
