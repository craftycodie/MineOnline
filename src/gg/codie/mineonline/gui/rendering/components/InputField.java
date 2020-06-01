package gg.codie.mineonline.gui.rendering.components;

import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

public class InputField extends GUIObject {

    Vector2f position;

    public String getValue() {
        return value;
    }

    String value;

    public InputField(String name, Vector2f position, String value) {
        super(name,
                new TexturedModel(Loader.singleton.loadGUIToVAO(new Vector2f(position.x, Display.getHeight() - position.y), new Vector2f(400, 40), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(200, 0), new Vector2f(200, 20))), new ModelTexture(Loader.singleton.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")))),
                new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1)
        );

        this.position = new Vector2f(position.x, Display.getHeight() - position.y);
        this.value = value;
    }

    public void render(Renderer renderer, GUIShader shader) {
        shader.start();
        renderer.renderGUI(this, shader);
        shader.stop();
        renderer.renderString(new Vector2f(position.x + 8, (Display.getHeight() - position.y) - 32), this.value, Color.white);
    }

    boolean focused = true;
    boolean mouseWasDown = false;
    boolean mouseWasOver = false;
    public void update() {
        int x = Mouse.getX();
        int y = Mouse.getY();

        if(!Mouse.isButtonDown(0) && mouseWasDown) {
            mouseWasDown = false;
        }

        if(focused) {
            while (Keyboard.next()) {
                if (Keyboard.getEventKeyState()) {
                    if (Keyboard.getEventKey() == Keyboard.KEY_BACK) { //Backspace
                        if (value.length() > 0) {
                            value = value.substring(0, value.length() - 1);
                        }
                    } else {
                        value += Keyboard.getEventCharacter();
                    }
                }
            }
        }

        boolean mouseIsOver = x - position.x <= 400 && x - position.x >= 0 && y - position.y <= 40 && y - position.y >= 0;

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
