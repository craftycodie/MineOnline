package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class ByteBufferPatch {
    // Classic 0.0.15a always sends the username "guest", so this patch swaps "guest" with the player's name.
    public static void enableC0015aUsernames(String username) {
        ByteBufferPutAdvice.username = username;

        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(ByteBuffer.class)
                .visit(Advice.to(ByteBufferPutAdvice.class).on(ElementMatchers.named("put").and(ElementMatchers.takesArguments(
                        byte[].class
                ))))
                .make()
                .load(ByteBuffer.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
