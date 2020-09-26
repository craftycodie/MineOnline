package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

public class SocketPatch {
    public static void watchSockets() {
        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(Socket.class)
                .visit(Advice.to(SocketConstructAdvice.class).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(
                        InetAddress.class,
                        int.class
                ))))
                .visit(Advice.to(SocketCloseAdvice.class).on(ElementMatchers.named("close")))
                .make()
                .load(Socket.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
