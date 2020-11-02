package gg.codie.mineonline.gui.rendering;

import gg.codie.minecraft.client.Options;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.gui.rendering.animation.IPlayerAnimation;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import gg.codie.mineonline.gui.rendering.utils.MathUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class PlayerGameObject extends GameObject {
    public static PlayerGameObject thePlayer;

    private final Loader loader;

    private final int SKIN_WIDTH = 64;
    private final int SKIN_HEIGHT = 64;

    private URL skinPath = LauncherFiles.TEMPLATE_SKIN_PATH;
    private URL cloakPath = LauncherFiles.TEMPLATE_CLOAK_PATH;
    boolean slim;

    boolean hat = true;
    boolean jacket = true;
    boolean leftSleeve = true;
    boolean rightSleeve = true;
    boolean leftPantsLeg = true;
    boolean rightPantsLeg = true;

    private IPlayerAnimation playerAnimation = new IdlePlayerAnimation();

    public void setPlayerAnimation(IPlayerAnimation animation) {
        this.playerAnimation = animation;
        this.playerAnimation.reset(this);
    }

    public IPlayerAnimation getPlayerAnimation() {
        return this.playerAnimation;
    }

    public PlayerGameObject(String name, Loader loader, Vector3f localPosition, Vector3f rotation, Vector3f scale)
    {
        super(name);

        hat = Settings.singleton.getShowHat();
        jacket = Settings.singleton.getShowJacket();
        leftSleeve = Settings.singleton.getShowLeftSleeve();
        rightSleeve = Settings.singleton.getShowRightSleeve();
        leftPantsLeg = Settings.singleton.getShowLeftPantsLeg();
        rightPantsLeg = Settings.singleton.getShowRightPantsLeg();

        thePlayer = this;

        this.loader = loader;

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


        playerHeadwear = addLayer(playerHead, "hat", new Vector3f(-4f, -8f, -4f), 8, 8, 8, TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(56, 8), new Vector2f(8, 8),
                new Vector2f(40, 8), new Vector2f(8, 8),
                new Vector2f(48, 8), new Vector2f(8, 8),
                new Vector2f(32, 8), new Vector2f(8, 8),
                new Vector2f(40, 0), new Vector2f(8, 8),
                new Vector2f(48, 0), new Vector2f(8, 8)
        ));

        playerBody = addBox("body", new Vector3f(-4, 0, 2), 8, 12, 4, new Vector3f(0, -4, -4), new Vector3f(0, 16, 0), TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(32, 20), new Vector2f(8, 12),
                new Vector2f(20, 20), new Vector2f(8, 12),
                new Vector2f(28, 20), new Vector2f(4, 12),
                new Vector2f(16, 20), new Vector2f(4, 12),
                new Vector2f(20, 16), new Vector2f(8, 4),
                new Vector2f(28, 16), new Vector2f(8, 4)
        ));

        playerJacket = addLayer(playerBody, "jacket", new Vector3f(-4f, -1, -2), 8, 12, 4, TextureHelper.getCubeTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(32, 36), new Vector2f(8, 12),
                new Vector2f(20, 36), new Vector2f(8, 12),
                new Vector2f(28, 36), new Vector2f(4, 12),
                new Vector2f(16, 36), new Vector2f(4, 12),
                new Vector2f(20, 32), new Vector2f(8, 4),
                new Vector2f(28, 32), new Vector2f(8, 4)
        ));

        playerRightArm = addBox("rightarm", new Vector3f(-3, -2, -2), 4, 12, 4, new Vector3f(2, -10, 0), new Vector3f(5, 24, 0), TextureHelper.getRightLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(52, 20), new Vector2f(4, 12),
                new Vector2f(44, 20), new Vector2f(4, 12),
                new Vector2f(48, 20), new Vector2f(4, 12),
                new Vector2f(40, 20), new Vector2f(4, 12),
                new Vector2f(44, 16), new Vector2f(4, 4),
                new Vector2f(48, 16), new Vector2f(4, 4)
        ));

        playerRightSleeve = addLayer(playerRightArm, "rightsleeve", new Vector3f(-1, -8, -2), 4, 12, 4, TextureHelper.getRightLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(52, 36), new Vector2f(4, 12),
                new Vector2f(44, 36), new Vector2f(4, 12),
                new Vector2f(48, 36), new Vector2f(4, 12),
                new Vector2f(40, 36), new Vector2f(4, 12),
                new Vector2f(44, 32), new Vector2f(4, 4),
                new Vector2f(48, 32), new Vector2f(4, 4)
        ));

        playerLeftArm = addBox("leftarm", new Vector3f(-1, -2, -2), 4, 12, 4, new Vector3f(-2, -10, 0), new Vector3f(-5, 24, 0), TextureHelper.getRightLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(44, 52), new Vector2f(4, 12),
                new Vector2f(36, 52), new Vector2f(4, 12),
                new Vector2f(40, 52), new Vector2f(4, 12),
                new Vector2f(32, 52), new Vector2f(4, 12),
                new Vector2f(36, 48), new Vector2f(4, 4),
                new Vector2f(40, 48), new Vector2f(4, 4)
        ));

        playerLeftSleeve = addLayer(playerLeftArm, "leftSleeve", new Vector3f(-3, -8, -2), 4, 12, 4, TextureHelper.getRightLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(60, 52), new Vector2f(4, 12),
                new Vector2f(52, 52), new Vector2f(4, 12),
                new Vector2f(56, 52), new Vector2f(4, 12),
                new Vector2f(48, 52), new Vector2f(4, 12),
                new Vector2f(52, 48), new Vector2f(4, 4),
                new Vector2f(56, 48), new Vector2f(4, 4)
        ));

        playerRightLeg = addBox("rightleg", new Vector3f(-2, 0, -2), 4 , 12, 4, new Vector3f(-2, -12, 0), new Vector3f(4, 12, 0), TextureHelper.getRightLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(28, 52), new Vector2f(4, 12),
                new Vector2f(20, 52), new Vector2f(4, 12),
                new Vector2f(24, 52), new Vector2f(4, 12),
                new Vector2f(16, 52), new Vector2f(4, 12),
                new Vector2f(20, 48), new Vector2f(4, 4),
                new Vector2f(24, 48), new Vector2f(4, 4)
        ));

        playerRightPantsLeg = addLayer(playerRightLeg, "rightpantsleg", new Vector3f(-3.9f, -8, -2), 4 , 12, 4, TextureHelper.getRightLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(12, 36), new Vector2f(4, 12),
                new Vector2f(4, 36), new Vector2f(4, 12),
                new Vector2f(8, 36), new Vector2f(4, 12),
                new Vector2f(0, 36), new Vector2f(4, 12),
                new Vector2f(4, 32), new Vector2f(4, 4),
                new Vector2f(8, 32), new Vector2f(4, 4)
        ));

        playerLeftLeg = addBox("leftleg", new Vector3f(-2, 0, -2), 4 , 12, 4, new Vector3f(2, -12, 0 ), new Vector3f(-4, 12, 0),  TextureHelper.getLeftLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(12, 20), new Vector2f(4, 12),
                new Vector2f(4, 20), new Vector2f(4, 12),
                new Vector2f(8, 20), new Vector2f(4, 12),
                new Vector2f(0, 20), new Vector2f(4, 12),
                new Vector2f(4, 16), new Vector2f(4, 4),
                new Vector2f(8, 16), new Vector2f(4, 4)
        ));

        playerLeftPantsLeg = addLayer(playerLeftLeg, "leftpantsleg", new Vector3f(-0.1f, -8, -2), 4 , 12, 4, TextureHelper.getRightLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                new Vector2f(12, 52), new Vector2f(4, 12),
                new Vector2f(4, 52), new Vector2f(4, 12),
                new Vector2f(8, 52), new Vector2f(4, 12),
                new Vector2f(0, 52), new Vector2f(4, 12),
                new Vector2f(4, 48), new Vector2f(4, 4),
                new Vector2f(8, 48), new Vector2f(4, 4)
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

    public PlayerGameObject(String name, Loader loader) {
        this(name, loader, new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1));
    }

    public void setSkin(URL path) {
        this.skinPath = path;
        updateSkin = true;
    }

    public void setCloak(URL path) {
        this.cloakPath = path;
        updateCloak = true;
    }

    public void setSlim(boolean slim) {
        this.slim = slim;
        updateSlim = true;
    }

    public void setSkinCustomization(boolean hat, boolean jacket, boolean leftSleeve, boolean rightSleeve, boolean leftPantsLeg, boolean rightPantsLeg) {
        this.hat = hat;
        this.jacket = jacket;
        this.leftSleeve = leftSleeve;
        this.rightSleeve = rightSleeve;
        this.leftPantsLeg = leftPantsLeg;
        this.rightPantsLeg = rightPantsLeg;
        updateSkinCustomization = true;
    }

    public boolean getSlim() {
        return slim;
    }

    private void loadSkinCustomization() {
        playerLeftSleeve.getModel().setDontRender(!leftSleeve);
        playerRightSleeve.getModel().setDontRender(!rightSleeve);
        playerLeftPantsLeg.getModel().setDontRender(!leftPantsLeg);
        playerRightPantsLeg.getModel().setDontRender(!rightPantsLeg);
        playerJacket.getModel().setDontRender(!jacket);
        playerHeadwear.getModel().setDontRender(!hat);
    }

    private void loadSlim() {
        TexturedModel newModel;
        if(slim) {
            newModel = getBoxModel(new Vector3f(-3, -2, -2), 3, 12, 4, TextureHelper.getRightLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                    new Vector2f(50, 20), new Vector2f(4, 12),
                    new Vector2f(44, 20), new Vector2f(3, 12),
                    new Vector2f(47, 20), new Vector2f(3, 12),
                    new Vector2f(40, 20), new Vector2f(4, 12),
                    new Vector2f(44, 16), new Vector2f(3, 4),
                    new Vector2f(47, 16), new Vector2f(3, 4)
            ));
            newModel.setTexture(playerRightArm.getChildren().getFirst().getModel().getTexture());
            playerRightArm.getChildren().getFirst().setModel(newModel);

            newModel = getBoxModel(new Vector3f(-1, -8, -2), 3, 12, 4, TextureHelper.getRightLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                    new Vector2f(40, 36), new Vector2f(4, 12),
                    new Vector2f(44, 36), new Vector2f(3, 12),
                    new Vector2f(47, 36), new Vector2f(3, 12),
                    new Vector2f(50, 36), new Vector2f(4, 12),
                    new Vector2f(44, 32), new Vector2f(3, 4),
                    new Vector2f(47, 32), new Vector2f(3, 4)
            ));
            newModel.setTexture(playerRightSleeve.getModel().getTexture());
            playerRightSleeve.setModel(newModel);

            newModel = getBoxModel(new Vector3f(0, -2, -2), 3, 12, 4, TextureHelper.getRightLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                    new Vector2f(42, 52), new Vector2f(4, 12),
                    new Vector2f(36, 52), new Vector2f(3, 12),
                    new Vector2f(39, 52), new Vector2f(3, 12),
                    new Vector2f(32, 52), new Vector2f(4, 12),
                    new Vector2f(36, 48), new Vector2f(3, 4),
                    new Vector2f(39, 48), new Vector2f(3, 4)
            ));
            newModel.setTexture(playerLeftArm.getChildren().getFirst().getModel().getTexture());
            playerLeftArm.getChildren().getFirst().setModel(newModel);

            newModel = getBoxModel(new Vector3f(-2, -8, -2), 3, 12, 4, TextureHelper.getRightLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                    new Vector2f(48, 52), new Vector2f(4, 12),
                    new Vector2f(52, 52), new Vector2f(3, 12),
                    new Vector2f(55, 52), new Vector2f(3, 12),
                    new Vector2f(58, 52), new Vector2f(4, 12),
                    new Vector2f(52, 48), new Vector2f(3, 4),
                    new Vector2f(54, 48), new Vector2f(3, 4)
            ));
            newModel.setTexture(playerLeftSleeve.getModel().getTexture());
            playerLeftSleeve.setModel(newModel);
        } else {
            newModel = getBoxModel(new Vector3f(-3, -2, -2), 4, 12, 4, TextureHelper.getRightLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                    new Vector2f(52, 20), new Vector2f(4, 12),
                    new Vector2f(44, 20), new Vector2f(4, 12),
                    new Vector2f(48, 20), new Vector2f(4, 12),
                    new Vector2f(40, 20), new Vector2f(4, 12),
                    new Vector2f(44, 16), new Vector2f(4, 4),
                    new Vector2f(48, 16), new Vector2f(4, 4)
            ));
            newModel.setTexture(playerRightArm.getChildren().getFirst().getModel().getTexture());
            playerRightArm.getChildren().getFirst().setModel(newModel);

            newModel = getBoxModel(new Vector3f(-1, -8, -2), 4, 12, 4, TextureHelper.getLeftLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                    new Vector2f(52, 36), new Vector2f(4, 12),
                    new Vector2f(44, 36), new Vector2f(4, 12),
                    new Vector2f(48, 36), new Vector2f(4, 12),
                    new Vector2f(40, 36), new Vector2f(4, 12),
                    new Vector2f(44, 32), new Vector2f(4, 4),
                    new Vector2f(47, 32), new Vector2f(4, 4)
            ));
            newModel.setTexture(playerRightSleeve.getModel().getTexture());
            playerRightSleeve.setModel(newModel);

            newModel = getBoxModel(new Vector3f(-1, -2, -2), 4, 12, 4, TextureHelper.getLeftLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                    new Vector2f(44, 52), new Vector2f(4, 12),
                    new Vector2f(36, 52), new Vector2f(4, 12),
                    new Vector2f(40, 52), new Vector2f(4, 12),
                    new Vector2f(32, 52), new Vector2f(4, 12),
                    new Vector2f(36, 48), new Vector2f(4, 4),
                    new Vector2f(40, 48), new Vector2f(4, 4)
            ));
            newModel.setTexture(playerLeftArm.getChildren().getFirst().getModel().getTexture());
            playerLeftArm.getChildren().getFirst().setModel(newModel);

            newModel = getBoxModel(new Vector3f(-3, -8, -2), 4, 12, 4, TextureHelper.getLeftLimbTextureCoords(new Vector2f(SKIN_WIDTH, SKIN_HEIGHT),
                    new Vector2f(60, 52), new Vector2f(4, 12),
                    new Vector2f(52, 52), new Vector2f(4, 12),
                    new Vector2f(56, 52), new Vector2f(4, 12),
                    new Vector2f(48, 52), new Vector2f(4, 12),
                    new Vector2f(52, 48), new Vector2f(4, 4),
                    new Vector2f(55, 48), new Vector2f(4, 4)
            ));
            newModel.setTexture(playerLeftSleeve.getModel().getTexture());
            playerLeftSleeve.setModel(newModel);
        }
    }

    boolean updateSkin;
    boolean updateCloak;
    boolean updateSlim;
    boolean updateSkinCustomization;

    public void update() {
        if(updateSkin) {
            loadSkin();
            updateSkin = false;
        }

        if(updateCloak) {
            loadCloak();
            updateCloak = false;
        }

        if(updateSlim) {
            loadSlim();
            loadSkinCustomization();
            updateSlim = false;
        }

        if(updateSkinCustomization) {
            loadSkinCustomization();
            updateSkinCustomization = false;
        }

        playerAnimation.animate(this);
    }

    private GameObject addBox(String name, Vector3f begin, int width, int height, int depth, Vector3f position, Vector3f pivotPosition, float[] textureCoords) {
        TexturedModel texturedModel = getBoxModel(begin, width, height, depth, textureCoords);

        GameObject box = new GameObject(name, texturedModel, position, new Vector3f(), new Vector3f(1, 1, 1));

        GameObject pivot = new GameObject(name + " pivot", pivotPosition, new Vector3f(), new Vector3f(1, 1, 1));

        addChild(pivot);
        pivot.addChild(box);

        return pivot;
    }

    private TexturedModel getBoxModel(Vector3f begin, int width, int height, int depth, float[] textureCoords) {
        RawModel model = loader.loadBoxToVAO(begin,
                new Vector3f(begin.x + width, begin.y + height, begin.z + depth),
                textureCoords);

        ModelTexture modelTexture;

        modelTexture = getSkinModelTexture();

        TexturedModel texturedModel =  new TexturedModel(model, modelTexture);

        return texturedModel;
    }

    private GameObject addLayer(GameObject parent, String name, Vector3f begin, int width, int height, int depth, float[] textureCoords) {
        RawModel model = loader.loadBoxToVAO(begin,
                new Vector3f(begin.x + width, begin.y + height, begin.z + depth),
                textureCoords);

        ModelTexture modelTexture;

        modelTexture = getSkinModelTexture();

        TexturedModel texturedModel =  new TexturedModel(model, modelTexture);

        GameObject box = new GameObject(name, texturedModel, new Vector3f(0, -3.5f, 0), new Vector3f(), new Vector3f(1, 1, 1));

        parent.addChild(box);

        box.scale(new Vector3f(1.1f, 1.1f, 1.1f));

        return box;
    }

    private GameObject addCape(String name, Vector3f begin, int width, int height, int depth, Vector3f position, Vector3f pivotPosition,  float[] textureCoords) {
        RawModel model = loader.loadBoxToVAO(begin,
                new Vector3f(begin.x + width, begin.y + height, begin.z + depth),
                textureCoords);

        ModelTexture modelTexture;

        modelTexture = getCloakModelTexture();

        TexturedModel texturedModel = new TexturedModel(model, modelTexture);

        GameObject box = new GameObject(name,texturedModel, position, new Vector3f(), new Vector3f(1, 1, 1));

        GameObject pivot = new GameObject("pivot", pivotPosition, new Vector3f(), new Vector3f(1, 1, 1));

        addChild(pivot);
        pivot.addChild(box);

        return pivot;
    }

    private ModelTexture getSkinModelTexture() {
        ModelTexture skin;

        try(InputStream inputStream = skinPath.openStream()) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            bufferedImage = TextureHelper.convertSkin(bufferedImage);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            skin = new ModelTexture(loader.loadTexture(new ByteArrayInputStream(os.toByteArray())));
        } catch(Exception ex) {
            // The texture loader can handle this error and return the missing texture ID.
            skin = new ModelTexture(loader.loadTexture(""));
        }

        return skin;
    }

    private ModelTexture getCloakModelTexture() {
        ModelTexture cloak;

        try(InputStream inputStream = cloakPath.openStream()) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            bufferedImage = TextureHelper.cropImage(bufferedImage,0, 0, 64, 32);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            cloak = new ModelTexture(loader.loadTexture(new ByteArrayInputStream(os.toByteArray())));
        } catch(Exception ex) {
            // The texture loader can handle this error and return the missing texture ID.
            cloak = new ModelTexture(loader.loadTexture(""));
        }

        return cloak;
    }

    private void loadSkin() {
        int oldTextureID = playerHead.getChildren().getFirst().getModel().getTexture().getTextureID();

        ModelTexture skin = getSkinModelTexture();

        playerHead.getChildren().getFirst().getModel().setTexture(skin);
        playerHeadwear.getModel().setTexture(skin);
        playerBody.getChildren().getFirst().getModel().setTexture(skin);
        playerJacket.getModel().setTexture(skin);
        playerRightArm.getChildren().getFirst().getModel().setTexture(skin);
        playerRightSleeve.getModel().setTexture(skin);
        playerLeftArm.getChildren().getFirst().getModel().setTexture(skin);
        playerLeftSleeve.getModel().setTexture(skin);
        playerRightLeg.getChildren().getFirst().getModel().setTexture(skin);
        playerRightPantsLeg.getModel().setTexture(skin);
        playerLeftLeg.getChildren().getFirst().getModel().setTexture(skin);
        playerLeftPantsLeg.getModel().setTexture(skin);

        GL11.glDeleteTextures(oldTextureID);
    }

    private void loadCloak() {
        int oldTextureID = playerCloak.getChildren().getFirst().getModel().getTexture().getTextureID();

        ModelTexture cloak = getCloakModelTexture();

        playerCloak.getChildren().getFirst().getModel().setTexture(cloak);

        GL11.glDeleteTextures(oldTextureID);
    }

    public GameObject playerHead;
    public GameObject playerHeadwear;
    public GameObject playerBody;
    public GameObject playerJacket;
    public GameObject playerRightArm;
    public GameObject playerRightSleeve;
    public GameObject playerLeftArm;
    public GameObject playerLeftSleeve;
    public GameObject playerRightLeg;
    public GameObject playerRightPantsLeg;
    public GameObject playerLeftLeg;
    public GameObject playerLeftPantsLeg;
    public GameObject playerCloak;
}
