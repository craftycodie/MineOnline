package gg.codie.mineonline;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;

class URLConstructAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(0) String url) {
        try {
            if(!url.isEmpty() && !url.startsWith("file:"))
                System.out.println("HOST: " + url);
            else
                return;

//            JSONObject settings = (JSONObject)Class.forName(Settings.class.getCanonicalName()).getDeclaredField("settings").get(null);
//
//            Settings.loadSettings();
//            System.out.println("Settings: " + settings);
            for(String replaceHost : new String[] {
                    "pc.realms.minecraft.net",
                    "www.minecraft.net:-1",
                    "skins.minecraft.net",
                    "session.minecraft.net",
                    "realms.minecraft.net",
                    "assets.minecraft.net",
                    "mcoapi.minecraft.net",
                    "snoop.minecraft.net",
                    "minecraft.net",
                    "www.minecraft.net",
                    "s3.amazonaws.com",
                    "api.mojang.com",
                    "authserver.mojang.com",
                    "sessionserver.mojang.com",

                    "banshee.alex231.com",
                    "mcauth-alex231.rhcloud.com",
            }) {
//                System.out.println(replaceHost);
                if(url.contains(replaceHost)) {
                    Field f = String.class.getDeclaredField("value");
                    f.setAccessible(true);
                    f.set(url, url.replace(replaceHost, Globals.API_HOSTNAME).toCharArray());
                    f.set(url, url.replace("https", "http").toCharArray());

                    System.out.println("Replaced: " + url);
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