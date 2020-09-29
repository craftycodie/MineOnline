package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.MouseHandler;
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

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class SmallInputField extends GUIObject {

    Vector2f position;

    public String getValue() {
        return value;
    }

    String value;
    IOnClickListener onEnterPressed;
    GUIText valueGuiText;
    GUIText cursor;
    int cursorPosition;

    public SmallInputField(String name, Vector2f position, String value, IOnClickListener onEnterPressed) {
        super(name,
                new TexturedModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(200), DisplayManager.scaledHeight(44)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 188), new Vector2f(101, 22))), new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")))),
                new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1)
        );

        this.position = new Vector2f(position.x, position.y);
        this.value = value;
        this.onEnterPressed = onEnterPressed;

        valueGuiText = new GUIText(value, 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 12, position.y - 32), Float.MAX_VALUE, false, true);
        cursor = new GUIText("_", 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 12 + valueGuiText.getLineLength(), position.y - 32), Float.MAX_VALUE, false, true);
        cursorPosition = value.length();    }

    public void render(Renderer renderer, GUIShader shader) {
        shader.start();
        renderer.renderGUI(this, shader);
        shader.stop();

        long diff = System.currentTimeMillis() % 600;

        if(!this.valueGuiText.textString.equals(this.value)) {
            valueGuiText.remove();
            valueGuiText = new GUIText(this.value, 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 12, position.y - 32), 400f, false, true);
        }

        if(focused && diff >= 300) {
            GUIText valueUpToCursor = new GUIText(value.substring(0, cursorPosition), 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 12, position.y - 32), 400f, false, true);
            if(cursor.getPosition().x != position.x + 12 + valueUpToCursor.getLineLength() || !TextMaster.hasText(cursor)) {
                String caret = cursorPosition == value.length() ? "_" : "|";
                cursor.remove();
                cursor = new GUIText(caret, 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 12 + valueUpToCursor.getLineLength(), position.y - 32), 400f, false, true);
            }
            valueUpToCursor.remove();
        } else {
            cursor.remove();
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
                    if (Keyboard.getEventKey() == Keyboard.KEY_V && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))) {
                        try {
                            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                            Transferable t = c.getContents(this);
                            String paste = "" + t.getTransferData(DataFlavor.stringFlavor);
                            StringBuilder stringBuilder = new StringBuilder();
                            char[] chars = new char[paste.length()];
                            paste.getChars(0, paste.length(), chars, 0);
                            for (char character : chars) {
                                if(("" + character).matches("[A-Za-z0-9\\[\\]{}'#@~./,<>?;:\\-\\\\()&^$%£\"!*&=_@~`¬¦| ]+")) {
                                    stringBuilder.append(character);
                                }
                            }
                            paste = stringBuilder.toString();
                            value = value.substring(0, cursorPosition) + paste + value.substring(cursorPosition);
                            cursorPosition += paste.length();
                        } catch (Exception ex) {
                            // Ignore.
                        }
                    } else if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
                        cursorPosition++;
                    } else if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
                        cursorPosition--;
                    } else if (Keyboard.getEventKey() == Keyboard.KEY_BACK) { //Backspace
                        if (value.length() > 0) {
                            value = value.substring(0, cursorPosition - 1) + value.substring(cursorPosition);
                            cursorPosition--;
                        }
                    } else if (Keyboard.getEventKey() == Keyboard.KEY_RETURN && this.onEnterPressed != null) {
                        this.onEnterPressed.onClick();
                    } else if (!Character.toString(Keyboard.getEventCharacter()).matches("[A-Za-z0-9\\[\\]{}'#@~./,<>?;:\\-\\\\()&^$%£\"!*&=_@~`¬¦| ]+")) {
                        continue;
                    } else {
                        value = value.substring(0, cursorPosition) +  Keyboard.getEventCharacter() + value.substring(cursorPosition);
                        cursorPosition++;
                    }
                }
            }
        }

        if(value.length() > 32)
            value = value.substring(0, 32);

        if(cursorPosition > value.length()) {
            cursorPosition = value.length();
        } else if (cursorPosition < 0) {
            cursorPosition = 0;
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
        this.valueGuiText.remove();
        this.cursor.remove();
    }

}
