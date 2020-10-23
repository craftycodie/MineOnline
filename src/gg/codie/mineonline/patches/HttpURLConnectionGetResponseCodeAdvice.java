package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.asm.Advice;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

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
                Method findSkinURLForUsername = skinUtilsClass.getMethod("findSkinURLForUsername", String.class);

                Class TextureHelper = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.gui.rendering.TextureHelper");
                Method convertModernSkin = TextureHelper.getMethod("convertModernSkin", InputStream.class);

                url = (String) findSkinURLForUsername.invoke(null, username);

                if(url == null) {
                    returnCode = 404;
                    return;
                } else returnCode = 200;

                Field inputStreamField = thisObj.getClass().getDeclaredField("inputStream");
                inputStreamField.setAccessible(true);

                inputStreamField.set(thisObj, convertModernSkin.invoke(null, new URL(url).openStream()));
            } catch (Exception ex) {
                if (Globals.DEV)
                    ex.printStackTrace();

                returnCode = 404;
            }
        } else if (thisObj.getURL().toString().contains("/MinecraftResources/custom/") || thisObj.getURL().toString().contains("/resources/custom/")) {
            returnCode = 404;
        }
    }
}
