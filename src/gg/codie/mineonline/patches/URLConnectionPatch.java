package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.net.HttpURLConnection;

public class URLConnectionPatch {
    public static void patchResponses() {
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(HttpURLConnection.class)
                .visit(Advice.to(HttpURLConnectionGetResponseCodeAdvice.class).on(ElementMatchers.named("getResponseCode")))
                .make()
                .load(HttpURLConnection.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
