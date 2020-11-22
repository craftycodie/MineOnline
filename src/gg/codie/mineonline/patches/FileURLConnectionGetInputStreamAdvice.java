package gg.codie.mineonline.patches;

import gg.codie.mineonline.Globals;
import gg.codie.mineonline.LauncherFiles;
import net.bytebuddy.asm.Advice;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileURLConnectionGetInputStreamAdvice {
    @Advice.OnMethodExit
    static void intercept(@Advice.This FileURLConnection thisObj, @Advice.Return(readOnly = false) InputStream returnStream) {
        try {
            if (!thisObj.getURL().toString().endsWith(".ogg") && !thisObj.getURL().toString().endsWith(".mus"))
                return;
            System.out.println(thisObj.getURL());
            String soundPath = Paths.get(thisObj.getURL().toURI()).toString();
            String MINEONLINE_RESOURCES_PATH = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.LauncherFiles").getDeclaredField("MINEONLINE_RESOURCES_PATH").get(null);
            String MINECRAFT_TEXTURE_PACKS_PATH = (String)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.LauncherFiles").getDeclaredField("MINECRAFT_TEXTURE_PACKS_PATH").get(null);
            soundPath = soundPath.replace(MINEONLINE_RESOURCES_PATH, "");
            soundPath = soundPath.substring(soundPath.indexOf(File.separator) + 1).replace(File.separator, "/");
            Object settings = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.Settings").getField("singleton").get(null);
            String texturePack = ((String) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.Settings").getMethod("getTexturePack").invoke(settings));
//            soundPath = MINECRAFT_TEXTURE_PACKS_PATH + texturePack + "!" + soundPath;
//            URL newURL = new URL(Paths.get(soundPath).toUri().toURL().toString().replace("file:", "jar:"));

            System.out.println(soundPath);

            ZipFile texturePackZip = new ZipFile(MINECRAFT_TEXTURE_PACKS_PATH + texturePack);
            ZipEntry soundEntry = texturePackZip.getEntry(soundPath);
            if (soundEntry != null)
                returnStream = texturePackZip.getInputStream(soundEntry);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
