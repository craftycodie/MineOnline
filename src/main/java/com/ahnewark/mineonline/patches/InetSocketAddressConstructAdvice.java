package com.ahnewark.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.net.InetAddress;

public class InetSocketAddressConstructAdvice {
    public static String serverIP;
    public static int serverPort;
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0, readOnly = false) String ip, @Advice.Argument(value = 1, readOnly = false) int port) {
        if (ip.equals("79.136.77.240") && port == 5565) {
            try {
                ip = (String) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.InetSocketAddressConstructAdvice").getField("serverIP").get(null);
                port = (int) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.InetSocketAddressConstructAdvice").getField("serverPort").get(null);

                if (ip == null || ip.isEmpty())
                    ip = "240.0.0.0";
                else {
                    Class presenceClazz = ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.discord.DiscordRPCHandler");
                    presenceClazz.getMethod("updateServer", new Class[]{String.class, String.class}).invoke(null, InetAddress.getByName(ip).getHostAddress(), "" + port);
                }
            } catch (Exception ex) {
                System.err.println("Unable to join server.");
                ex.printStackTrace();
            }
        }

        try {
            if ((boolean) ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.Globals").getField("DEV").get(null)) {
                System.out.println("Connecting to " + ip + ":" + port);
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ex) {
            System.out.println("InetSocketAddressConstructorAdvice");
            ex.printStackTrace();
        }
    }
}
