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
import java.awt.event.KeyEvent;

public class PasswordInputField extends GUIObject {

    Vector2f position;

    public String getValue() {
        return value;
    }

    String value;
    IOnClickListener onEnterPressed;
    GUIText valueGuiText;
    GUIText cursor;
    int cursorPosition;
    int truncateLimit = 32;
    int inputLimit = 512;

    public PasswordInputField(String name, Vector2f position, String value, IOnClickListener onEnterPressed) {
        super(name,
                new TexturedModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(404), DisplayManager.scaledHeight(44)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 166), new Vector2f(202, 22))), new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")))),
                new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1)
        );

        this.position = new Vector2f(position.x, position.y);
        this.value = value;
        this.onEnterPressed = onEnterPressed;

        valueGuiText = new GUIText(value.replaceAll(".", "*"), 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 12, position.y - 32), Float.MAX_VALUE, false, true);
        cursor = new GUIText("_", 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 12 + valueGuiText.getLineLength(), position.y - 32), Float.MAX_VALUE, false, true);
        cursorPosition = value.length();
    }

    public void render(Renderer renderer, GUIShader shader) {
        shader.start();
        renderer.renderGUI(this, shader);
        shader.stop();

        long diff = System.currentTimeMillis() % 600;

        String truncatedValue = this.value;
        int truncateStart = cursorPosition;
        if (truncatedValue.length() - cursorPosition <= truncateStart)
            truncateStart = truncatedValue.length() - truncateStart;
        if(truncateStart < 0)
            truncateStart = 0;
        if(truncatedValue.length() > truncateLimit) {
            truncatedValue = this.value.substring(truncateStart);
            if (truncatedValue.length() > truncateLimit);
                truncatedValue = truncatedValue.substring(0, truncateLimit);
        }

        if(!this.valueGuiText.textString.equals(truncatedValue)) {
            valueGuiText.remove();
            valueGuiText = new GUIText(truncatedValue.replaceAll(".", "*"), 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 12, position.y - 32), 400f, false, true);
        }

        if(focused && diff >= 300) {
            GUIText valueUpToCursor = new GUIText(truncatedValue.substring(0, cursorPosition - truncateStart).replaceAll(".", "*"), 1.5f, TextMaster.minecraftFont, new Vector2f(position.x + 12, position.y - 32), 400f, false, true);
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

    boolean isValidInputKey(int key) {
            return !(
                   key == Keyboard.KEY_LCONTROL
                || key == Keyboard.KEY_RCONTROL
                || key == Keyboard.KEY_LSHIFT
                || key == Keyboard.KEY_RSHIFT
                || key == Keyboard.KEY_KANA
                || key == Keyboard.KEY_KANJI
                || key == Keyboard.KEY_LMENU
                || key == Keyboard.KEY_RMENU
                || key == Keyboard.KEY_LMETA
                || key == Keyboard.KEY_RMETA
                || key == Keyboard.KEY_SCROLL
                || key == Keyboard.KEY_NEXT
                || key == Keyboard.KEY_NOCONVERT
                || key == Keyboard.KEY_NONE
                || key == Keyboard.KEY_RETURN
                || key == Keyboard.KEY_NUMPADENTER
                || key == Keyboard.KEY_NUMLOCK
                || key == Keyboard.KEY_PAUSE
                || key == Keyboard.KEY_FUNCTION
                || key == Keyboard.KEY_F1
                || key == Keyboard.KEY_F2
                || key == Keyboard.KEY_F3
                || key == Keyboard.KEY_F4
                || key == Keyboard.KEY_F5
                || key == Keyboard.KEY_F6
                || key == Keyboard.KEY_F7
                || key == Keyboard.KEY_F8
                || key == Keyboard.KEY_F9
                || key == Keyboard.KEY_F10
                || key == Keyboard.KEY_F11
                || key == Keyboard.KEY_F12
                || key == Keyboard.KEY_F13
                || key == Keyboard.KEY_F14
                || key == Keyboard.KEY_F15
                || key == Keyboard.KEY_F16
                || key == Keyboard.KEY_F17
                || key == Keyboard.KEY_F18
                || key == Keyboard.KEY_F19
                || key == Keyboard.KEY_HOME
                || key == Keyboard.KEY_INSERT
                || key == Keyboard.KEY_PRIOR
                || key == Keyboard.KEY_SECTION
                || key == Keyboard.KEY_SLEEP
                || key == Keyboard.KEY_STOP
                || key == Keyboard.KEY_UNDERLINE
                || key == Keyboard.KEY_UNLABELED
                || key == Keyboard.KEY_SYSRQ
                || key == Keyboard.KEY_TAB
                || key == Keyboard.KEY_END
                || key == Keyboard.KEY_ESCAPE
                || key == Keyboard.KEY_DELETE
                || key == Keyboard.KEY_CIRCUMFLEX // ???
                || key == Keyboard.KEY_DOWN
                || key == Keyboard.KEY_UP
                || key == Keyboard.KEY_CLEAR
                || key == Keyboard.KEY_CAPITAL
                || key == Keyboard.KEY_CONVERT
                || key == Keyboard.KEY_APPS
                || key == Keyboard.KEY_AX
            );
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
//                                if(("" + character).matches("[A-Za-z0-9\\[\\]{}'#@~./,<>?;:\\-\\\\()&^$%£\"!*&=_@~`¬¦| ]+")) {
                                    stringBuilder.append(character);
//                                }
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
                    } else if (!isValidInputKey(Keyboard.getEventKey())) {
                        return;
                    } else if (Keyboard.getEventKey() == Keyboard.KEY_BACK) { //Backspace
                        if (value.length() > 0) {
                            value = value.substring(0, cursorPosition - 1) + value.substring(cursorPosition);
                            cursorPosition--;
                        }
                    } else if (Keyboard.getEventKey() == Keyboard.KEY_RETURN && this.onEnterPressed != null) {
                        this.onEnterPressed.onClick();
                    } else {
                        value = value.substring(0, cursorPosition) +  Keyboard.getEventCharacter() + value.substring(cursorPosition);
                        cursorPosition++;
                    }
                }
            }
        }

        if(value.length() > inputLimit)
            value = value.substring(0, inputLimit);

        if(cursorPosition > value.length()) {
            cursorPosition = value.length();
        } else if (cursorPosition < 0) {
            cursorPosition = 0;
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

        if(MouseHandler.didClick() && mouseIsOver) {
            focused = true;
        } else if (Mouse.isButtonDown(0) && !mouseIsOver) {
            focused = false;
        }
    }

    public void resize() {
        this.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(404), DisplayManager.scaledHeight(44)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 166), new Vector2f(202, 22))));
    }

    public void cleanUp() {
        this.valueGuiText.remove();
        this.cursor.remove();
    }

}
