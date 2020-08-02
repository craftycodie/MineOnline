package gg.codie.mineonline;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.net.URL;
import java.net.URLStreamHandler;

public class URLPatch {
    public static void redefineURL() {
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(URL.class)
                .visit(Advice.to(URLAdvice.class).on(ElementMatchers.named("set")))
                .make()
                .load(URL.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
