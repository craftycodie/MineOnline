package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.Arrays;
import java.util.LinkedList;

public class PlayerGameObject extends GameObject {

    private final Loader loader;
    private final StaticShader shader;

    private final int SKIN_WIDTH = 64;
    private final int SKIN_HEIGHT = 32;

    public PlayerGameObject(Loader loader, StaticShader shader, Vector3f localPosition, float rotX, float rotY, float rotZ, float scale)
    {
        this.loader = loader;
        this.shader = shader;

        this.localPosition = localPosition;
        this.localXRot = rotX;
        this.localYRot = rotY;
        this.localZRot = rotZ;
        this.scale = scale;

        playerCloak = addCape(new Vector3f(-5f, 0f, -1f), 10, 16, 1, new Vector3f(0, 8, -2), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(0, 0), new Vector2f(10, 16),
                new Vector2f(0, 0), new Vector2f(10, 16),
                new Vector2f(0, 0), new Vector2f(10, 16),
                new Vector2f(0, 0), new Vector2f(10, 16),
                new Vector2f(0, 0), new Vector2f(10, 16),
                new Vector2f(0, 0), new Vector2f(10, 16)
        ));

        playerHead = addBox(new Vector3f(-4f, -8f, -4f), 8, 8, 8, new Vector3f(0, 32, 0), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(24, 8), new Vector2f(8, 8),
                new Vector2f(8, 8), new Vector2f(8, 8),
                new Vector2f(0, 8), new Vector2f(8, 8),
                new Vector2f(8, 8), new Vector2f(8, 8),
                new Vector2f(8, 8), new Vector2f(8, 8),
                new Vector2f(8, 8), new Vector2f(8, 8)
                ));


        playerHeadwear = addBox(new Vector3f(-4, -8, -4), 8, 8, 8, new Vector3f(0, 32, 0), textureCoords);
        playerBody = addBox(new Vector3f(-4, 0, 2), 8, 12, 4, new Vector3f(0, 12, -4), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(20, 20), new Vector2f(8, 12),
                new Vector2f(20, 20), new Vector2f(8, 12),
                new Vector2f(20, 20), new Vector2f(8, 12),
                new Vector2f(20, 20), new Vector2f(8, 12),
                new Vector2f(20, 20), new Vector2f(8, 12),
                new Vector2f(20, 20), new Vector2f(8, 12)
        ));

        playerRightArm = addBox(new Vector3f(-3, -2, -2), 4, 12, 4, new Vector3f(7, 14, 0), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(44, 20), new Vector2f(4, 12),
                new Vector2f(44, 20), new Vector2f(4, 12),
                new Vector2f(44, 20), new Vector2f(4, 12),
                new Vector2f(44, 20), new Vector2f(4, 12),
                new Vector2f(44, 20), new Vector2f(4, 12),
                new Vector2f(44, 20), new Vector2f(4, 12)
        ));


        playerLeftArm = addBox(new Vector3f(-1, -2, -2), 4, 12, 4, new Vector3f(-7, 14, 0), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(44, 20), new Vector2f(4, 12),
                new Vector2f(44, 20), new Vector2f(4, 12),
                new Vector2f(44, 20), new Vector2f(4, 12),
                new Vector2f(44, 20), new Vector2f(4, 12),
                new Vector2f(44, 20), new Vector2f(4, 12),
                new Vector2f(44, 20), new Vector2f(4, 12)
        ));


        playerRightLeg = addBox(new Vector3f(-2, 0, -2), 4 , 12, 4, new Vector3f(2, 0, 0), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(4, 20), new Vector2f(4, 12),
                new Vector2f(4, 20), new Vector2f(4, 12),
                new Vector2f(4, 20), new Vector2f(4, 12),
                new Vector2f(4, 20), new Vector2f(4, 12),
                new Vector2f(4, 20), new Vector2f(4, 12),
                new Vector2f(4, 20), new Vector2f(4, 12)
        ));

        playerLeftLeg = addBox(new Vector3f(-2, 0, -2), 4 , 12, 4, new Vector3f(-2, 0, 0 ), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(4, 20), new Vector2f(4, 12),
                new Vector2f(4, 20), new Vector2f(4, 12),
                new Vector2f(4, 20), new Vector2f(4, 12),
                new Vector2f(4, 20), new Vector2f(4, 12),
                new Vector2f(4, 20), new Vector2f(4, 12),
                new Vector2f(4, 20), new Vector2f(4, 12)
        ));
    }

    public PlayerGameObject(Loader loader, StaticShader shader) {
        this(loader, shader, new Vector3f(0, 0, 0), 0, 0, 0, 1);
    }

    @Override
    public void increaseRotation(float dx, float dy, float dz) {
        super.increaseRotation(dx, dy, dz);

        if(getX() > 30) {
            setLocalXRot(30);
        }

        if(getX() < -30) {
            setLocalXRot(-30);
        }
    }

    private GameObject addBox(Vector3f begin, int width, int height, int depth, Vector3f position, float[] textureCoords) {
        RawModel model = loader.loadBoxToVAO(begin,
                new Vector3f(begin.x + width, begin.y + height, begin.z + depth),
                textureCoords);

        ModelTexture modelTexture = new ModelTexture(loader.loadTexture("char"));
        TexturedModel texturedModel =  new TexturedModel(model, modelTexture);

        GameObject box = new GameObject(texturedModel, position, 0, 0, 0, 1);

        addChild(box);

        return box;
    }

    private GameObject addCape(Vector3f begin, int width, int height, int depth, Vector3f position, float[] textureCoords) {
        RawModel model = loader.loadBoxToVAO(begin,
                new Vector3f(begin.x + width, begin.y + height, begin.z + depth),
                textureCoords);

        ModelTexture modelTexture = new ModelTexture(loader.loadTexture("cape"));
        TexturedModel texturedModel =  new TexturedModel(model, modelTexture);

        GameObject box = new GameObject(texturedModel, position, 0, 0, 0, 1);

        addChild(box);

        return box;
    }

    public GameObject playerHead;
    public GameObject playerHeadwear;
    public GameObject playerBody;
    public GameObject playerRightArm;
    public GameObject playerLeftArm;
    public GameObject playerRightLeg;
    public GameObject playerLeftLeg;
    public GameObject playerCloak;

    static final float[] textureCoords = {

            0,0,
            0,1,
            1,1,
            1,0,
            0,0,
            0,1,
            1,1,
            1,0,
            0,0,
            0,1,
            1,1,
            1,0,
            0,0,
            0,1,
            1,1,
            1,0,
            0,0,
            0,1,
            1,1,
            1,0,
            0,0,
            0,1,
            1,1,
            1,0


    };

}
