package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.asm.Advice;

import java.net.InetAddress;

public class InetSocketAddressConstructAdvice {
    public static String serverIP;
    public static int serverPort;
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0, readOnly = false) String ip, @Advice.Argument(value = 1, readOnly = false) int port) {
        if (ip.equals("79.136.77.240") && port == 5565) {
            try {
                ip = (String) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.InetSocketAddressConstructAdvice").getField("serverIP").get(null);
                port = (int) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.InetSocketAddressConstructAdvice").getField("serverPort").get(null);

                Class presenceClazz = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.discord.DiscordPresence");
                presenceClazz.getMethod("updateServer", new Class[] { String.class, String.class }).invoke(null, InetAddress.getByName(ip).getHostAddress(), "" + port);
            } catch (Exception ex) {
                System.err.println("Unable to join server.");
                ex.printStackTrace();
            }
        }

        if (Globals.DEV) {
            System.out.println("Connecting to " + ip  + ":" + port);
        }
    }
}
