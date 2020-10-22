package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.net.URL;

public class URLPatch {
    public static void redefineURL() {
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(URL.class)
                .visit(Advice.to(URLConstructAdvice.class).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(
                        String.class
                ))))
                .visit(Advice.to(URLContextConstructAdvice.class).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(
                        URL.class,
                        String.class
                ))))
                .make()
                .load(URL.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }

    public static void redefineURL(String updateURL) {
        URLConstructAdvice.updateURL = updateURL;
        redefineURL();
    }

    public static void redefineURL(String serverlistAddress, String serverlistPort) {
        URLConstructAdvice.serverlistAddress = serverlistAddress;
        URLConstructAdvice.serverlistPort = serverlistPort;
        redefineURL();
    }
}
