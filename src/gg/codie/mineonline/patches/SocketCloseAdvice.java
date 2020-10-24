package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.net.InetAddress;
import java.net.Socket;

public class SocketCloseAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Socket thisObject) {
        try {
            Class constructClazz = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.SocketConstructAdvice");
            InetAddress serverAddress = (InetAddress)constructClazz.getField("serverAddress").get(null);
            int serverPort = (int)constructClazz.getField("serverPort").get(null);

            if (thisObject.getInetAddress() == serverAddress && thisObject.getPort() == serverPort) {
                constructClazz.getField("serverAddress").set(null, null);
                constructClazz.getField("serverPort").set(null, -1);
                System.out.println("Left Server " + thisObject.getInetAddress().getHostAddress() + ":" + thisObject.getPort());
                Class presenceClazz = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.discord.DiscordRPCHandler");
                presenceClazz.getMethod("updateServer", new Class[]{ String.class, String.class }).invoke(null, null, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
