package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LWJGLGL11GLEnableAdvice {
    public static boolean enableClassicViewmodelPatch;
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(0) int target) {
        if (target != 2977)
            return;

        try {
            boolean enable = (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLGL11GLEnableAdvice").getField("enableClassicViewmodelPatch").get(null);
            if (!enable)
                return;

            boolean hideViewModel = (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.FOVViewmodelAdvice").getField("hideViewModel").get(null);

            //TODO: Figure out how to get viewmodel FOV working, making this patch classic only.
            if (hideViewModel)
                GL11.glScalef(0,0,0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
