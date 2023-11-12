package gg.codie.mineonline.patches.minecraft;

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
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            // If the lib isn't loaded the version must not need it, no need to patch it.
        }
    }
}
