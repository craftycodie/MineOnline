package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

public class ClassPatch {
    public static String texturePack;

    public static void init() {
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(Class.class)
                .visit(Advice.to(ClassGetResourceAdvice.class).on(ElementMatchers.named("getResourceAsStream").and(ElementMatchers.takesArguments(
                        String.class
                ))))
                .make()
                .load(Class.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
