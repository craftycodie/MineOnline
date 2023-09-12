
package com.ahnewark.mineonline.gui.components;


import com.ahnewark.mineonline.gui.input.InputSanitization;
import com.ahnewark.mineonline.gui.rendering.Font;
import com.ahnewark.mineonline.gui.rendering.Renderer;
import com.ahnewark.mineonline.gui.screens.AbstractGuiScreen;
import org.lwjgl.input.Keyboard;

public class GuiTextField extends GuiComponent
{
    boolean allowSpaces = true;

    public GuiTextField(AbstractGuiScreen guiscreen, int x, int y, int width, int height, String value)
    {
        isFocused = false;
        isEnabled = true;
        parentGuiScreen = guiscreen;
        xPos = x;
        yPos = y;
        this.width = width;
        this.height = height;
        setText(value);
    }

    @Override
    public void resize(int x, int y) {
        xPos = x;
        yPos = y;
    }

    public void disableSpaces() {
        allowSpaces = false;
    }

    public void setText(String s)
    {
        text = s;
    }

    public String getText()
    {
        return text;
    }

    public void textboxKeyTyped(char c, int i)
    {
        if(!isEnabled || !isFocused)
        {
            return;
        }
        if(i == Keyboard.KEY_TAB)
        {
            parentGuiScreen.selectNextField();
        }
        if(c == '\026')
        {
            String s;
            int j;
            s = AbstractGuiScreen.getClipboardString();
            if(s == null)
            {
                s = "";
            }
            j = maxStringLength - text.length();
            if(j > s.length())
            {
                j = s.length();
            }
            if(j > 0)
            {
                int validCount = 0;
                for (char copiedCharacter: s.toCharArray()) {
                    if(validCount == j)
                        break;
                    if (InputSanitization.allowedCharacters.indexOf(copiedCharacter) >= 0 && (int) copiedCharacter > (allowSpaces ? 31 : 32)) {
                        text += copiedCharacter;
                    }
                    validCount++;
                }
            }
        }
        if(i == 14 && text.length() > 0)
        {
            text = text.substring(0, text.length() - 1);
        }
        if(InputSanitization.allowedCharacters.indexOf(c) >= 0 && (text.length() < maxStringLength || maxStringLength == 0) && (int)c > (allowSpaces ? 31 : 32))
        {
            text += c;
        }
    }

    public void mouseClicked(int x, int y, int button)
    {
        boolean flag = isEnabled && x >= xPos && x < xPos + width && y >= yPos && y < yPos + height;
        setFocused(flag);
    }

    public void setFocused(boolean flag)
    {
        isFocused = flag;
    }

    public void drawTextBox()
    {
        Renderer.singleton.drawRect(xPos - 1, yPos - 1, xPos + width + 1, yPos + height + 1, 0xffa0a0a0);
        Renderer.singleton.drawRect(xPos, yPos, xPos + width, yPos + height, 0xff000000);
        if(isEnabled)
        {
            boolean flag = isFocused && (System.currentTimeMillis()) % 1000 < 500;
            Font.minecraftFont.drawString((new StringBuilder()).append(text).append(flag ? "_" : "").toString(), xPos + 4, yPos + (height - 8) / 2, 0xe0e0e0);
        } else
        {
            Font.minecraftFont.drawString(text, xPos + 4, yPos + (height - 8) / 2, 0x707070);
        }
    }

    public void setMaxStringLength(int i)
    {
        maxStringLength = i;
    }

    private int xPos;
    private int yPos;
    private final int width;
    private final int height;
    private String text;
    private int maxStringLength;
    public boolean isFocused;
    public boolean isEnabled;
    private AbstractGuiScreen parentGuiScreen;
}
