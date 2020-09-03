package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.asm.Advice;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.UUID;

public class URLConstructAdvice {

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(0) String url) {
        try {
            if (url.isEmpty() || url.startsWith("file:"))
                return;
//            else
//                System.out.println("Original URL: " + url);

            // The string have to be copied here as this method is used before any class is loaded,
            // and catching class not founds doesn't here for some reason.

            for(String replaceHost : new String[] {
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
                if(url.contains(replaceHost)) {
                    Field f = String.class.getDeclaredField("value");
                    f.setAccessible(true);
                    f.set(url, url.replace(replaceHost, Globals.API_HOSTNAME).toCharArray());
                    f.set(url, url.replace("https", "http").toCharArray());

//                    System.out.println("New URL: " + url);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (NoClassDefFoundError error) {
            error.printStackTrace();
            // ionore
        }
    }
}