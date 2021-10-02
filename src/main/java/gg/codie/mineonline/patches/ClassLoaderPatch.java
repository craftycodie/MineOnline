package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

public class ClassLoaderPatch {
    public static void allowUnsignedCode() {
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(ClassLoader.class)
                .visit(Advice.to(ClassLoaderCheckCertsAdvice.class).on(ElementMatchers.named("checkCerts")))
                .make()
                .load(ClassLoader.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
