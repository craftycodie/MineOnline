package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.GUIScale;
import gg.codie.mineonline.gui.input.MouseHandler;
import gg.codie.mineonline.gui.components.GuiComponent;
import gg.codie.mineonline.gui.components.GuiButton;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractGuiScreen extends GuiComponent
{

    public AbstractGuiScreen()
    {
        controlList = new ArrayList<>();
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
                    guibutton.doClick();
                    selectedButton = guibutton;
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

    public abstract void initGui();

    protected void actionPerformed(GuiButton guiButton) {

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
        int width = Display.getWidth();
        int height = Display.getHeight();

        if (LegacyGameManager.isInGame()) {
            width = LegacyGameManager.getWidth();
            height = LegacyGameManager.getHeight();
        }

        if(MouseHandler.didClick(0))
        {
            int i = (Mouse.getEventX() * getWidth()) / width;
            int k = getHeight() - (Mouse.getEventY() * getHeight()) / height - 1;
            mouseClicked(i, k, Mouse.getEventButton());
        } else
        {
            int j = (Mouse.getEventX() * getWidth()) / width;
            int l = getHeight() - (Mouse.getEventY() * getWidth()) / height - 1;
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
        // Alpha/Beta
        drawGradientRect(0, 0, getWidth(), getHeight(), 0xc0101010, 0xd0101010);
        // Classic
        //drawGradientRect(0, 0, getWidth(), getHeight(), 1610941696, -1607454624);
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
    protected List<GuiComponent> controlList;
    private GuiButton selectedButton;
}
