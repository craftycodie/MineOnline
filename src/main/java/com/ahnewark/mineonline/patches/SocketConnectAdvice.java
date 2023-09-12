package com.ahnewark.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class SocketConnectAdvice {
    public static InetAddress serverAddress;
    public static int serverPort;

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0) SocketAddress address) {
        if (!(address instanceof InetSocketAddress))
            return;
        InetSocketAddress inetAddress = (InetSocketAddress) address;
        System.out.println("Joined Server " + inetAddress.getHostName() + ":" + inetAddress.getPort());
        try {
            ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.SocketConnectAdvice").getField("serverAddress").set(null, inetAddress.getAddress());
            ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.SocketConnectAdvice").getField("serverPort").set(null, inetAddress.getPort());
            Class presenceClazz = ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.discord.DiscordRPCHandler");
            presenceClazz.getMethod("updateServer", new Class[] { String.class, String.class }).invoke(null, inetAddress.getAddress().getHostAddress(), "" + inetAddress.getPort());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
