package gg.codie.mineonline.patches.lwjgl;

import gg.codie.mineonline.patches.mcpatcher.HDTextureFXHelper;
import net.bytebuddy.asm.Advice;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LWJGLGL11GLTexSubImageAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(0) int target, @Advice.Argument(value = 2, readOnly = false) int xOffset, @Advice.Argument(value = 3, readOnly = false) int yOffset, @Advice.Argument(value = 4, readOnly = false) int width, @Advice.Argument(value = 5, readOnly = false) int height, @Advice.Argument(value = 8, readOnly = false) ByteBuffer textureBuffer) {

        if (target != 3553) //GL_TEXTURE_2D
            return;

//        System.out.println("DEBUG: Offset = " + xOffset + ", " + yOffset);
//        System.out.println("DEBUG: Size = " + width + ", " + height);

        if (width != 16 || height != 16)
            return;

        try {
            String texturePacksPath = (String) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.LauncherFiles").getField("MINECRAFT_TEXTURE_PACKS_PATH").get(null);

            int[] animatedData = new int[width * height];

            // Sometimes the position is not 0.
            textureBuffer.position(0);

            for (int i = 0; i < width * height; i++) {
                animatedData[i] = textureBuffer.getInt();
            }

            BufferedImage animatedTexture = HDTextureFXHelper.didSubTex(xOffset, yOffset);


            if (animatedTexture == null) {
                animatedTexture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                animatedTexture.setData(Raster.createRaster(animatedTexture.getSampleModel(), new DataBufferInt(animatedData, animatedData.length), new Point()));

                width *= HDTextureFXHelper.scale;
                height *= HDTextureFXHelper.scale;

                BufferedImage destinationBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g2 = destinationBufferedImage.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2.drawImage(animatedTexture, 0, 0, width, height, null);
                g2.dispose();

                animatedTexture = destinationBufferedImage;
            } else {
                width *= HDTextureFXHelper.scale;
                height *= HDTextureFXHelper.scale;
            }

//            int[] animatedPixels = new int[width * height];
//            animatedTexture.getRGB(0, 0, width, height, animatedPixels, 0, width);

            xOffset *= HDTextureFXHelper.scale;
            yOffset *= HDTextureFXHelper.scale;

//            System.out.println("DEBUG: Scaled Offset = " + xOffset + ", " + yOffset);
//            System.out.println("DEBUG: Scaled Size = " + width + ", " + height);

            int[] pixels = new int[width * height];
            animatedTexture.getRGB(0, 0, width, height, pixels, 0, width);

            textureBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
            textureBuffer.limit(width * height * 4);

            for (int pixel : pixels) {
                textureBuffer.putInt(pixel);
            }
//
            textureBuffer.position(0).limit(width * height * 4);

//            System.out.println("DEBUG: DONE");

            // This can be optimized by manually upscaling.
            // TODO: Optimise

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
