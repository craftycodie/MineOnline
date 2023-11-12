package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

import java.awt.*;

public class BufferedImageGetRGBM1Advice {
    @Advice.OnMethodExit
    public static void intercept(@Advice.Return(readOnly = false) int[] rgb) {
        for (int i = 0; i < rgb.length; i++) {
            Color color = new Color(rgb[i], true);
            Color swapped = new Color(color.getBlue(), color.getGreen(), color.getRed(), color.getAlpha());
            rgb[i] = swapped.getRGB();
        }
    }
}
