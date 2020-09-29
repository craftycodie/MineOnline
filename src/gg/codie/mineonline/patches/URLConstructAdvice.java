package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;

public class URLConstructAdvice {
    public static String updateURL;
    public static String serverlistAddress;
    public static String serverlistPort;

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(0) String url) {
        try {
            if (url.isEmpty() || url.startsWith("file:"))
                return;

            // Dont mess with document base urls. These need to stay the same.
            if (url.equals("http://www.minecraft.net:80/game/") || url.equals("http://www.minecraft.net/game/")) {
                return;
            }

            boolean DEV = (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.Globals").getField("DEV").get(null);

            if(DEV) {
                System.out.println("Old URL: " + url);
            }

            Field f = String.class.getDeclaredField("value");
            f.setAccessible(true);

            if (url.contains("/heartbeat.jsp")) {
                String serverlistAddress = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.URLConstructAdvice").getField("serverlistAddress").get(null);
                String serverlistPort = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.URLConstructAdvice").getField("serverlistPort").get(null);

                if (serverlistAddress != null && !serverlistAddress.isEmpty()) {
                    if(url.contains("?ip=")) {
                        String ip = url.substring(url.indexOf("ip=") + 3);
                        f.set(url, url.replace("ip=" + ip, "ip=" + serverlistAddress).toCharArray());
                    } else {
                        // Append "ip=" in case no IP was provided.
                        f.set(url, (url + "?ip=" + serverlistAddress).toCharArray());
                    }
                }
                if (serverlistPort != null && !serverlistPort.isEmpty()) {
                    String port = url.substring(url.indexOf("port=") + 5);
                    port = port.substring(0, port.indexOf("&"));
                    f.set(url, url.replace("port=" + port, "port=" + serverlistPort).toCharArray());
                }
            }

            String updateUrl = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.URLConstructAdvice").getField("updateURL").get(null);
            String oldUpdateUrl = "http://s3.amazonaws.com/MinecraftDownload/minecraft.jar";


            if (updateUrl != null && url.contains(oldUpdateUrl)) {
                f.set(url, updateUrl.toCharArray());
            } else if (url.contains("launcher.mojang.com/v1/objects/")) {
                // Quick patch to allow launchers to pull from this endpoint.
            } else {
                for (String replaceHost : new String[]{
                        "textures.minecraft.net",
                        "pc.realms.minecraft.net",
                        "www.minecraft.net:-1",
                        "skins.minecraft.net",
                        "session.minecraft.net",
                        "realms.minecraft.net",
                        "assets.minecraft.net",
                        "mcoapi.minecraft.net",
                        "snoop.minecraft.net",
                        "www.minecraft.net",
                        "resources.download.minecraft.net",
                        "libraries.minecraft.net",
                        "minecraft.net",
                        "s3.amazonaws.com",
                        "api.mojang.com",
                        "authserver.mojang.com",
                        "account.mojang.com",
                        "sessionserver.mojang.com",
                        "launchermeta.mojang.com",
                        "mojang.com",
                        "aka.ms",

                        // for mods
                        "banshee.alex231.com",
                        "mcauth-alex231.rhcloud.com",
                }) {
                    if (url.contains(replaceHost)) {
                        f.setAccessible(true);
                        f.set(url, url.replace(replaceHost, Globals.API_HOSTNAME).toCharArray());
                        f.set(url, url.replace("https", "http").toCharArray());
                    }
                }
            }

            if(DEV) {
                System.out.println("New URL: " + url);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}