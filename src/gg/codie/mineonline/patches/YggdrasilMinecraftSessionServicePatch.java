package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class YggdrasilMinecraftSessionServicePatch {
    public static void allowMineonlineSkins() {
        try {
            new ByteBuddy()
                    .redefine(YggdrasilMinecraftSessionServicePatch.class.getClassLoader().loadClass("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService"))
                    .visit(Advice.to(ReturnTrueAdvice.class).on(ElementMatchers.named("isWhitelistedDomain").and(ElementMatchers.takesArguments(String.class))))
                    .make()
                    .load(Class.forName("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } catch (ClassNotFoundException ex) {
            // If the lib isn't loaded the version must not need it, no need to patch it.
        }
    }
}
