package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.asm.Advice;
import org.lwjgl.opengl.Display;

public class GuiScreenOpenAdvice {
    public static int guiScale;

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 1, readOnly = false) int width, @Advice.Argument(value = 2, readOnly = false) int height) {
        try {
            int guiScale = (int) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.GuiScreenOpenAdvice").getField("guiScale").get(null);

            int scaledWidth;
            int scaledHeight;
            double guiScaleRight;
            double guiScaleBottom;
            int scaleFactor;

            scaledWidth = Display.getWidth();
            scaledHeight = Display.getHeight();
            scaleFactor = 1;
            int k = guiScale;
            if (k == 0) {
                k = 1000;
            }
            for (; scaleFactor < k && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240; scaleFactor++) {
            }
            guiScaleRight = (double) scaledWidth / (double) scaleFactor;
            guiScaleBottom = (double) scaledHeight / (double) scaleFactor;
            scaledWidth = (int) Math.ceil(guiScaleRight);
            scaledHeight = (int) Math.ceil(guiScaleBottom);

            width = scaledWidth;
            height = scaledHeight;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
