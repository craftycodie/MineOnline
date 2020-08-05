package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

public class YggdrasilEnvironmentPatch {
    public static void redefineYggdrasilEnvironment() throws Exception {
        new ByteBuddy()
                .redefine(URLPatch.class.getClassLoader().loadClass("com.mojang.authlib.yggdrasil.YggdrasilEnvironment"))
                .method(ElementMatchers.named("getName"))
                .intercept(FixedValue.value("MineOnline"))
                .method(ElementMatchers.named("getAuthHost"))
                .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
                .method(ElementMatchers.named("getAccountsHost"))
                .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
                .method(ElementMatchers.named("getSessionHost"))
                .intercept(FixedValue.value("http://" + Globals.API_HOSTNAME))
                .visit(Advice.to(YggdrasilEnvironmentConstructAdvice.class).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(
                        String.class, String.class, String.class
                ))))
                .make()
                .load(Class.forName("com.mojang.authlib.yggdrasil.YggdrasilEnvironment").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
