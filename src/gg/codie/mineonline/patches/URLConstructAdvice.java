package gg.codie.mineonline.patches;

import gg.codie.minecraft.api.SessionServer;
import gg.codie.minecraft.skins.SkinUtils;
import gg.codie.mineonline.Globals;
import gg.codie.mineonline.Session;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

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
            } else if (url.contains("/game/joinserver.jsp")) {
                Class sessionClass = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.Session");
                Object session = sessionClass.getField("session").get(null);

                Class sessionServerClass = ClassLoader.getSystemClassLoader().loadClass("gg.codie.minecraft.api.SessionServer");
                String serverId = url.substring(url.indexOf("&serverId=") + 10);

                boolean validJoin = (boolean)sessionServerClass.getMethod("joinGame", String.class, String.class, String.class).invoke(
                        null,
                        sessionClass.getMethod("getSessionToken").invoke(session),
                        sessionClass.getMethod("getUuid").invoke(session),
                        serverId
                );

                if (validJoin)
                    url = ClassLoader.getSystemResource("ok").toString();
                else // Just something to make it error.
                    url = "";
            } else if (url.contains("/game/checkserver.jsp")) {
                url = url.replace("http://www.minecraft.net/game/checkserver.jsp?user=", "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=");
            } else if ((url.contains("/MinecraftSkins/") || url.contains("/skin/")) && url.contains(".png")) {
                String username = (url.contains("/MinecraftSkins/")
                        ? url.substring(url.indexOf("/MinecraftSkins/"))
                        : url.substring(url.indexOf("/skin/")))
                        .replace("/MinecraftSkins/", "")
                        .replace("/skin/", "")
                        .replace(".png", "");

                Class skinUtilsClass = ClassLoader.getSystemClassLoader().loadClass("gg.codie.minecraft.skins.SkinUtils");
                Method findSkinURLForUsername = skinUtilsClass.getMethod("findSkinURLForUsername", String.class);

                url = (String)findSkinURLForUsername.invoke(null, username);
            } else if (url.contains("/MinecraftCloaks/") && url.contains(".png")) {
                String username = url.substring(url.indexOf("/MinecraftCloaks/"))
                        .replace("/MinecraftCloaks/", "")
                        .replace(".png", "");

                Class skinUtilsClass = ClassLoader.getSystemClassLoader().loadClass("gg.codie.minecraft.skins.SkinUtils");
                Method findCloakURLForUsername = skinUtilsClass.getMethod("findCloakURLForUsername", String.class);

                url = (String)findCloakURLForUsername.invoke(null, username);
            } else if (url.contains("/cloak/get.jsp?user=")) {
                String username = url.substring(url.indexOf("/cloak/get.jsp?user="))
                        .replace("/cloak/get.jsp?user=", "");

                Class skinUtilsClass = ClassLoader.getSystemClassLoader().loadClass("gg.codie.minecraft.skins.SkinUtils");
                Method findCloakURLForUsername = skinUtilsClass.getMethod("findCloakURLForUsername", String.class);

                url = (String)findCloakURLForUsername.invoke(null, username);
            } else {
                for (String replaceHost : new String[]{
                        "textures.minecraft.net",
                        "www.minecraft.net:-1",
                        "skins.minecraft.net",
                        "session.minecraft.net",
                        "authenticate.minecraft.net",
                        "assets.minecraft.net",
                        "mcoapi.minecraft.net",
                        "www.minecraft.net",
                        "minecraft.net",
                        "s3.amazonaws.com",

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