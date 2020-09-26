package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.net.InetAddress;

public class SocketConstructAdvice {
    public static InetAddress serverAddress;
    public static int serverPort;

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0) InetAddress ip, @Advice.Argument(value = 1) int port) {
       System.out.println("Joined Server " + ip.getHostAddress() + ":" + port);
       try {
           ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.SocketConstructAdvice").getField("serverAddress").set(null, ip);
           ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.SocketConstructAdvice").getField("serverPort").set(null, port);
           Class presenceClazz = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.discord.DiscordPresence");
           presenceClazz.getMethod("updateServer", new Class[] { InetAddress.class, String.class }).invoke(null, ip, "" + port);
       } catch (Exception ex) {
           ex.printStackTrace();
       }
    }

}
