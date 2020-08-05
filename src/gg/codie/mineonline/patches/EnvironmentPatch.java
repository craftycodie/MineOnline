package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

public class EnvironmentPatch {
    public static void redefineEnvironment() throws Exception {
        new ByteBuddy()
                .redefine(URLPatch.class.getClassLoader().loadClass("com.mojang.authlib.Environment"))
                .method(ElementMatchers.named("getName"))
                .intercept(FixedValue.value("PROD"))
                .method(ElementMatchers.named("getAuthHost"))
                .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
                .method(ElementMatchers.named("getAccountsHost"))
                .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
                .method(ElementMatchers.named("getSessionHost"))
                .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
                .visit(Advice.to(EnvironmentConstructAdvice.class).on(ElementMatchers.named("create").and(ElementMatchers.takesArguments(
                        String.class, String.class, String.class, String.class
                ))))
                .make()
                .load(Class.forName("com.mojang.authlib.Environment").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
