package gg.codie.mineonline.gui.rendering;

import org.lwjgl.util.vector.Vector2f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class TextureHelper {

    public static float[] getPlaneTextureCoords(Vector2f textureDimensions, Vector2f pixelBegin, Vector2f pixelEnd) {
        Vector2f units = new Vector2f(textureDimensions.x / 100, textureDimensions.y / 100);

        return new float[] {
                (pixelBegin.x / units.x) / 100, (pixelBegin.y / units.y) / 100,
                (pixelBegin.x / units.x) / 100, ((pixelBegin.y + pixelEnd.y) / units.y) / 100,
                ((pixelBegin.x + pixelEnd.x) / units.x) / 100, ((pixelBegin.y + pixelEnd.y) / units.y) / 100,
                ((pixelBegin.x + pixelEnd.x) / units.x) / 100, (pixelBegin.y / units.y) / 100,
        };
    }

    public static float[] getXFlippedPlaneTextureCoords(Vector2f textureDimensions, Vector2f pixelBegin, Vector2f pixelEnd) {
        Vector2f units = new Vector2f(textureDimensions.x / 100, textureDimensions.y / 100);

        return new float[] {
                ((pixelBegin.x + pixelEnd.x) / units.x) / 100, (pixelBegin.y / units.y) / 100,
                ((pixelBegin.x + pixelEnd.x) / units.x) / 100, ((pixelBegin.y + pixelEnd.y) / units.y) / 100,
                (pixelBegin.x / units.x) / 100, ((pixelBegin.y + pixelEnd.y) / units.y) / 100,
                (pixelBegin.x / units.x) / 100, (pixelBegin.y / units.y) / 100,
        };
    }

    public static float[] getYFlippedPlaneTextureCoords(Vector2f textureDimensions, Vector2f pixelBegin, Vector2f pixelEnd) {
        Vector2f units = new Vector2f(textureDimensions.x / 100, textureDimensions.y / 100);

        return new float[] {
                (pixelBegin.x / units.x) / 100, ((pixelBegin.y + pixelEnd.y) / units.y) / 100,
                (pixelBegin.x / units.x) / 100, (pixelBegin.y / units.y) / 100,
                ((pixelBegin.x + pixelEnd.x) / units.x) / 100, (pixelBegin.y / units.y) / 100,
                ((pixelBegin.x + pixelEnd.x) / units.x) / 100, ((pixelBegin.y + pixelEnd.y) / units.y) / 100,
        };
    }

    public static BufferedImage cropImage(BufferedImage bufferedImage, int x, int y, int width, int height){
        BufferedImage croppedImage = bufferedImage.getSubimage(x, y, width, height);
        return croppedImage;
    }

    public static InputStream convertModernSkin(InputStream inputStream){
        try {
            BufferedImage skin = ImageIO.read(inputStream);
            BufferedImage croppedSkin = skin.getSubimage(0, 0, 64, 32);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(croppedSkin, "png", os);
            byte[] bytes = os.toByteArray();
            return new ByteArrayInputStream(bytes);
        } catch (Exception ex) {
            return null;
        }
    }

    public static BufferedImage convertSkin(BufferedImage legacySkin) {
        BufferedImage skin = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);

        Graphics g = skin.getGraphics();

        g.drawImage(legacySkin, 0, 0, null);
        if(legacySkin.getHeight() < 64 && legacySkin.getHeight() >= 32) {
            g.drawImage(legacySkin.getSubimage(0, 16, 16, 16), 16, 48, null);
            g.drawImage(legacySkin.getSubimage(40, 16, 16, 16), 32, 48, null);
        }
        g.dispose();

        return skin;
    }

    public static float[] getCubeTextureCoords(Vector2f textureDimensions,
                                                   Vector2f pixelBegin1, Vector2f pixelEnd1,
                                                   Vector2f pixelBegin2, Vector2f pixelEnd2,
                                                   Vector2f pixelBegin3, Vector2f pixelEnd3,
                                                   Vector2f pixelBegin4, Vector2f pixelEnd4,
                                                   Vector2f pixelBegin5, Vector2f pixelEnd5,
                                                   Vector2f pixelBegin6, Vector2f pixelEnd6
                                                ) {
        float[] results = new float[48];

        int i = 0;

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin1, pixelEnd1)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin2, pixelEnd2)) {
            results[i] = coord;
            i++;
        }


        for(float coord : getXFlippedPlaneTextureCoords(textureDimensions, pixelBegin3, pixelEnd3)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin4, pixelEnd4)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getYFlippedPlaneTextureCoords(textureDimensions, pixelBegin5, pixelEnd5)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin6, pixelEnd6)) {
            results[i] = coord;
            i++;
        }

        return results;

    }

    // Ok so this is just for left arms/legs, but this whole package is a mess so I'm not refactoring now.
    public static float[] getLeftLimbTextureCoords(Vector2f textureDimensions,
                                                   Vector2f pixelBegin1, Vector2f pixelEnd1,
                                                   Vector2f pixelBegin2, Vector2f pixelEnd2,
                                                   Vector2f pixelBegin3, Vector2f pixelEnd3,
                                                   Vector2f pixelBegin4, Vector2f pixelEnd4,
                                                   Vector2f pixelBegin5, Vector2f pixelEnd5,
                                                   Vector2f pixelBegin6, Vector2f pixelEnd6
    ) {
        float[] results = new float[48];

        int i = 0;

        for(float coord : getXFlippedPlaneTextureCoords(textureDimensions, pixelBegin1, pixelEnd1)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin2, pixelEnd2)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getXFlippedPlaneTextureCoords(textureDimensions, pixelBegin3, pixelEnd3)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin4, pixelEnd4)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getYFlippedPlaneTextureCoords(textureDimensions, pixelBegin5, pixelEnd5)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin6, pixelEnd6)) {
            results[i] = coord;
            i++;
        }

        return results;

    }

    public static float[] getRightLimbTextureCoords(Vector2f textureDimensions,
                                                    Vector2f pixelBegin1, Vector2f pixelEnd1,
                                                    Vector2f pixelBegin2, Vector2f pixelEnd2,
                                                    Vector2f pixelBegin3, Vector2f pixelEnd3,
                                                    Vector2f pixelBegin4, Vector2f pixelEnd4,
                                                    Vector2f pixelBegin5, Vector2f pixelEnd5,
                                                    Vector2f pixelBegin6, Vector2f pixelEnd6
    ) {
        float[] results = new float[48];

        int i = 0;

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin1, pixelEnd1)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getXFlippedPlaneTextureCoords(textureDimensions, pixelBegin2, pixelEnd2)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getXFlippedPlaneTextureCoords(textureDimensions, pixelBegin3, pixelEnd3)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin4, pixelEnd4)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getYFlippedPlaneTextureCoords(textureDimensions, pixelBegin5, pixelEnd5)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin6, pixelEnd6)) {
            results[i] = coord;
            i++;
        }

        return results;

    }

}
