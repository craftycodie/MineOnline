package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class GameProfileUUIDPatch {
    public static void redefineGetId() throws Exception {
        new ByteBuddy()
            .redefine(URLPatch.class.getClassLoader().loadClass("com.mojang.authlib.GameProfile"))
            .visit(Advice.to(GameProfileGetIdAdvice.class).on(ElementMatchers.named("getId")))
            //.method(ElementMatchers.named("getId"))
            //.intercept(FixedValue.value(UUID.nameUUIDFromBytes(Session.session.getUsername().getBytes())))
            .make()
            .load(Class.forName("com.mojang.authlib.GameProfile").getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
