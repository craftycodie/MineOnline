package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.patches.ReturnTrueAdvice;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

import java.net.URLClassLoader;

public class PropertiesSignaturePatch {
    public static void redefineIsSignatureValid(URLClassLoader classLoader) {
        try {
            new ByteBuddy()
                    .redefine(classLoader.loadClass("com.mojang.authlib.properties.Property"))
                    .visit(Advice.to(ReturnTrueAdvice.class).on(ElementMatchers.named("hasSignature")))
                    .visit(Advice.to(ReturnTrueAdvice.class).on(ElementMatchers.named("isSignatureValid")))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            // If the lib isn't loaded the version must not need it, no need to patch it.
        }
    }
}
