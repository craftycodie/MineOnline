package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.asm.Advice;

public class URLConstructAdvice {
    public static String updateURL;
    public static String serverlistAddress;
    public static String serverlistPort;

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0, readOnly = false) String url) {
        try {
            if (url == null || url.isEmpty() || url.startsWith("file:"))
                return;

            // Dont mess with document base urls. These need to stay the same.
            if (url.equals("http://www.minecraft.net:80/game/") || url.equals("http://www.minecraft.net/game/")) {
                return;
            }

            boolean DEV = (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.Globals").getField("DEV").get(null);

            if(DEV) {
                System.out.println("Old URL: " + url);
            }

            if (url.contains("/heartbeat.jsp")) {
                String serverlistAddress = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.URLConstructAdvice").getField("serverlistAddress").get(null);
                String serverlistPort = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.URLConstructAdvice").getField("serverlistPort").get(null);

                if (serverlistPort != null && !serverlistPort.isEmpty()) {
                    if(url.contains("?port=")) {
                        String port = url.substring(url.indexOf("port=") + 3);
                        url = url.replace("port=" + port, "port=" + serverlistPort);
                    } else {
                        url = url + "?port=" + serverlistPort;
                    }
                }
                if (serverlistAddress != null && !serverlistAddress.isEmpty()) {
                    if(url.contains("&ip=")) {
                        String ip = url.substring(url.indexOf("ip=") + 3);
                        url = url.replace("ip=" + ip, "ip=" + serverlistAddress);
                    } else {
                        // Append "ip=" in case no IP was provided.
                        url = url + "&ip=" + serverlistAddress;
                    }
                }

            }

            String updateUrl = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.URLConstructAdvice").getField("updateURL").get(null);
            String oldUpdateUrl = "http://s3.amazonaws.com/MinecraftDownload/minecraft.jar";


            if (updateUrl != null && url.contains(oldUpdateUrl)) {
                url = updateUrl;
            } else if (url.contains("launcher.mojang.com/v1/objects/")) {
                // Quick patch to allow launchers to pull from this endpoint.
            } else {
                for (String replaceHost : new String[]{
                        "textures.minecraft.net",
                        "pc.realms.minecraft.net",
                        "www.minecraft.net:-1",
                        "skins.minecraft.net",
                        "session.minecraft.net",
                        "login.minecraft.net",
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
                        url = url.replace(replaceHost, Globals.API_HOSTNAME);
                        url = url.replace("https://", Globals.API_PROTOCOL);
                        url = url.replace("http://", Globals.API_PROTOCOL);
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