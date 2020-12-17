package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;
import sun.net.www.protocol.file.FileURLConnection;

import java.net.HttpURLConnection;

public class URLConnectionPatch {
    public static void patchResponses() {
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(HttpURLConnection.class)
                .visit(Advice.to(HttpURLConnectionGetResponseCodeAdvice.class).on(ElementMatchers.named("getResponseCode")))
                .make()
                .load(HttpURLConnection.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(FileURLConnection.class)
                .visit(Advice.to(FileURLConnectionGetInputStreamAdvice.class).on(ElementMatchers.named("getInputStream")))
                .make()
                .load(FileURLConnection.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
