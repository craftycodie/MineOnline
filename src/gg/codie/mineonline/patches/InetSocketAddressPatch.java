package gg.codie.mineonline.patches;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;

import java.net.InetSocketAddress;

public class InetSocketAddressPatch {
    public static void allowCustomServers(String serverAddress, String serverPort) {
        InetSocketAddressConstructAdvice.serverIP = serverAddress;
        InetSocketAddressConstructAdvice.serverPort = Integer.parseInt(serverPort);

        new ByteBuddy()
                .with(Implementation.Context.Disabled.Factory.INSTANCE)
                .redefine(InetSocketAddress.class)
                .visit(Advice.to(InetSocketAddressConstructAdvice.class).on(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(
                        String.class,
                        int.class
                ))))
                .make()
                .load(InetSocketAddress.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
}
