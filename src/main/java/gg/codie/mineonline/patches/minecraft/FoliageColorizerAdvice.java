package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import net.bytebuddy.asm.Advice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FoliageColorizerAdvice {

    public static int[] pixels = new int[65536];

    public static void updateColorizer() {
        try {
            BufferedImage bufferedImage = null;
            String texturePack = Settings.singleton.getTexturePack();

            if (!texturePack.equals("Default")) {
                try {
                    ZipFile texturesZip = new ZipFile(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH + Settings.singleton.getTexturePack());
                    ZipEntry texture = texturesZip.getEntry("misc/foliagecolor.png");
                    if (texture != null) {
                        bufferedImage = ImageIO.read(texturesZip.getInputStream(texture));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (bufferedImage == null)
                bufferedImage = ImageIO.read(LegacyGameManager.getAppletWrapper().getMinecraftAppletClass().getResourceAsStream("/misc/foliagecolor.png"));

            pixels = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
            bufferedImage.getRGB(0, 0, 256, 256, pixels, 0, 256);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Advice.OnMethodExit()
    public static void intercept(@Advice.Argument(0) double d, @Advice.Argument(value = 1, readOnly = false) double d2, @Advice.Return(readOnly = false) int returnColor) {
        try {
            int n = (int) ((1.0 - d) * 255.0);
            int n2 = (int) ((1.0 - (d2 *= d)) * 255.0);
            int[] pixels = (int[]) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.FoliageColorizerAdvice").getDeclaredField("pixels").get(null);
            returnColor = pixels[n2 << 8 | n];
        } catch (Exception ex) {
            ex.printStackTrace();
            // ignore.
        }
    }
}
