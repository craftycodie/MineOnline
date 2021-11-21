package gg.codie.mineonline.patches.mcpatcher;

import gg.codie.common.utils.OSUtils;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.patches.HashMapPutAdvice;
import gg.codie.mineonline.patches.minecraft.ClockFXAdvice;
import gg.codie.mineonline.patches.minecraft.CompassFXAdvice;
import gg.codie.mineonline.utils.MathUtils;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class HDTextureFXHelper {

    public static HashMap<String, Float> ticks = new HashMap<>();
    public static HashMap<String, Integer> frameCounts = new HashMap<>();
    public static HashMap<String, Integer> frameTimes = new HashMap<>();
    public static HashMap<String, int[][]> textures = new HashMap<>();
    public static int[] currentTexture;

    public static float scale = 1;

    private static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_FAST);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static void reloadTextures() {
        textures.clear();
        ticks.clear();
        frameCounts.clear();
        currentTexture = null;

        String[] textureNames = new String[] {
            "/custom_water_flowing.png",
            "/custom_water_still.png",
            "/custom_lava_flowing.png",
            "/custom_lava_still.png",
            "/custom_portal.png",
            "/custom_fire_e_w.png",
            "/custom_fire_n_s.png",
            "/custom_clock.png",
            "/custom_compass.png"
        };

        ZipFile texturesZip = null;

        for (String textureName : textureNames) {
            try {
                if (texturesZip == null)
                    texturesZip = new ZipFile(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH + Settings.singleton.getTexturePack());

                ZipEntry texture = texturesZip.getEntry(textureName.substring(1));
                frameTimes.put(textureName, 1);

                if (texture != null) {
                    BufferedImage b = ImageIO.read(texturesZip.getInputStream(texture));
                    frameCounts.put(textureName, b.getHeight() / b.getWidth());
                    currentTexture = new int[(int) ((scale * 16) * (scale * 16) * 4)];
                    textures.put(textureName, new int[b.getHeight() / b.getWidth()][(int) ((scale * 16) * (scale * 16) * 4)]);
                    for (int i = 0; i < b.getHeight() / b.getWidth(); i++) {
                        int[] tmp = new int[(int) ((scale * 16) * (scale * 16))];
                        BufferedImage frame = b.getSubimage(0, i * b.getWidth(), b.getWidth(), b.getWidth());

                        if (frame.getWidth() != (scale * 16)) {
                            frame = resize(frame, (int) (scale * 16), (int) (scale * 16));
                        }

                        frame.getRGB(0, 0, (int) ((scale * 16)), (int) (scale * 16), tmp, 0, (int) (scale * 16));

                        for (int pixelI = 0; pixelI < tmp.length; pixelI++) {
                            int alpha = ((tmp[pixelI] >> 24) & 0xff);
                            int red = ((tmp[pixelI] >> 16) & 0xff);
                            int green = ((tmp[pixelI] >> 8) & 0xff);
                            int blue = ((tmp[pixelI]) & 0xff);

                            if (OSUtils.isM1System())
                                tmp[pixelI] = blue | (green << 8) | (red << 16) | (alpha << 24);
                            else
                                tmp[pixelI] = red | (green << 8) | (blue << 16) | (alpha << 24);
                        }

                        textures.get(textureName)[i] = tmp;
                    }
                }

                ZipEntry mcmeta = texturesZip.getEntry(textureName.replace(".png", ".mcmeta").substring(1));
                if (mcmeta != null) {
                    String mcmetaString = new BufferedReader(new InputStreamReader(texturesZip.getInputStream(mcmeta)))
                            .lines().collect(Collectors.joining("\n"));
                    JSONObject mcmetaJson = new JSONObject(mcmetaString);
                    if (mcmetaJson.optJSONObject("animation") != null)
                        frameTimes.put(textureName, mcmetaJson.getJSONObject("animation").optInt("frametime", 1));
                }
            } catch (FileNotFoundException fex) {
                // nada
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static BufferedImage didSubTex(int x, int y) {
        String textureName = "";

        // classic
        if (x == 224 && y == 0) {
            if (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().hasNetherPortalTexture)
                textureName = "/custom_portal.png";
            else
                textureName = "/custom_water_still.png";
        } else if (x == 224 && y == 16)
            textureName = "/custom_lava_still.png";

        // after classic
        else if (x == 208 && y == 192)
            textureName = "/custom_water_still.png";
        else if (x == 208 && y == 224)
            textureName = "/custom_lava_still.png";

        // Indev animated border water.
        else if (GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D) == HashMapPutAdvice.textures.getOrDefault("/water.png", -1))
            textureName = "/custom_water_still.png";

//        else if (x == 96 && y == 48) // Compass
//            textureName = "/custom_fire_e_w.png";
//        else if (x == 96 && y == 64) // Clock
//            textureName = "/custom_fire_e_w.png";

        else if (x == 224 && y == 32) // 1/4
            textureName = "/custom_water_flowing.png";

//        else if (x == 224 && y == 48) // 1/4
//            textureName = "/custom_gear_clockwise.png";
//        else if (x == 240 && y == 48) // 1/4
//            textureName = "/custom_gear_counterclockwise.png";

        else if (x == 224 && y == 192) // 1/4
            textureName = "/custom_water_flowing.png";
        else if (x == 224 && y == 208) // 1/4
            textureName = "/custom_water_flowing.png";
        else if (x == 240 && y == 208) // 1/4
            textureName = "/custom_water_flowing.png";
        else if (x == 240 && y == 192) // 1/4
            textureName = "/custom_water_flowing.png";

        else if (x == 224 && y == 224) // 1/4
            textureName = "/custom_lava_flowing.png";
        else if (x == 224 && y == 240) // 1/4
            textureName = "/custom_lava_flowing.png";
        else if (x == 240 && y == 224) // 1/4
            textureName = "/custom_lava_flowing.png";
        else if (x == 240 && y == 240) // 1/4
            textureName = "/custom_lava_flowing.png";

        else if (x == 240 && y == 16)
            textureName = "/custom_fire_e_w.png";
        else if (x == 240 && y == 32)
            textureName = "/custom_fire_n_s.png";

        else if (x == 96 && y == 48)
            textureName = "/custom_compass.png";
        else if (x == 96 && y == 64)
            textureName = "/custom_clock.png";

        // Unknown, beta 1.9-pre6
        else if (x == 0 && y == 0)
            return null;

        else
            System.out.println("Unknown dynamic texture: " + x + ", " + y);

        if (!textures.containsKey(textureName))
            return null;

        if (ticks.containsKey(textureName))
            ticks.put(textureName, ticks.get(textureName) + (1 / (float)frameTimes.get(textureName)));
        else
            ticks.put(textureName, 0f);

        if (textureName.equals("/custom_compass.png"))
            ticks.put(textureName, (float)(32 - ((int)((Math.abs(CompassFXAdvice.dial) % 6.4) * 10) / 2) - 1));

        if (textureName.equals("/custom_clock.png"))
            ticks.put(textureName, (float)Math.abs((int)(MathUtils.mod(ClockFXAdvice.dial, 6.3) * 10)));

        if (ticks.get(textureName) > frameCounts.get(textureName) - 1)
            ticks.put(textureName, 0f);

        System.arraycopy(textures.get(textureName)[(int)Math.floor(ticks.get(textureName))%textures.get(textureName).length], 0, currentTexture, 0, textures.get(textureName)[(int)Math.floor(ticks.get(textureName))%textures.get(textureName).length].length);

        BufferedImage animatedTexture = new BufferedImage((int)scale * 16, (int)scale * 16, BufferedImage.TYPE_INT_ARGB);
        animatedTexture.setData(Raster.createRaster(animatedTexture.getSampleModel(), new DataBufferInt(currentTexture, currentTexture.length), new Point()));

        return animatedTexture;
    }

}
