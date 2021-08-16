package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.Globals;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.textures.EGUITexture;
import gg.codie.mineonline.patches.HashMapPutAdvice;
import gg.codie.mineonline.patches.mcpatcher.HDTextureFXHelper;
import gg.codie.mineonline.patches.minecraft.ItemRendererAdvice;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.*;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// TODO: Remove slick-util.
public class Loader {
    private HashMap<String, Integer> textures = new HashMap<>();

    static final String MINEONLINE_TEXTURE_PREFIX = "mo:";

    public final int MISSING_TEXTURE_ID;

    public static Loader singleton;

    public Loader() {
        MISSING_TEXTURE_ID = loadTexture("missingno.", LauncherFiles.MISSING_TEXTURE);
        singleton = this;
    }

    public void unloadTexture(String name) {
        if (textures.containsKey(name)) {
            GL11.glDeleteTextures(textures.get(name));
            textures.remove(name);
        }
    }

    public int loadTexture(String name, URL url) {
        if (textures.containsKey(name))
            return textures.get(name);

        Texture texture;
        try {
            texture = TextureLoader.getTexture("PNG", url.openStream());
        } catch (Exception e) {
            return MISSING_TEXTURE_ID;
        }

        int textureID = texture.getTextureID();

        textures.put(name, textureID);

        return textureID;
    }

    public int loadTexture(String name, String path) {
        if (textures.containsKey(name))
            return textures.get(name);

        Texture texture;
        try {
            if(path.startsWith("http")) {
                texture = TextureLoader.getTexture("PNG", new URL(path).openStream());
            } else {
                texture = TextureLoader.getTexture("PNG", new FileInputStream(path));
            }
        } catch (Exception e) {
            return MISSING_TEXTURE_ID;
        }

        int textureID = texture.getTextureID();

        textures.put(name, textureID);

        return textureID;
    }

    public int loadTexture(String name, InputStream stream) {
        if (textures.containsKey(name))
            return textures.get(name);

        Texture texture;
        try {
            texture = TextureLoader.getTexture("PNG", stream);
        } catch (Exception e) {
            return MISSING_TEXTURE_ID;
        }

        int textureID = texture.getTextureID();

        textures.put(name, textureID);

        return textureID;
    }

    public void overwriteTexture(int textureID, InputStream in, String resourceName) throws IOException {
        SGL GL = Renderer.get();
        LoadableImageData imageData = ImageDataFactory.getImageDataFor(resourceName);
        ByteBuffer textureBuffer = imageData.loadImage(new BufferedInputStream(in), false, null);
        TextureImpl texture = new TextureImpl(resourceName, GL11.GL_TEXTURE_2D, textureID);
        GL.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        int width = imageData.getWidth();
        int height = imageData.getHeight();
        boolean hasAlpha = imageData.getDepth() == 32;
        texture.setTextureWidth(imageData.getTexWidth());
        texture.setTextureHeight(imageData.getTexHeight());
        int texWidth = texture.getTextureWidth();
        int texHeight = texture.getTextureHeight();
        IntBuffer temp = BufferUtils.createIntBuffer(16);
        GL.glGetInteger(3379, temp);
        int max = temp.get(0);
        if (texWidth <= max && texHeight <= max) {
            int srcPixelFormat = hasAlpha ? 6408 : 6407;
            int componentCount = hasAlpha ? 4 : 3;
            texture.setWidth(width);
            texture.setHeight(height);
            texture.setAlpha(hasAlpha);
            //if (TextureLoader.holdTextureData) {
                texture.setTextureData(srcPixelFormat, componentCount, 9729, 9729, textureBuffer);
            //}

            GL.glTexParameteri(GL11.GL_TEXTURE_2D, 10241, GL11.GL_NEAREST);
            GL.glTexParameteri(GL11.GL_TEXTURE_2D, 10240, GL11.GL_NEAREST);
            GL.glTexImage2D(GL11.GL_TEXTURE_2D, 0, 6408, get2Fold(width), get2Fold(height), 0, srcPixelFormat, 5121, textureBuffer);
        } else {
            throw new IOException("Attempt to allocate a texture to big for the current hardware");
        }
    }

    private static LinkedList<String> ignoredTextures = new LinkedList<>(Arrays.asList(
            "/font/default.png", //Needs to be patched separately.
            "/misc/foliagecolor.png", //Needs to be patched separately.
            "/misc/grasscolor.png", //Needs to be patched separately.
            "/misc/watercolor.png", //Needs to be patched separately.
            "/default.png", //Needs to be patched separately.
            "/default.gif", //Needs to be patched separately.

            "/pack.png"
    ));

    public static void reloadMinecraftTexture(String textureName) {
        if (Globals.DEV)
            System.out.println("Loading Texture " + textureName);

        if (ignoredTextures.contains(textureName))
            return;

//        if (LegacyGameManager.isInGame() && !LegacyGameManager.getVersion().useTexturepackPatch)
//            return;

        boolean clamp = textureName.startsWith("%clamp%") || textureName.startsWith("%%") || textureName.equals("/shadow.png");
        boolean blur = textureName.startsWith("%blur%");

        // These can probably be handled better.
        if (textureName.startsWith("%"))
            textureName = textureName.substring(textureName.lastIndexOf("%") + 1);

        if(blur)
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        } else if(clamp)
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        } else
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        }

        String texturePack = Settings.singleton.getTexturePack();

        if (!texturePack.equals("Default")) {
            try {
                ZipFile texturesZip = new ZipFile(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH + texturePack);

                ZipEntry texture = texturesZip.getEntry(textureName.substring(1));

                if (texture != null) {
                    Loader.singleton.overwriteTexture(HashMapPutAdvice.textures.get(textureName), texturesZip.getInputStream(texture), textureName);

                    if (textureName.equals("/terrain.png")) {
                        try {
                            BufferedImage terrain = ImageIO.read(texturesZip.getInputStream(texture));
                            HDTextureFXHelper.scale = (float) terrain.getHeight() / 256;
                            ItemRendererAdvice.terrainScale = (float) terrain.getHeight() / 256;
                        } catch (Exception ex) {
                            HDTextureFXHelper.scale = 1;
                            ItemRendererAdvice.terrainScale = 1;
                        }
                        HDTextureFXHelper.reloadTextures();
                    }

                    if (textureName.equals("/gui/items.png")) {
                        try {
                            BufferedImage terrain = ImageIO.read(texturesZip.getInputStream(texture));
                            ItemRendererAdvice.itemScale = (float) terrain.getHeight() / 256;
                        } catch (Exception ex) {
                            ItemRendererAdvice.itemScale = 1;
                        }
                        HDTextureFXHelper.reloadTextures();
                    }

                    return;
                } else {
                    if (textureName.equals("/terrain.png")) {
                        HDTextureFXHelper.scale = 1;
                        ItemRendererAdvice.terrainScale = 1;
                        HDTextureFXHelper.reloadTextures();
                    }
                    if (textureName.equals("/gui/items.png")) {
                        HDTextureFXHelper.scale = 1;
                        ItemRendererAdvice.terrainScale = 1;
                        HDTextureFXHelper.reloadTextures();
                    }
                }
            } catch (Exception ex) {

            }
        }

        try {
            if (LegacyGameManager.getAppletWrapper().getMinecraftAppletClass() != null)
                Loader.singleton.overwriteTexture(HashMapPutAdvice.textures.get(textureName), LegacyGameManager.getAppletWrapper().getMinecraftAppletClass().getResourceAsStream(textureName), textureName);
            if(textureName.equals("/terrain.png")) {
                HDTextureFXHelper.scale = 1;
                HDTextureFXHelper.reloadTextures();
            }
        } catch (Exception ex) {
            // ignore
        }
    }

    public static void reloadMinecraftTextures() {
        String[] textureNames = new String[] {
                "/terrain.png",
                "/particles.png",
                "/clouds.png",
                "/rain.png",
                "/rock.png",
                "/water.png",
                "/dirt.png",
                "/grass.png",
                "/char.png",
                "/2char.png",

                "/gui/gui.png",
                "/gui/background.png",
                "/gui/container.png",
                "/gui/crafting.png",
                "/gui/logo.png",
                "/gui/furnace.png",
                "/gui/inventory.png",
                "/gui/items.png",
                "/gui/unknown_pack.png",
                "/gui/icons.png",

                "/armor/chain_1.png",
                "/armor/chain_2.png",
                "/armor/cloth_1.png",
                "/armor/cloth_2.png",
                "/armor/diamond_1.png",
                "/armor/diamond_2.png",
                "/armor/gold_1.png",
                "/armor/gold_2.png",
                "/armor/iron_1.png",
                "/armor/iron_2.png",

                "/art/kz.png",

                "/environment/clouds.png",
                "/environment/rain.png",
                "/environment/snow.png",

//                "/font/default.png",

                "/item/arrows.png",
                "/item/boat.png",
                "/item/cart.png",
                "/item/door.png",
                "/item/sign.png",

                "/misc/dial.png",
                "/misc/foliagecolor.png",
                "/misc/grasscolor.png",
                "/misc/watercolor.png",
                "/misc/pumpkinblur.png",
                "/misc/shadow.png",
                "/misc/vignette.png",
                "/misc/water.png",

                "/mob/char.png",
                "/mob/chicken.png",
                "/mob/cow.png",
                "/mob/creeper.png",
                "/mob/ghast.png",
                "/mob/ghast_fire.png",
                "/mob/pig.png",
                "/mob/pigman.png",
                "/mob/pigzombie.png",
                "/mob/saddle.png",
                "/mob/sheep.png",
                "/mob/sheep_fur.png",
                "/mob/skeleton.png",
                "/mob/slime.png",
                "/mob/spider.png",
                "/mob/spider_eyes.png",
                "/mob/zombie.png",

                "/terrain/moon.png",
                "/terrain/sun.png",

//                "/default.png",
//                "/default.gif",
        };

//        if (LegacyGameManager.isInGame() && !LegacyGameManager.getVersion().useTexturepackPatch)
//            return;

        for (String textureName : textureNames) {
            reloadMinecraftTexture(textureName);
        }
    }

    public static int get2Fold(int fold) {
        int ret;
        for(ret = 2; ret < fold; ret *= 2) {
        }

        return ret;
    }

    public int getGuiTexture(EGUITexture eguiTexture) {
        if (!textures.containsKey(MINEONLINE_TEXTURE_PREFIX + eguiTexture.textureName)) {
            Settings.singleton.loadSettings();
            if (eguiTexture.useTexturePack ) {
                try {
                    ZipFile texturesZip = new ZipFile(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH + Settings.singleton.getTexturePack());
                    ZipEntry texture = texturesZip.getEntry(eguiTexture.textureName.substring(1));
                    if (texture != null) {
                        return loadTexture(MINEONLINE_TEXTURE_PREFIX + eguiTexture.textureName, texturesZip.getInputStream(texture));
                    }
                } catch (Exception ex) {

                }
            }

            return loadTexture(MINEONLINE_TEXTURE_PREFIX + eguiTexture.textureName, Loader.class.getResource("/textures" + eguiTexture.textureName));
        } else
            return textures.get(MINEONLINE_TEXTURE_PREFIX + eguiTexture.textureName);
    }

    public void unloadTexture(EGUITexture eguiTexture) {
        if (textures.containsKey(MINEONLINE_TEXTURE_PREFIX + eguiTexture.textureName)) {
            GL11.glDeleteTextures(textures.get(MINEONLINE_TEXTURE_PREFIX + eguiTexture.textureName));
            textures.remove(MINEONLINE_TEXTURE_PREFIX + eguiTexture.textureName);
        }
    }

    public static synchronized ByteBuffer createDirectByteBuffer(int i)
    {
        ByteBuffer bytebuffer = ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
        return bytebuffer;
    }
}
