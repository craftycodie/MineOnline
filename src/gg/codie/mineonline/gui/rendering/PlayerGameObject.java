package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.gui.rendering.animation.IPlayerAnimation;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import gg.codie.mineonline.gui.rendering.utils.MathUtils;
import jdk.nashorn.api.scripting.URLReader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;

public class PlayerGameObject extends GameObject {

    public static PlayerGameObject thePlayer;

    private final Loader loader;
    private final StaticShader shader;

    private final int SKIN_WIDTH = 64;
    private final int SKIN_HEIGHT = 32;

    private URL skinPath = LauncherFiles.TEMPLATE_SKIN_PATH;
    private URL cloakPath = LauncherFiles.TEMPLATE_CLOAK_PATH;

    private IPlayerAnimation playerAnimation = new IdlePlayerAnimation();

    public void setPlayerAnimation(IPlayerAnimation animation) {
        this.playerAnimation = animation;
        this.playerAnimation.reset(this);
    }

    public PlayerGameObject(String name, Loader loader, StaticShader shader, Vector3f localPosition, Vector3f rotation, Vector3f scale)
    {
        super(name);

        thePlayer = this;

        this.loader = loader;
        this.shader = shader;

        Quaternion rotationQuaterion = new Quaternion();
        MathUtils.rotate(rotationQuaterion, rotation);

        this.localMatrix = MathUtils.createTransformationMatrix(localPosition, rotationQuaterion, scale);

        playerHead = addBox("head", new Vector3f(-4f, -8f, -4f), 8, 8, 8, new Vector3f(0, -4, 0), new Vector3f(0, 36, 0), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(24, 8), new Vector2f(8, 8),
                new Vector2f(8, 8), new Vector2f(8, 8),
                new Vector2f(16, 8), new Vector2f(8, 8),
                new Vector2f(0, 8), new Vector2f(8, 8),
                new Vector2f(8, 0), new Vector2f(8, 8),
                new Vector2f(16, 0), new Vector2f(8, 8)
                ));


        playerHeadwear = addHeadwear("hat", new Vector3f(-4f, -8f, -4f), 8, 8, 8, TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(56, 8), new Vector2f(8, 8),
                new Vector2f(40, 8), new Vector2f(8, 8),
                new Vector2f(48, 8), new Vector2f(8, 8),
                new Vector2f(32, 8), new Vector2f(8, 8),
                new Vector2f(40, 0), new Vector2f(8, 8),
                new Vector2f(48, 0), new Vector2f(8, 8)
        ));

        playerHeadwear.scale(new Vector3f(1.1f, 1.1f, 1.1f));

        playerBody = addBox("body", new Vector3f(-4, 0, 2), 8, 12, 4, new Vector3f(0, -4, -4), new Vector3f(0, 16, 0), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(32, 20), new Vector2f(8, 12),
                new Vector2f(20, 20), new Vector2f(8, 12),
                new Vector2f(28, 20), new Vector2f(4, 12),
                new Vector2f(16, 20), new Vector2f(4, 12),
                new Vector2f(20, 16), new Vector2f(8, 4),
                new Vector2f(28, 16), new Vector2f(8, 4)
        ));

        playerRightArm = addBox("rightarm", new Vector3f(-3, -2, -2), 4, 12, 4, new Vector3f(2, -10, 0), new Vector3f(5, 24, 0), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(52, 20), new Vector2f(4, 12),
                new Vector2f(44, 20), new Vector2f(4, 12),
                new Vector2f(48, 20), new Vector2f(4, 12),
                new Vector2f(40, 20), new Vector2f(4, 12),
                new Vector2f(44, 16), new Vector2f(4, 4),
                new Vector2f(48, 16), new Vector2f(4, 4)
        ));


        playerLeftArm = addBox("leftarm", new Vector3f(-1, -2, -2), 4, 12, 4, new Vector3f(-2, -10, 0), new Vector3f(-5, 24, 0), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(52, 20), new Vector2f(4, 12),
                new Vector2f(44, 20), new Vector2f(4, 12),
                new Vector2f(48, 20), new Vector2f(4, 12),
                new Vector2f(40, 20), new Vector2f(4, 12),
                new Vector2f(44, 16), new Vector2f(4, 4),
                new Vector2f(48, 16), new Vector2f(4, 4)
        ));


        playerRightLeg = addBox("rightleg", new Vector3f(-2, 0, -2), 4 , 12, 4, new Vector3f(-2, -12, 0), new Vector3f(4, 12, 0), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(12, 20), new Vector2f(4, 12),
                new Vector2f(4, 20), new Vector2f(4, 12),
                new Vector2f(8, 20), new Vector2f(4, 12),
                new Vector2f(0, 20), new Vector2f(4, 12),
                new Vector2f(4, 16), new Vector2f(4, 4),
                new Vector2f(8, 16), new Vector2f(4, 4)
        ));

        playerLeftLeg = addBox("leftleg", new Vector3f(-2, 0, -2), 4 , 12, 4, new Vector3f(2, -12, 0 ), new Vector3f(-4, 12, 0),  TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(12, 20), new Vector2f(4, 12),
                new Vector2f(4, 20), new Vector2f(4, 12),
                new Vector2f(8, 20), new Vector2f(4, 12),
                new Vector2f(0, 20), new Vector2f(4, 12),
                new Vector2f(4, 16), new Vector2f(4, 4),
                new Vector2f(8, 16), new Vector2f(4, 4)
        ));

        playerCloak = addCape("cloak", new Vector3f(-5f, 0f, -1f), 10, 16, 1, new Vector3f(0, -16, 0), new Vector3f(0, 24, -2), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, 32),
                new Vector2f(1, 1), new Vector2f(10, 16),
                new Vector2f(11, 1), new Vector2f(10, 16),
                new Vector2f(1, 0), new Vector2f(1, 16),
                new Vector2f(21, 1), new Vector2f(1, 16),
                new Vector2f(1, 0), new Vector2f(10, 1),
                new Vector2f(11, 0), new Vector2f(10, 1)
        ));
    }

    public PlayerGameObject(String name, Loader loader, StaticShader shader) {
        this(name, loader, shader, new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1));
    }

    public void setSkin(URL path) {
        this.skinPath = path;
        updateSkin = true;
    }

    public void setCloak(URL path) {
        this.cloakPath = path;
        updateCloak = true;
    }

    boolean updateSkin;
    boolean updateCloak;

    public void update() {
        if(updateSkin) {
            loadSkin(skinPath);
            updateSkin = false;
        }

        if(updateCloak) {
            loadCloak(cloakPath);
            updateCloak = false;
        }

        playerAnimation.animate(this);
    }

    private GameObject addBox(String name, Vector3f begin, int width, int height, int depth, Vector3f position, Vector3f pivotPosition, float[] textureCoords) {
        RawModel model = loader.loadBoxToVAO(begin,
                new Vector3f(begin.x + width, begin.y + height, begin.z + depth),
                textureCoords);

        ModelTexture modelTexture;

        modelTexture = new ModelTexture(loader.loadTexture(skinPath));

        TexturedModel texturedModel =  new TexturedModel(model, modelTexture);

        GameObject box = new GameObject(name, texturedModel, position, new Vector3f(), new Vector3f(1, 1, 1));

        GameObject pivot = new GameObject(name + " pivot", pivotPosition, new Vector3f(), new Vector3f(1, 1, 1));

        addChild(pivot);
        pivot.addChild(box);

        return pivot;
    }

    private GameObject addHeadwear(String name, Vector3f begin, int width, int height, int depth, float[] textureCoords) {
        RawModel model = loader.loadBoxToVAO(begin,
                new Vector3f(begin.x + width, begin.y + height, begin.z + depth),
                textureCoords);

        ModelTexture modelTexture;

        modelTexture = new ModelTexture(loader.loadTexture(skinPath));

        TexturedModel texturedModel =  new TexturedModel(model, modelTexture);

        GameObject box = new GameObject(name, texturedModel, new Vector3f(0, -3.5f, 0), new Vector3f(), new Vector3f(1, 1, 1));

        playerHead.addChild(box);

        return box;
    }

    private GameObject addCape(String name, Vector3f begin, int width, int height, int depth, Vector3f position, Vector3f pivotPosition,  float[] textureCoords) {
        RawModel model = loader.loadBoxToVAO(begin,
                new Vector3f(begin.x + width, begin.y + height, begin.z + depth),
                textureCoords);

        ModelTexture modelTexture;

        modelTexture = new ModelTexture(loader.loadTexture(cloakPath));

        TexturedModel texturedModel =  new TexturedModel(model, modelTexture);

        GameObject box = new GameObject(name,texturedModel, position, new Vector3f(), new Vector3f(1, 1, 1));

        GameObject pivot = new GameObject("pivot", pivotPosition, new Vector3f(), new Vector3f(1, 1, 1));

        addChild(pivot);
        pivot.addChild(box);

        return pivot;
    }

    private void loadSkin(URL path) {
        int oldTextureID = playerHead.getChildren().getFirst().getModel().getTexture().getTextureID();

        ModelTexture skin;

        try {
            BufferedImage bufferedImage = ImageIO.read(path.openStream());
            bufferedImage = TextureHelper.cropImage(bufferedImage, 0, 0, 64, 32);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            skin = new ModelTexture(loader.loadTexture(new ByteArrayInputStream(os.toByteArray())));
        } catch(Exception ex) {
            // The texture loader can handle this error and return the missing texture ID.
            skin = new ModelTexture(loader.loadTexture(""));
        }

        playerHead.getChildren().getFirst().getModel().setTexture(skin);
        playerHead.getChildren().getLast().getModel().setTexture(skin);
        playerBody.getChildren().getFirst().getModel().setTexture(skin);
        playerRightArm.getChildren().getFirst().getModel().setTexture(skin);
        playerLeftArm.getChildren().getFirst().getModel().setTexture(skin);
        playerRightLeg.getChildren().getFirst().getModel().setTexture(skin);
        playerLeftLeg.getChildren().getFirst().getModel().setTexture(skin);

        GL11.glDeleteTextures(oldTextureID);
    }

    private void loadCloak(URL path) {
        int oldTextureID = playerCloak.getChildren().getFirst().getModel().getTexture().getTextureID();

        ModelTexture cloak;

        try {
            BufferedImage bufferedImage = ImageIO.read(path.openStream());
            bufferedImage = TextureHelper.cropImage(bufferedImage, 0, 0, 64, 32);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            cloak = new ModelTexture(loader.loadTexture(new ByteArrayInputStream(os.toByteArray())));
        } catch(Exception ex) {
            // The texture loader can handle this error and return the missing texture ID.
            cloak = new ModelTexture(loader.loadTexture(""));
        }

        playerCloak.getChildren().getFirst().getModel().setTexture(cloak);

        GL11.glDeleteTextures(oldTextureID);
    }

    public GameObject playerHead;
    public GameObject playerHeadwear;
    public GameObject playerBody;
    public GameObject playerRightArm;
    public GameObject playerLeftArm;
    public GameObject playerRightLeg;
    public GameObject playerLeftLeg;
    public GameObject playerCloak;
}
