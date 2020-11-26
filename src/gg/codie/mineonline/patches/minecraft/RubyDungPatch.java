package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.patches.ReturnTrueAdvice;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class RubyDungPatch {
    public static void getRubyDungInstance(String rubyDungClassName) {
        try {
            new ByteBuddy()
                    .redefine(ClassLoader.getSystemClassLoader().loadClass(rubyDungClassName))
                    .visit(Advice.to(RubyDungConstructorAdvice.class).on(ElementMatchers.isConstructor()))
                    .visit(Advice.to(ReturnTrueAdvice.class).on(ElementMatchers.named("isSignatureValid")))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            // If the lib isn't loaded the version must not need it, no need to patch it.
        }
    }
}
