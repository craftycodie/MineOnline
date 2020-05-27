package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.utils.MathUtils;
import jdk.nashorn.api.scripting.URLReader;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.FileInputStream;
import java.io.InputStream;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Loader {

    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<Integer>();

    public final int MISSING_TEXTURE_ID;

    public Loader() {
        MISSING_TEXTURE_ID = loadTexture(LauncherFiles.MISSING_TEXTURE);
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
        begin = new Vector2f((begin.x / Display.getWidth()) - 1, (begin.y / Display.getHeight()) - 1);
        size = new Vector2f(size.x / Display.getWidth(), size.y / Display.getHeight());

        System.out.println(begin);
        System.out.println(size);

        Vector2f end = new Vector2f(begin.x + size.x, begin.y + size.y);

        int vaoID = createVAO();
        bindIndicesBuffer(new int[] {
                0,1,2,
                2,3,0,
        });
        storeDataInAttributeList(0, 3, MathUtils.makePlaneVertices(begin, size));
        storeDataInAttributeList(1, 2, textureCoordinates);
        unbindVAO();
        return new RawModel(vaoID, 6);
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

    public int loadTexture(URL url) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", url.openStream());
        } catch (Exception e) {
            return MISSING_TEXTURE_ID;
        }

        int textureID = texture.getTextureID();

        return textureID;
    }

    public int loadTexture(String path) {
        Texture texture = null;
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

        return textureID;
    }

    public int loadTexture(InputStream stream) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", stream);
        } catch (Exception e) {
            return MISSING_TEXTURE_ID;
        }

        int textureID = texture.getTextureID();

        return textureID;
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

        for (int texture : textures) {
            GL11.glDeleteTextures(texture);
        }
    }

}
