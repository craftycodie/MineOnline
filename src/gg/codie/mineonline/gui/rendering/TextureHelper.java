package gg.codie.mineonline.gui.rendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.util.Arrays;

public class TextureHelper {

    public static float[] getPlaneTextureCoords(Vector2f textureDimensions, Vector2f pixelBegin, Vector2f pixelEnd) {
        Vector2f units = new Vector2f(textureDimensions.x / 100, textureDimensions.y / 100);

        return new float[] {
                (pixelBegin.x / units.x) / 100, (pixelBegin.y / units.y) / 100,
                (pixelBegin.x / units.x) / 100, ((pixelBegin.y + pixelEnd.y) / units.y) / 100,
                ((pixelBegin.x + pixelEnd.x) / units.x) / 100, ((pixelBegin.x + pixelEnd.y) / units.y) / 100,
                ((pixelBegin.x + pixelEnd.x) / units.x) / 100, (pixelBegin.y / units.y) / 100,
        };
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

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin3, pixelEnd3)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin4, pixelEnd4)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin5, pixelEnd5)) {
            results[i] = coord;
            i++;
        }

        for(float coord : getPlaneTextureCoords(textureDimensions, pixelBegin6, pixelEnd6)) {
            results[i] = coord;
            i++;
        }

        System.out.println(Arrays.toString(results));
        return results;

    }

}
