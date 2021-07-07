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
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0, readOnly = false) String url) {
        try {
            if (url == null || url.isEmpty() || url.startsWith("file:")) {
                return;
            }

            boolean DEV = (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.Globals").getField("DEV").get(null);

            if(DEV) {
                System.out.println("Old URL: " + url);
            }

            if (url.contains("/game/joinserver.jsp")) {
                url = url.replace("http", "mineonline");
            } else if (url.contains("/login/session.jsp") || url.contains("/game/?n=") || url.contains("/haspaid.jsp")) {
                url = url.replace("http", "mineonline");
            } else if (url.endsWith("/resources/") || url.endsWith("/MinecraftResources/")) {
                url = url.replace("http", "mineonline");
            } else if ((url.contains("/MinecraftSkins/") || url.contains("/skin/")) && url.contains(".png")) {
                url = url.replace("http", "mineonline");
            } else if ((url.contains("/MinecraftCloaks/") && url.contains(".png")) || url.contains("/cloak/get.jsp?user=")) {
                url = url.replace("http", "mineonline");
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