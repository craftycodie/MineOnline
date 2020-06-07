package gg.codie.mineonline.gui.rendering.components;

import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.shaders.SelectableVersionShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import java.nio.file.Paths;

public class SelectableVersion extends GUIObject {

    Vector2f position;

    String versionName;
    String path;
    String info;

    GUIText nameText;
    GUIText pathText;
    GUIText infoText;

    private SelectableVersionList parent;

    public SelectableVersion(String name, Vector2f position, String versionName, String path, String info, SelectableVersionList parent) {
        super(name,
                new TexturedModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(440), DisplayManager.scaledHeight(72)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 130), new Vector2f(220, 36))), new ModelTexture(Loader.singleton.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")))),
                new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1)
        );

        this.position = new Vector2f(position.x, position.y);
        this.versionName = versionName;
        this.path = path;
        this.parent = parent;
        this.info = info;

        nameText = new GUIText(this.versionName, 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 8, position.y - 70), 440, false);
        nameText.setYBounds(new Vector2f(69 , 69));

        String jarName = Paths.get(path).getFileName().toString();

        if(this.info != null) {
            infoText = new GUIText(this.info, 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 8, position.y - 48), 440, false);
            infoText.setColour(0.7F, 0.7F, 0.7F);
            infoText.setYBounds(new Vector2f(69 , 69));

            pathText = new GUIText(jarName, 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 8, position.y - 26), 440, false);
            pathText.setColour(0.5F, 0.5F, 0.5F);
            pathText.setYBounds(new Vector2f(69 , 69));

        } else {
            pathText = new GUIText(jarName, 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 8, position.y - 48), 440, false);
            pathText.setColour(0.5F, 0.5F, 0.5F);
            pathText.setYBounds(new Vector2f(69 , 69));
        }
    }

    public void render(Renderer renderer, GUIShader shader) {
        if(focused) {
            SelectableVersionShader selectableVersionShader = new SelectableVersionShader();
            selectableVersionShader.start();
            selectableVersionShader.loadViewMatrix(Camera.singleton);
            renderer.renderGUI(this, selectableVersionShader);
            selectableVersionShader.stop();
        }
    }

    public void resize() {
        this.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(440), DisplayManager.scaledHeight(72)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 130), new Vector2f(220, 36))));
    }

    boolean focused = false;
    boolean mouseWasDown = false;
    boolean mouseWasOver = false;
    public void update() {
        int x = Mouse.getX();
        int y = Mouse.getY();

        if(!Mouse.isButtonDown(0) && mouseWasDown) {
            mouseWasDown = false;
        }

        boolean mouseIsOver =
                x - (DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer()) <= DisplayManager.scaledWidth(440)
                        && x - (DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer()) >= 0
                        && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) - DisplayManager.getYBuffer() <= DisplayManager.scaledHeight(72)
                        && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) - DisplayManager.getYBuffer() >= 0;

        if (mouseIsOver && !mouseWasOver) {
            mouseWasOver = true;
        } else if(!mouseIsOver && mouseWasOver) {
            mouseWasOver = false;
        }

        if (mouseWasDown || !Mouse.isButtonDown(0)) return;

        if(Mouse.isButtonDown(0) && mouseIsOver) {
            parent.selectVersion(path);
        }

        if(Mouse.isButtonDown(0) && !mouseWasDown) {
            mouseWasDown = true;
        }
    }

    @Override
    public void translate(Vector3f localPosition) {
        super.translate(localPosition);
        position = position.translate(localPosition.x, -localPosition.y * 200);
    }

    public void cleanUp() {
        nameText.remove();
        pathText.remove();
        if (infoText != null)
            infoText.remove();
    }
}
