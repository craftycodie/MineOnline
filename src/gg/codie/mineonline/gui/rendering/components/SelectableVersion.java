package gg.codie.mineonline.gui.rendering.components;

import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

public class SelectableVersion extends GUIObject {

    Vector2f position;

    String versionName;
    String path;

    public SelectableVersion(String name, Vector2f position, String versionName, String path) {
        super(name,
                new TexturedModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(440), DisplayManager.scaledHeight(72)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 130), new Vector2f(220, 36))), new ModelTexture(Loader.singleton.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")))),
                new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1)
        );

        this.position = new Vector2f(position.x, position.y);
        this.versionName = versionName;
        this.path = path;
    }

    public void render(Renderer renderer, GUIShader shader) {
//        if(focused) {
            shader.start();
            renderer.renderGUI(this, shader);
            shader.stop();
//        }
        //renderer.renderCenteredString(new Vector2f(position.x + 202, (Display.getDefaultHeight() - position.y) - 31), 16, this.name, Color.black);
        renderer.renderString(new Vector2f(position.x + 8,  position.y - 70), this.versionName, mouseWasOver ? new Color(1, 1, 0.627f, 1) : Color.white);
        renderer.renderString(new Vector2f(position.x + 8,  position.y - 48), this.path, mouseWasOver ? new Color(1, 1, 0.627f, 1) : Color.gray);
    }

    public void resize() {
        this.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(400), DisplayManager.scaledHeight(40)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 20), new Vector2f(200, 20))));
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
                x - (DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer()) <= DisplayManager.scaledWidth(400)
                        && x - (DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer()) >= 0
                        && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) - DisplayManager.getYBuffer() <= DisplayManager.scaledHeight(40)
                        && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) - DisplayManager.getYBuffer() >= 0;

        if (mouseIsOver && !mouseWasOver) {
            mouseWasOver = true;
        } else if(!mouseIsOver && mouseWasOver) {
            mouseWasOver = false;
        }

        if (mouseWasDown || !Mouse.isButtonDown(0)) return;

        if(Mouse.isButtonDown(0) && mouseIsOver) {
            focused = true;
        } else if (Mouse.isButtonDown(0) && !mouseIsOver) {
            focused = false;
        }

        if(Mouse.isButtonDown(0) && !mouseWasDown) {
            mouseWasDown = true;
        }
    }

}
