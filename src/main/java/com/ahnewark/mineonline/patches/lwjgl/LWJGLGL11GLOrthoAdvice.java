package com.ahnewark.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;
import org.lwjgl.opengl.Display;

public class LWJGLGL11GLOrthoAdvice {
    public static boolean enable;
    public static int guiScale;
    public static boolean hideHud;

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 1, readOnly = false) double right, @Advice.Argument(value = 2, readOnly = false) double bottom, @Advice.Argument(value = 4, readOnly = false) double zNear, @Advice.Argument(value = 5, readOnly = false) double zFar) {
        try {
            int guiScale = (int)ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.lwjgl.LWJGLGL11GLOrthoAdvice").getField("guiScale").get(null);
            boolean enable = (boolean)ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.lwjgl.LWJGLGL11GLOrthoAdvice").getField("enable").get(null);
            boolean hideHud = (boolean)ClassLoader.getSystemClassLoader().loadClass("com.ahnewark.mineonline.patches.lwjgl.LWJGLGL11GLOrthoAdvice").getField("hideHud").get(null);

            if (hideHud) {
                zNear = 1;
                zFar = 2;
                return;
            }

            if (guiScale == 0)
                return;

            if (!enable)
                return;

            int scaledWidth;
            int scaledHeight;
            double guiScaleRight;
            double guiScaleBottom;
            double noGuiScaleRight;
            double noGuiScaleBottom;
            int scaleFactor;

            scaledWidth = Display.getWidth();
            scaledHeight = Display.getHeight();
            scaleFactor = 1;
            int k = guiScale;
            if(k == 0)
            {
                k = 1000;
            }
            for(; scaleFactor < k && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240; scaleFactor++) { }
            guiScaleRight = (double)scaledWidth / (double)scaleFactor;
            guiScaleBottom = (double)scaledHeight / (double)scaleFactor;

            scaleFactor = 1;
            k = 1000;

            for(; scaleFactor < k && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240; scaleFactor++) { }
            noGuiScaleRight = (double)scaledWidth / (double)scaleFactor;
            noGuiScaleBottom = (double)scaledHeight / (double)scaleFactor;

//            if ((int)right == (int)noGuiScaleRight && (int)bottom == (int)noGuiScaleBottom) {
                right = guiScaleRight;
                bottom = guiScaleBottom;
//            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
