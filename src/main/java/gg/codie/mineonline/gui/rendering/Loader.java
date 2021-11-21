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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Loader {
    private final HashMap<String, Integer> textures = new HashMap<>();

    static final String MINEONLINE_TEXTURE_PREFIX = "mo:";

    public final int MISSING_TEXTURE_ID;

    public static Loader singleton;

    public Loader() {
        MISSING_TEXTURE_ID = loadTexture("missingno.", LauncherFiles.MISSING_TEXTURE);
        singleton = this;
    }

    public static void reloadMineOnlineTextures() {
        List<String> moTextures = singleton.textures.keySet().stream().filter(tex -> tex.startsWith(MINEONLINE_TEXTURE_PREFIX)).collect(Collectors.toList());
        moTextures.forEach(texture -> {
            singleton.unloadTexture(texture);
        });
        Font.reloadFont();
    }

    public void unloadTexture(String name) {
        if (textures.containsKey(name)) {
            GL11.glDeleteTextures(textures.get(name));
            textures.remove(name);
        }
    }

    public int loadTexture(String name, URL url) {
        try {
            return loadTexture(name, url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return MISSING_TEXTURE_ID;
    }

    public void loadImageData(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        int[] aint = new int[width * height];
        byte[] byteBuf = new byte[width * height * 4];

        bufferedImage.getRGB(0, 0, width, height, aint, 0, width);

        for (int l = 0; l < aint.length; ++l) {
            int alpha = aint[l] >> 24 & 255;
            int red = aint[l] >> 16 & 255;
            int green = aint[l] >> 8 & 255;
            int blue = aint[l] & 255;

            if (alpha == 0) {
                red = 255;
                green = 255;
                blue = 255;
            }

            byteBuf[l * 4 + 0] = (byte) red;
            byteBuf[l * 4 + 1] = (byte) green;
            byteBuf[l * 4 + 2] = (byte) blue;
            byteBuf[l * 4 + 3] = (byte) alpha;
        }

        this.checkImageDataSize(width, height);
        this.imageData.clear();
        this.imageData.put(byteBuf);
        this.imageData.position(0).limit(byteBuf.length);
    }

    public void overwriteTexture(int textureID, InputStream in) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(in);

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        this.loadImageData(bufferedImage);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        IntBuffer temp = BufferUtils.createIntBuffer(16);
        GL11.glGetInteger(3379, temp);
        int max = temp.get(0);
        if (width <= max && height <= max) {
            GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5121, this.imageData);
        } else {
            throw new IOException("Attempt to allocate a texture to big for the current hardware");
        }
    }

    private static final LinkedList<String> ignoredTextures = new LinkedList<>(Arrays.asList(
            "/font/default.png", //Needs to be patched separately.
            "/misc/foliagecolor.png", //Needs to be patched separately.
            "/misc/grasscolor.png", //Needs to be patched separately.
            "/misc/watercolor.png", //Needs to be patched separately.
            "/default.png", //Needs to be patched separately.
            "/default.gif", //Needs to be patched separately.

            "/pack.png"
    ));

    public static void reloadMinecraftTexture(String textureName) {
//        if (Globals.DEV)
//            System.out.println("Loading Texture " + textureName);

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
                    if (!HashMapPutAdvice.textures.containsKey(textureName))
                        return;

                    Loader.singleton.overwriteTexture(HashMapPutAdvice.textures.get(textureName), texturesZip.getInputStream(texture));

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
                ex.printStackTrace();
            }
        }

        try {
            if (LegacyGameManager.getAppletWrapper().getMinecraftAppletClass() != null)
                Loader.singleton.overwriteTexture(HashMapPutAdvice.textures.get(textureName), LegacyGameManager.getAppletWrapper().getMinecraftAppletClass().getResourceAsStream(textureName));
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
                "/gui/background.png", // This was commented before, no idea why.
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

    public static FloatBuffer createDirectFloatBuffer(int var0) {
        return createDirectByteBuffer(var0 << 2).asFloatBuffer();
    }

    private final IntBuffer singleIntBuffer = createDirectIntBuffer(1);
    private boolean clampTexture = false;
    private boolean blurTexture = false;
    private ByteBuffer imageData;

    public static IntBuffer createDirectIntBuffer(int var0) {
        return createDirectByteBuffer(var0 << 2).asIntBuffer();
    }

    public int loadTexture(String name, InputStream textureStream) {
        Integer id = this.textures.get(name);

        if (id != null) {
            return id;
        } else {
            try {
                if (textureStream == null) return MISSING_TEXTURE_ID;

                BufferedImage bufferedImage = ImageIO.read(textureStream);
                this.singleIntBuffer.clear();
                GL11.glGenTextures(this.singleIntBuffer);
                id = this.singleIntBuffer.get(0);
                if (name.startsWith("##")) {
                    this.setupTexture(bufferedImage, id);
                } else if (name.startsWith("%clamp%")) {
                    this.clampTexture = true;
                    this.setupTexture(bufferedImage, id);
                    this.clampTexture = false;
                } else if (name.startsWith("%blur%")) {
                    this.blurTexture = true;
                    this.setupTexture(bufferedImage, id);
                    this.blurTexture = false;
                } else {
                    this.setupTexture(bufferedImage, id);
                }

                this.textures.put(name, id);
                return id;
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
                return MISSING_TEXTURE_ID;
            }
        }
    }

    public void setupTexture(BufferedImage bufferedimage, int i) {
        GL11.glBindTexture(3553, i);

        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);

        if (this.blurTexture) {
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
        }

        if (this.clampTexture) {
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
        } else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }

        int width = bufferedimage.getWidth();
        int height = bufferedimage.getHeight();
        this.loadImageData(bufferedimage);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, 6408, width, height, 0, 6408, 5121, this.imageData);
    }

    private void checkImageDataSize(int width, int height) {
        if (this.imageData != null) {
            int len = width * height * 4;

            if (this.imageData.capacity() >= len) {
                return;
            }
        }

        this.allocateImageData(width, height);
    }

    private void allocateImageData(int width, int height) {
        int imgLen = width * height * 4;

        this.imageData = createDirectByteBuffer(imgLen);
    }
}
