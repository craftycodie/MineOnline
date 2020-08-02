package gg.codie.mineonline;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;

class URLAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Origin Object url, @Advice.Argument(1) String host) {
        try {
            if(!host.isEmpty())
                System.out.println("HOST: " + host);
            else
                return;

//            JSONObject settings = (JSONObject)Class.forName(Settings.class.getCanonicalName()).getDeclaredField("settings").get(null);
//
//            Settings.loadSettings();
//            System.out.println("Settings: " + settings);
            for(String replaceHost : new String[] {
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
                    "mcauth-alex231.rhcloud.com"
            }) {
//                System.out.println(replaceHost);
                if(host.equals(replaceHost)) {
                    Field f = String.class.getDeclaredField("value");
                    f.setAccessible(true);
                    f.set(host, host.replace(replaceHost, Globals.API_HOSTNAME).toCharArray());

                    System.out.println("Replaced." );
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