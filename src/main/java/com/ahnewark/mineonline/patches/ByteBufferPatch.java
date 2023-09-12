package com.ahnewark.mineonline.patches;

import com.ahnewark.mineonline.Session;
import com.ahnewark.mineonline.client.LegacyGameManager;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.nio.ByteBuffer;

public class ByteBufferPatch {
    // Classic 0.0.15a always sends the username "guest", so this patch swaps "guest" with the player's name.
    public static void init() {
        ByteBufferPutAdvice.username = Session.session.getUsername();

        if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().useUsernamesPatch) {
            new ByteBuddy()
                    .with(Implementation.Context.Disabled.Factory.INSTANCE)
                    .redefine(ByteBuffer.class)
                    .visit(Advice.to(ByteBufferPutAdvice.class).on(ElementMatchers.named("put").and(ElementMatchers.takesArguments(
                            byte[].class
                    ))))
                    .visit(Advice.to(ByteBufferAllocateDirectAdvice.class).on(ElementMatchers.named("allocateDirect").and(ElementMatchers.takesArguments(int.class))))
                    .make()
                    .load(ByteBuffer.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        } else {
            new ByteBuddy()
                    .with(Implementation.Context.Disabled.Factory.INSTANCE)
                    .redefine(ByteBuffer.class)
                    .visit(Advice.to(ByteBufferAllocateDirectAdvice.class).on(ElementMatchers.named("allocateDirect").and(ElementMatchers.takesArguments(int.class))))
                    .make()
                    .load(ByteBuffer.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        }
    }
}
