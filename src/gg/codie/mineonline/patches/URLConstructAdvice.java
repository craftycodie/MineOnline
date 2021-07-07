package gg.codie.mineonline.patches;

import gg.codie.mineonline.LauncherFiles;
import net.bytebuddy.asm.Advice;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class URLConstructAdvice {
    public static String updateURL;
    public static String serverlistAddress;
    public static String serverlistPort;

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0, readOnly = false) String url) {
        try {
            if (url == null || url.isEmpty() || url.startsWith("file:")) {
                return;
            }

            // Dont mess with document base urls. These need to stay the same.
            if (url.equals("http://www.minecraft.net:80/game/") || url.equals("http://www.minecraft.net/game/")) {
                return;
            }

            // Release URLS that don't need moving.
            if (url.contains("pc.realms."))
                return;

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
                        sessionClass.getMethod("getAccessToken").invoke(session),
                        sessionClass.getMethod("getUuid").invoke(session),
                        serverId
                );

                if (validJoin)
                    url = ClassLoader.getSystemResource("responses/ok").toString();
                else // Just something to make it error.
                    url = ClassLoader.getSystemResource("responses/bad login").toString();
            } else if (url.contains("/game/checkserver.jsp")) {
                String username = null;
                String serverId = null;
                String ip = null;

                String[] args = url
                        .substring(url.lastIndexOf("?") + 1)
                        .split("&");
                for (String arg : args) {
                    String[] keyValue = arg.split("=");

                    if (keyValue[0].equals("user"))
                        username = keyValue[1];
                    else if (keyValue[0].equals("serverId"))
                        serverId = keyValue[1];
                    else if (keyValue[0].equals("ip"))
                        ip = keyValue[1];
                }

                if (username == null || serverId == null) {
                    url = "";
                    return;
                }

                Class sessionServerClass = ClassLoader.getSystemClassLoader().loadClass("gg.codie.minecraft.api.SessionServer");

                boolean validJoin = (boolean)sessionServerClass.getMethod("hasJoined", String.class, String.class, String.class).invoke(
                        null,
                        username,
                        serverId,
                        ip
                );

                if (validJoin)
                    url = ClassLoader.getSystemResource("responses/YES").toString();
                else // Just something to make it error.
                    url = ClassLoader.getSystemResource("responses/bad login").toString();
            } else if (url.contains("/login/session.jsp")) {
                url = ClassLoader.getSystemResource("responses/ok").toString();
            } else if (url.contains("/game/?n=")) {
                url = ClassLoader.getSystemResource("responses/42069").toString();
            } else if (url.contains("/haspaid.jsp")) {
                url = ClassLoader.getSystemResource("responses/true").toString();
            } else if (url.endsWith("/resources/")) {
                String resourcesVersion = (String) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.FilePatch").getField("resourcesVersion").get(null);
                Object resourceDownloaderService = ClassLoader.getSystemClassLoader().loadClass("gg.codie.minecraft.resources.ResourcesIndexService").newInstance();
                url = ((URL)ClassLoader.getSystemClassLoader().loadClass("gg.codie.minecraft.resources.ResourcesIndexService").getMethod("getResourcesIndex", String.class).invoke(resourceDownloaderService, resourcesVersion)).toString();
            } else if ((url.contains("/MinecraftSkins/") || url.contains("/skin/")) && url.contains(".png")) {
                // Handled by UrlConnectionPatch
            } else if (url.contains("textures.minecraft.net")) {
                // This is where official skins are fetched, so don't mod it!
            } else if (url.contains("/MinecraftCloaks/") && url.contains(".png")) {
                String username = url.substring(url.indexOf("/MinecraftCloaks/"))
                        .replace("/MinecraftCloaks/", "")
                        .replace(".png", "");

                Class skinUtilsClass = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.utils.SkinUtils");
                Method findCloakURLForUsername = skinUtilsClass.getMethod("findCloakURLForUsername", String.class);

                url = (String)findCloakURLForUsername.invoke(null, username);
            } else if (url.contains("/cloak/get.jsp?user=")) {
                String username = url.substring(url.indexOf("/cloak/get.jsp?user="))
                        .replace("/cloak/get.jsp?user=", "");

                Class skinUtilsClass = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.utils.SkinUtils");
                Method findCloakURLForUsername = skinUtilsClass.getMethod("findCloakURLForUsername", String.class);

                url = (String) findCloakURLForUsername.invoke(null, username);

            } else if (url.contains("/listmaps.jsp?user=") || url.contains("/level/save.html") || url.contains("/level/load.html")) {
                url = url.replace("http", "mineonline");
            }

            if(DEV) {
                System.out.println("New URL: " + url);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}