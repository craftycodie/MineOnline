package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.textures.EGUITexture;
import gg.codie.mineonline.gui.rendering.utils.MathUtils;
import gg.codie.mineonline.patches.HashMapPutAdvice;
import gg.codie.mineonline.patches.lwjgl.LWJGLGL11GLTexSubImageAdvice;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
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
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Loader {

    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();
    private HashMap<String, Integer> textures = new HashMap<>();

    public final int MISSING_TEXTURE_ID;

    public static Loader singleton;

    public Loader() {
        MISSING_TEXTURE_ID = loadTexture("missingno.", LauncherFiles.MISSING_TEXTURE);
        singleton = this;
    }

    public int loadToVAO(float[] positions, float[] textureCoords) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, 2, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        unbindVAO();
        return vaoID;
    }

    public RawModel loadToVAO(float[] positions, float[] textureCoordinates, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoordinates);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public RawModel loadGUIToVAO(Vector2f begin, Vector2f size, float[] textureCoordinates) {
        begin = new Vector2f((begin.x / (Display.getWidth() / 2)) -1, (begin.y / (Display.getHeight() / 2)) -1);
        size = new Vector2f((size.x / (Display.getWidth() / 2)), (size.y / (Display.getHeight() / 2)));

        Vector2f end = new Vector2f(begin.x + size.x, begin.y + size.y);

        int vaoID = createVAO();
        bindIndicesBuffer(new int[] {
                0,1,2,
                2,3,0,
        });
        storeDataInAttributeList(0, 3, MathUtils.makePlaneVertices(begin, end));
        storeDataInAttributeList(1, 2, textureCoordinates);
        unbindVAO();
        return new RawModel(vaoID, 6);
    }

    public RawModel loadPlaneToVAO(Vector3f begin, Vector3f end, float[] textureCoordinates) {
        int vaoID = createVAO();
        bindIndicesBuffer(new int[] {
                0,1,2,
                2,3,0,
        });
        storeDataInAttributeList(0, 3, MathUtils.makePlaneVertices(new Vector2f(begin.x, begin.y), new Vector2f(end.x, end.y)));
        storeDataInAttributeList(1, 2, textureCoordinates);
        unbindVAO();
        return new RawModel(vaoID, 36);
    }


    public RawModel loadBoxToVAO(Vector3f begin, Vector3f end, float[] textureCoordinates) {
        int vaoID = createVAO();
        bindIndicesBuffer(new int[] {
                0,1,3,
                3,1,2,
                4,5,7,
                7,5,6,
                8,9,11,
                11,9,10,
                12,13,15,
                15,13,14,
                16,17,19,
                19,17,18,
                20,21,23,
                23,21,22

        });
        storeDataInAttributeList(0, 3, MathUtils.makeBoxVertices(begin, end));
        storeDataInAttributeList(1, 2, textureCoordinates);
        unbindVAO();
        return new RawModel(vaoID, 36);
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
//        int textureID = createTextureID();
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

    public static void reloadMinecraftTextures() {
        String[] textureNames = new String[] {
                "/terrain.png",
                "/particles.png",

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

                "/font/default.png",

                "/item/arrows.png",
                "/item/boat.png",
                "/item/cart.png",
                "/item/door.png",
                "/item/sign.png",

                "/misc/dial.png",
                "/misc/foliagecolor.png",
                "/misc/grasscolor.png",
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

                "/terrain/.moonpng",
                "/terrain/sun.png",

                "/default.png"
        };

        String texturePack = Settings.singleton.getTexturePack();

        ZipFile texturesZip = null;

        for (String textureName : textureNames) {
            try {
                if (texturesZip == null)
                    texturesZip = new ZipFile(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH + texturePack);

                ZipEntry texture = texturesZip.getEntry(textureName.substring(1));

                if (texture != null) {
                    Loader.singleton.overwriteTexture(HashMapPutAdvice.textures.get(textureName), texturesZip.getInputStream(texture), textureName);
                    continue;
                }
            } catch (Exception ex) {

            }
            try {
                if (LegacyGameManager.getAppletWrapper().getMinecraftAppletClass() != null) Loader.singleton.overwriteTexture(HashMapPutAdvice.textures.get(textureName), LegacyGameManager.getAppletWrapper().getMinecraftAppletClass().getResourceAsStream(textureName), textureName);
            } catch (Exception ex) {
                // ignore
            }
        }

        if (texturesZip != null) {
            try {
                BufferedImage terrain = ImageIO.read(texturesZip.getInputStream(texturesZip.getEntry("terrain.png")));
                LWJGLGL11GLTexSubImageAdvice.xMul = (float)terrain.getWidth() / 256;
                LWJGLGL11GLTexSubImageAdvice.yMul = (float)terrain.getHeight() / 256;
            } catch (Exception ex) {
                LWJGLGL11GLTexSubImageAdvice.xMul = 1;
                LWJGLGL11GLTexSubImageAdvice.yMul = 1;
            }
        } else {
            LWJGLGL11GLTexSubImageAdvice.xMul = 1;
            LWJGLGL11GLTexSubImageAdvice.yMul = 1;
        }
    }

    public static int get2Fold(int fold) {
        int ret;
        for(ret = 2; ret < fold; ret *= 2) {
        }

        return ret;
    }

    public int getGuiTexture(EGUITexture eguiTexture) {
        if (!textures.containsKey("mineonline:" + eguiTexture.textureName)) {
            Settings.singleton.loadSettings();
            if (eguiTexture.useTexturePack ) {
                try {
                    ZipFile texturesZip = new ZipFile(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH + Settings.singleton.getTexturePack());
                    ZipEntry texture = texturesZip.getEntry(eguiTexture.textureName.substring(1));
                    if (texture != null) {
                        return loadTexture("mineonline:" + eguiTexture.textureName, texturesZip.getInputStream(texture));
                    }
                } catch (Exception ex) {

                }
            }

            return loadTexture("mineonline:" + eguiTexture.textureName, Loader.class.getResource(eguiTexture.textureName));
        } else
            return textures.get("mineonline:" + eguiTexture.textureName);
    }

    public void unloadTexture(EGUITexture eguiTexture) {
        if (textures.containsKey("mineonline:" + eguiTexture.textureName)) {
            GL11.glDeleteTextures(textures.get("mineonline:" + eguiTexture.textureName));
            textures.remove("mineonline:" + eguiTexture.textureName);
        }
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataIntoIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataIntoIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public void cleanUp() {
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }

        for (int vbo : vbos) {
            GL15.glDeleteBuffers(vbo);
        }

        for (int texture : textures.values()) {
            GL11.glDeleteTextures(texture);
            textures.clear();
        }
    }

}
