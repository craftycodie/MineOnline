package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.GUIScale;
import gg.codie.mineonline.gui.input.MouseHandler;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiComponent;
import gg.codie.mineonline.gui.rendering.Renderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGuiScreen extends GuiComponent
{
    @Override
    public void resize(int x, int y) {
        // do nothing.
    }

    public AbstractGuiScreen()
    {
        controlList = new ArrayList<>();
        selectedButton = null;
    }

    public void drawScreen(int mouseX, int mouseY)
    {
        for(int i = 0; i < controlList.size(); i++)
        {
            GuiButton guibutton = (GuiButton)controlList.get(i);
            guibutton.drawButton(mouseX, mouseY);
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

    protected void mouseClicked(int x, int y, int button)
    {
        if(button == 0)
        {
            for(int l = 0; l < controlList.size(); l++)
            {
                GuiButton guibutton = (GuiButton)controlList.get(l);
                if(guibutton.mousePressed(x, y))
                {
                    guibutton.doClick();
                    selectedButton = guibutton;
                }
            }

        }
    }

    protected void mouseMovedOrUp(int x, int y, int button)
    {
        if(selectedButton != null && button == 0)
        {
            selectedButton.mouseReleased(x, y);
            selectedButton = null;
        }
    }

    public void initGui() {

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
        Renderer.singleton.drawGradient(0, 0, getWidth(), getHeight(), 0xc0, 0x10, 0x10, 0x10, 0xd0, 0x10, 0x10, 0x10);
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
