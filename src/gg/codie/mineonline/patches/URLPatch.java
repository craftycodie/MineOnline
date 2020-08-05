package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.net.URL;

public class URLPatch {
    public static void redefineURL() {
        new ByteBuddy()
            .with(Implementation.Context.Disabled.Factory.INSTANCE)
            .redefine(URL.class)
            .visit(Advice.to(URLConstructAdvice.class).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(
                    String.class
            ))))
            .make()
            .load(URL.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
