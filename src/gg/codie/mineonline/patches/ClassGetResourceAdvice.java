package gg.codie.mineonline.patches;

import gg.codie.mineonline.LauncherFiles;
import net.bytebuddy.asm.Advice;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassGetResourceAdvice {
    public static String texturePack;

    @Advice.OnMethodExit
    static void intercept(@Advice.Argument(0) String textureName, @Advice.Return(readOnly = false) InputStream inputStream) {
//        if (textureName.endsWith(".png"))
//            return;

        try {
            boolean DEV = (boolean) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.Globals").getField("DEV").get(null);

            if (DEV)
                System.out.println("Loading texture: " + textureName);

            String texturePack = (String) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.ClassGetResourceAdvice").getField("texturePack").get(null);
            String texturePacksPath = (String) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.LauncherFiles").getField("MINECRAFT_TEXTURE_PACKS_PATH").get(null);


            if (texturePack == null || texturePack.isEmpty())
                return;

            if (texturePack.toLowerCase().endsWith(".zip")) {
                ZipFile texturesZip = new ZipFile(texturePacksPath + texturePack);
                ZipEntry texture = texturesZip.getEntry(textureName.substring(1));
                if (texture != null) {
                    inputStream = texturesZip.getInputStream(texture);
                }
            } else {
                File texture = new File(texturePacksPath + texturePack + File.separator + textureName);
                if (texture.exists())
                    inputStream = new FileInputStream(texture);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
