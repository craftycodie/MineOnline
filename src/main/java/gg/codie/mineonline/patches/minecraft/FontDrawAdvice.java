package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.rendering.Font;
import net.bytebuddy.asm.Advice;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FontDrawAdvice {
    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    public static boolean intercept(@Advice.Argument(0) String string, @Advice.Argument(1) int x, @Advice.Argument(2) int y, @Advice.Argument(3) int color, @Advice.Argument(4) boolean darken) {
        try {
            Class fontClass = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.gui.rendering.Font");
            Object minecraftFont = fontClass.getDeclaredField("minecraftFont").get(null);
            fontClass.getDeclaredMethod("renderString", String.class, int.class, int.class, int.class, boolean.class).invoke(minecraftFont, string, x, y, color, darken);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
