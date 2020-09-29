package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;

public class URLConstructAdvice {
    public static String updateURL;

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(0) String url) {
        try {
            if (url.isEmpty() || url.startsWith("file:"))
                return;

            String updateUrl = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.URLConstructAdvice").getField("updateURL").get(null);
            String oldUpdateUrl = "http://s3.amazonaws.com/MinecraftDownload/minecraft.jar";

            Field f = String.class.getDeclaredField("value");

            if (updateUrl != null && url.contains(oldUpdateUrl)) {
                f.setAccessible(true);
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}