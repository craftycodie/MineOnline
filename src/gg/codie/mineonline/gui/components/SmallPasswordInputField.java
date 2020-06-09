package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.gui.MouseHandler;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class SmallPasswordInputField extends GUIObject {

    Vector2f position;

    public String getValue() {
        return value;
    }

    String value;
    IOnClickListener onEnterPressed;
    GUIText guiText;

    public SmallPasswordInputField(String name, Vector2f position, String value, IOnClickListener onEnterPressed) {
        super(name,
                new TexturedModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(200), DisplayManager.scaledHeight(44)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 188), new Vector2f(101, 22))), new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")))),
                new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1)
        );

        this.position = new Vector2f(position.x, position.y);
        this.value = value;
        this.onEnterPressed = onEnterPressed;

        guiText = new GUIText(this.value.replaceAll(".", "*"), 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 12, position.y - 32), 200f, false, true);
    }

    public void render(Renderer renderer, GUIShader shader) {
        shader.start();
        renderer.renderGUI(this, shader);
        shader.stop();

        long diff = System.currentTimeMillis() % 600;

        if(focused && diff >= 300 && !this.guiText.textString.equals(this.value.replaceAll(".", "*") + "_")) {
            guiText.remove();
            guiText = new GUIText(this.value.replaceAll(".", "*") + "_", 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 12, position.y - 32), 200f, false, true);
        } else if (diff < 300 && !this.guiText.textString.equals(this.value.replaceAll(".", "*"))) {
            guiText.remove();
            guiText = new GUIText(this.value.replaceAll(".", "*"), 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 12, position.y - 32), 200f, false, true);
        }

    }

    boolean focused = false;
    boolean mouseWasOver = false;
    public void update() {
        int x = Mouse.getX();
        int y = Mouse.getY();

        if(focused) {
            while (Keyboard.next()) {
                if (Keyboard.getEventKeyState()) {
                    if (Keyboard.getEventKey() == Keyboard.KEY_BACK) { //Backspace
                        if (value.length() > 0) {
                            value = value.substring(0, value.length() - 1);
                        }
                    } else if (Keyboard.getEventKey() == Keyboard.KEY_RETURN && this.onEnterPressed != null) {
                        this.onEnterPressed.onClick();
                    } else if (!Character.toString(Keyboard.getEventCharacter()).matches("[A-Za-z0-9\\[\\]{}'#@~./,<>?;:\\-\\\\()&^$%£\"!*&=_@~`¬¦|]+")) {
                        continue;
                    } else {
                        value += Keyboard.getEventCharacter();
                    }
                }
            }
        }

        boolean mouseIsOver =
               x - (DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer()) <= DisplayManager.scaledWidth(200)
            && x - (DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer()) >= 0
            && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) - DisplayManager.getYBuffer() <= DisplayManager.scaledHeight(40)
            && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) - DisplayManager.getYBuffer() >= 0;

        if (mouseIsOver && !mouseWasOver) {
            mouseWasOver = true;
        } else if(!mouseIsOver && mouseWasOver) {
            mouseWasOver = false;
        }

        if(MouseHandler.didClick() && mouseIsOver) {
            focused = true;
        } else if (MouseHandler.didClick() && !mouseIsOver) {
            focused = false;
        }
    }

    public void resize() {
        this.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(200), DisplayManager.scaledHeight(44)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 188), new Vector2f(101, 22))));
    }

    public void cleanUp() {
        this.guiText.remove();
    }

}
