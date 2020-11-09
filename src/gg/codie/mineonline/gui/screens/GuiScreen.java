package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.gui.GUIScale;
import gg.codie.mineonline.gui.MouseHandler;
import gg.codie.mineonline.gui.components.GuiComponent;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.sound.ClickSound;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;
import java.util.List;

public class GuiScreen extends GuiComponent
{

    public GuiScreen()
    {
        controlList = new ArrayList();
        selectedButton = null;
    }

    public void drawScreen(int i, int j)
    {
        for(int k = 0; k < controlList.size(); k++)
        {
            GuiButton guibutton = (GuiButton)controlList.get(k);
            guibutton.drawButton(i, j);
        }

    }

    public static String getClipboardString()
    {
        try
        {
            java.awt.datatransfer.Transferable transferable = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if(transferable != null && transferable.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor))
            {
                String s = (String)transferable.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
                return s;
            }
        }
        catch(Exception exception) { }
        return null;
    }

    protected void mouseClicked(int i, int j, int k)
    {
        if(k == 0)
        {
            for(int l = 0; l < controlList.size(); l++)
            {
                GuiButton guibutton = (GuiButton)controlList.get(l);
                if(guibutton.mousePressed(i, j))
                {
                    selectedButton = guibutton;
                    ClickSound.play();
                    actionPerformed(guibutton);
                }
            }

        }
    }

    protected void mouseMovedOrUp(int i, int j, int k)
    {
        if(selectedButton != null && k == 0)
        {
            selectedButton.mouseReleased(i, j);
            selectedButton = null;
        }
    }

    protected void actionPerformed(GuiButton guibutton)
    {
    }

    public void initGui()
    {
    }

    public void handleInput()
    {
        for(; Mouse.next(); handleMouseInput()) { }
        for(; Keyboard.next(); handleKeyboardInput()) { }
    }

    protected void keyTyped(char keyChar, int keyCode) {

    }

    public void handleMouseInput()
    {
        if(MouseHandler.didClick())
        {
            int i = (Mouse.getEventX() * getWidth()) / Display.getWidth();
            int k = getHeight() - (Mouse.getEventY() * getHeight()) / Display.getHeight() - 1;
            mouseClicked(i, k, Mouse.getEventButton());
        } else
        {
            int j = (Mouse.getEventX() * getWidth()) / Display.getWidth();
            int l = getHeight() - (Mouse.getEventY() * getWidth()) / Display.getHeight() - 1;
            mouseMovedOrUp(j, l, Mouse.getEventButton());
        }
    }

    public void handleKeyboardInput()
    {
        if(Keyboard.getEventKeyState())
        {
            keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }
    }

    public void updateScreen()
    {
    }

    public void onGuiClosed()
    {
    }

    public void drawDefaultBackground()
    {
        drawWorldBackground(0);
    }

    public void drawWorldBackground(int i)
    {
        drawGradientRect(0, 0, getWidth(), getHeight(), 0xc0101010, 0xd0101010);
    }

    public void selectNextField()
    {
    }

    public int getWidth() {
        return GUIScale.lastScaledWidth();
    }

    public int getHeight() {
        return GUIScale.lastScaledHeight();
    }
    protected List controlList;
    private GuiButton selectedButton;
}
