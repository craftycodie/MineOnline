package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.asm.Advice;
import org.lwjgl.opengl.Display;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ScaledResolutionConstructorAdvice {
    public static int guiScale;

    @Advice.OnMethodExit
    static void intercept(@Advice.This Object thisObj) {
        try {
            int guiScale = (int) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.ScaledResolutionConstructorAdvice").getField("guiScale").get(null);

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

            Field scaledWidthField = null;
            Field scaledHeightField = null;
            Field scaleFactorField = null;

            for (Field field : thisObj.getClass().getDeclaredFields()) {
                if (field.getType() == int.class) {
                    if (Modifier.isPrivate(field.getModifiers())) {
                        field.setAccessible(true);

                        if (scaledWidthField == null)
                            scaledWidthField = field;
                        else
                            scaledHeightField = field;
                    } else
                        scaleFactorField = field;
                }
            }

            scaledWidthField.set(thisObj, scaledWidth);
            scaledHeightField.set(thisObj, scaledHeight);
            scaleFactorField.set(thisObj, scaleFactor);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
