package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiSmallButton;
import org.lwjgl.input.Mouse;

public class GuiControls extends GuiScreen
{

    public GuiControls(GuiScreen guiscreen)
    {
        screenTitle = "Controls";
        buttonId = -1;
        parentScreen = guiscreen;
    }

    private int func_20080_j()
    {
        return getWidth() / 2 - 155;
    }

    public void initGui()
    {
        int i = func_20080_j();
//        for(int j = 0; j < options.keyBindings.length; j++)
//        {
//            controlList.add(new GuiSmallButton(j, i + (j % 2) * 160, getHeight() / 6 + 24 * (j >> 1), 70, 20, options.getOptionDisplayString(j)));
//        }

        controlList.add(new GuiButton(200, getWidth() / 2 - 100, getHeight() / 6 + 168, "Done"));
        screenTitle = "Controls";
    }

    protected void actionPerformed(GuiButton guibutton)
    {
//        for(int i = 0; i < options.keyBindings.length; i++)
//        {
//            ((GuiButton)controlList.get(i)).displayString = options.getOptionDisplayString(i);
//        }

        if(guibutton.id == 200)
        {
            MenuManager.setGUIScreen(parentScreen);
            if(parentScreen == null)
                Mouse.setGrabbed(true);
        } else
        {
            buttonId = guibutton.id;
//            guibutton.displayString = (new StringBuilder()).append("> ").append(options.getOptionDisplayString(guibutton.id)).append(" <").toString();
        }
    }

    protected void keyTyped(char c, int i)
    {
        if(buttonId >= 0)
        {
//            options.setKeyBinding(buttonId, i);
//            ((GuiButton)controlList.get(buttonId)).displayString = options.getOptionDisplayString(buttonId);
            buttonId = -1;
        } else
        {
            super.keyTyped(c, i);
        }
    }

    public void drawScreen(int i, int j)
    {
        controlList.clear();
        initGui();

        drawDefaultBackground();
        drawCenteredString(screenTitle, getWidth() / 2, 20, 0xffffff);
        int k = func_20080_j();
//        for(int l = 0; l < options.keyBindings.length; l++)
//        {
//            drawString(options.getKeyBindingDescription(l), k + (l % 2) * 160 + 70 + 6, getHeight() / 6 + 24 * (l >> 1) + 7, -1);
//        }

        super.drawScreen(i, j);
    }

    private GuiScreen parentScreen;
    protected String screenTitle;
    private int buttonId;
}
