package gg.codie.mineonline.gui.rendering.components;

import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

public class LargeButton extends GUIObject {

    Vector2f position;
    IOnClickListener clickListener;

    public LargeButton(String name, Vector2f position, IOnClickListener clickListener) {
        super(name,
                new TexturedModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(400), DisplayManager.scaledHeight(40)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 20), new Vector2f(200, 20))), new ModelTexture(Loader.singleton.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")))),
                new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1)
        );

        this.position = new Vector2f(position.x, position.y);
        this.clickListener = clickListener;
    }

    public void render(Renderer renderer, GUIShader shader) {
        shader.start();
        renderer.renderGUI(this, shader);
        shader.stop();
        //renderer.renderCenteredString(new Vector2f(position.x + 202, (Display.getDefaultHeight() - position.y) - 31), 16, this.name, Color.black);
        renderer.renderCenteredString(new Vector2f(position.x + 200,  position.y - 32), this.name, mouseWasOver ? new Color(1, 1, 0.627f, 1) : Color.white);
    }

    public void resize() {
        this.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(400), DisplayManager.scaledHeight(40)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 20), new Vector2f(200, 20))));
    }

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

            RawModel model = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(400), DisplayManager.scaledHeight(40)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(200, 20), new Vector2f(200, 20)));
            ModelTexture texture = new ModelTexture(Loader.singleton.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")));
            this.model = new TexturedModel(model, texture);

        } else if(!mouseIsOver && mouseWasOver) {
            mouseWasOver = false;

            RawModel model = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(400), DisplayManager.scaledHeight(40)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 20), new Vector2f(200, 20)));
            ModelTexture texture = new ModelTexture(Loader.singleton.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")));
            this.model = new TexturedModel(model, texture);
        }

        if (mouseWasDown || !Mouse.isButtonDown(0)) return;

        if(mouseIsOver && clickListener != null) {
            clickListener.onClick();
        }

        if(Mouse.isButtonDown(0) && !mouseWasDown) {
            mouseWasDown = true;
        }
    }

}
