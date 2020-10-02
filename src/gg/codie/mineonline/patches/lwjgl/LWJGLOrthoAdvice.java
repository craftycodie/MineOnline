package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;
import org.lwjgl.opengl.Display;

public class LWJGLOrthoAdvice {
    public static int guiScale;

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 1, readOnly = false) double right, @Advice.Argument(value = 2, readOnly = false) double bottom) {
        try {
            int guiScale = (int)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLOrthoAdvice").getField("guiScale").get(null);

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

            if ((int)right == (int)noGuiScaleRight && (int)bottom == (int)noGuiScaleBottom) {
                right = guiScaleRight;
                bottom = guiScaleBottom;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
