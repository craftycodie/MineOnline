package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.asm.Advice;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;

public class HttpURLConnectionGetResponseCodeAdvice {
    @Advice.OnMethodExit
    static void intercept(@Advice.This HttpURLConnection thisObj, @Advice.Return(readOnly = false) int returnCode) {
        if (thisObj.getURL().toString().contains("/MinecraftSkins/") || thisObj.getURL().toString().contains("/skin/")) {
            try {
                String url = thisObj.getURL().toString();
                String username = (url.contains("/MinecraftSkins/")
                        ? url.substring(url.indexOf("/MinecraftSkins/"))
                        : url.substring(url.indexOf("/skin/")))
                        .replace("/MinecraftSkins/", "")
                        .replace("/skin/", "")
                        .replace(".png", "");

                Class skinUtilsClass = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.utils.SkinUtils");
                Method getUserSkin = skinUtilsClass.getMethod("getUserSkin", String.class);

                Class TextureHelper = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.gui.textures.TextureHelper");
                Method convertModernSkin = TextureHelper.getMethod("convertModernSkin", ClassLoader.getSystemClassLoader().loadClass("org.json.JSONObject"));

                Object skin = getUserSkin.invoke(null, username);

                if(skin == null) {
                    returnCode = 404;
                    return;
                } else returnCode = 200;

                Field inputStreamField = thisObj.getClass().getDeclaredField("inputStream");
                inputStreamField.setAccessible(true);

                inputStreamField.set(thisObj, convertModernSkin.invoke(null, skin));
            } catch (Exception ex) {
                if (Globals.DEV)
                    ex.printStackTrace();

                returnCode = 404;
            }
        } else if (thisObj.getURL().toString().endsWith("/MinecraftResources/") || thisObj.getURL().toString().endsWith("/resources/")) {
            // Sounds are downloaded by MineOnline so we don't need to check for updates in game.
            returnCode = 404;
        }
    }
}
