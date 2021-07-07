package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import net.bytebuddy.asm.Advice;
import org.lwjgl.Sys;

import javax.imageio.ImageIO;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.nio.file.Paths;
import java.util.Arrays;

public class HttpURLConnectionGetResponseCodeAdvice {
    public static boolean test = false;

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
        } else if (thisObj.getURL().toString().contains("/level/load.html?id=") && returnCode != 200) {
            try {
                String mineonlineWorldsFolder = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.LauncherFiles").getField("MINEONLINE_WORLDS_PATH").get(null);

                Field f = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.HttpURLConnectionGetResponseCodeAdvice").getDeclaredField("test");
                if ((Boolean)f.get(null)) return;
                else f.set(null, true);

                File dir = new File(mineonlineWorldsFolder);
                File[] directoryListing = dir.listFiles();
                if (directoryListing != null) {
                    for (File child : directoryListing) {
                        if (child.getName().endsWith(".mine") && child.getName().startsWith("2_")) {
//                            url = Paths.get(mineonlineWorldsFolder + child.getName()).toUri().toURL().toString();
//                            System.out.println(thisObj.getURL());

                            Field inputStreamField = thisObj.getClass().getDeclaredField("inputStream");
                            inputStreamField.setAccessible(true);
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            FileInputStream fileInputStream = new FileInputStream(mineonlineWorldsFolder + child.getName());
                            new DataOutputStream(os).writeUTF("ok");
                            while (fileInputStream.available() > 0) {
                                os.write(fileInputStream.read());
                            }
                            byte[] bytes = os.toByteArray();
                            InputStream inputStream = new ByteArrayInputStream(bytes);
//                            System.out.println(Arrays.toString(bytes));
                            inputStreamField.set(thisObj, inputStream);
                            System.out.println(thisObj.getClass());
                            returnCode = 200;

                            break;
                        }
                        // Do something with child
                    }
                }

                System.out.println(returnCode);
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
