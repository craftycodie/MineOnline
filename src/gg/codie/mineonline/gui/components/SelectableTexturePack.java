package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.MouseHandler;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.SelectableVersionShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class SelectableTexturePack extends GUIObject {

    Vector2f originalPosition;
    Vector2f currentPosition;

    String texturePackName;
    String info;

    GUIText nameText;
    GUIText infoText;
    GUIObject icon;

    private SelectableTexturePackList parent;
    private IOnClickListener doubleClickListener;

    public SelectableTexturePack(String name, Vector2f position, String texturePackName, String info, SelectableTexturePackList parent, int packIcon, IOnClickListener doubleClickListener) {
        super(name,
                new TexturedModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(440), DisplayManager.scaledHeight(72)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 130), new Vector2f(220, 36))), new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")))),
                new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1)
        );

        RawModel logoModel = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x + 4) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (position.y - 4)) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(64), DisplayManager.scaledHeight(64)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 0), new Vector2f(512, 512)));
        ModelTexture iconTexture = new ModelTexture(packIcon == -1 ? Loader.singleton.loadTexture(MenuManager.class.getResource("/img/unknown_pack.png")) : packIcon);
        TexturedModel texuredLogoModel =  new TexturedModel(logoModel, iconTexture);
        icon = new GUIObject(name + " icon", texuredLogoModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

        this.originalPosition = new Vector2f(position.x, position.y);
        this.currentPosition = position;
        this.texturePackName = texturePackName;
        this.parent = parent;
        this.info = info;
        this.doubleClickListener = doubleClickListener;

        nameText = new GUIText(this.texturePackName.isEmpty() ? "Default" : this.texturePackName, 1.5f, TextMaster.minecraftFont, new Vector2f(currentPosition.x + 80, currentPosition.y - 70), 368, false, true);
        nameText.setYBounds(new Vector2f(69 , 69));

        if(this.info != null) {
            infoText = new GUIText(this.info, 1.5f, TextMaster.minecraftFont, new Vector2f(currentPosition.x + 80, currentPosition.y - 48), 368, false, true);
            infoText.setMaxLines(2);
            infoText.setColour(0.7F, 0.7F, 0.7F);
            infoText.setYBounds(new Vector2f(69 , 69));
        }
    }

    public void render(Renderer renderer) {
        SelectableVersionShader.singleton.start();
        SelectableVersionShader.singleton.loadViewMatrix(Camera.singleton);
        if (focused)
            renderer.renderGUI(this, SelectableVersionShader.singleton);

        renderer.renderGUI(icon, SelectableVersionShader.singleton);
        SelectableVersionShader.singleton.stop();
    }

    public void resize() {
        this.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(currentPosition.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - currentPosition.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(440), DisplayManager.scaledHeight(72)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 130), new Vector2f(220, 36))));
        this.icon.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(currentPosition.x + 4) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (currentPosition.y - 4)) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(64), DisplayManager.scaledHeight(64)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 0), new Vector2f(512, 512))));
    }

    boolean focused = false;
    long lastClickTime;
    boolean mouseWasOver = false;
    public void update() {
        int x = Mouse.getX();
        int y = Mouse.getY();

        float viewportHeight = Display.getHeight() - DisplayManager.scaledHeight(138) - (DisplayManager.getYBuffer() * 2);
        float viewportStartY = DisplayManager.getYBuffer() + DisplayManager.scaledHeight(69) + viewportHeight;
        
        boolean mouseIsOver =
                x - (DisplayManager.scaledWidth(currentPosition.x) + DisplayManager.getXBuffer()) <= DisplayManager.scaledWidth(440)
                        && x - (DisplayManager.scaledWidth(currentPosition.x) + DisplayManager.getXBuffer()) >= 0
                        && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - currentPosition.y) - DisplayManager.getYBuffer() <= DisplayManager.scaledHeight(72)
                        && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - currentPosition.y) - DisplayManager.getYBuffer() >= 0;

        if(y > viewportStartY || y < viewportStartY - viewportHeight) {
            mouseIsOver = false;
        }

        if (mouseIsOver && !mouseWasOver) {
            mouseWasOver = true;
        } else if(!mouseIsOver && mouseWasOver) {
            mouseWasOver = false;
        }

        if(MouseHandler.didClick() && mouseIsOver) {
            parent.selectTexturePack(texturePackName);

            if(System.currentTimeMillis() - lastClickTime < 350 && doubleClickListener != null) {
                doubleClickListener.onClick();
            }

            lastClickTime = System.currentTimeMillis();
        }
    }

    public void scroll(float yOffset) {
        currentPosition = new Vector2f(originalPosition.x, originalPosition.y + (yOffset / (float)DisplayManager.getScale()));

        this.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(currentPosition.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - currentPosition.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(440), DisplayManager.scaledHeight(72)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 130), new Vector2f(220, 36))));
        this.icon.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(currentPosition.x + 4) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (currentPosition.y - 4)) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(64), DisplayManager.scaledHeight(64)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 0), new Vector2f(512, 512))));

        nameText.setPosition(new Vector2f(currentPosition.x + 80, currentPosition.y - 70));

        if(this.info != null) {
            infoText.setPosition(new Vector2f(currentPosition.x + 80, currentPosition.y - 48));
        }
    }

    public void cleanUp() {
        nameText.remove();
        if (infoText != null)
            infoText.remove();
    }
}
