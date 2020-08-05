package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class PropertiesSignaturePatch {
    public static void redefineIsSignatureValid() throws Exception {
        new ByteBuddy()
            .redefine(PropertiesSignaturePatch.class.getClassLoader().loadClass("com.mojang.authlib.properties.Property"))
            .visit(Advice.to(ReturnTrueAdvice.class).on(ElementMatchers.named("hasSignature")))
            .visit(Advice.to(ReturnTrueAdvice.class).on(ElementMatchers.named("isSignatureValid")))
            .make()
            .load(Class.forName("com.mojang.authlib.properties.Property").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
