// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package gg.codie.mineonline.gui.screens;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, StringTranslate, EnumOptions, GuiSmallButton, 
//            GameSettings, GuiSlider, GuiButton, ScaledResolution

import gg.codie.mineonline.Settings;
import gg.codie.mineonline.gui.GUIScale;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiSlider;
import gg.codie.mineonline.gui.components.GuiSmallButton;
import gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GuiIngameOptions extends GuiScreen
{

    public GuiIngameOptions(GuiScreen guiscreen)
    {
        field_22107_a = "Options";
        parent = guiscreen;
        initGui();
    }

    public void initGui()
    {
        field_22107_a = "Options";
        int i = 0;
        EnumOptions aenumoptions[] = EnumOptions.values();
        int j = aenumoptions.length;
        for(int k = 0; k < j; k++)
        {
            EnumOptions enumoptions = aenumoptions[k];
            if(!enumoptions.getEnumFloat())
            {
                controlList.add(new GuiSmallButton(enumoptions.ordinal(), (getWidth() / 2 - 155) + (i % 2) * 160, getHeight() / 6 + 24 * (i >> 1), enumoptions, enumoptions.name()));
            } else
            {
                controlList.add(new GuiSlider(enumoptions.ordinal(), (getWidth() / 2 - 155) + (i % 2) * 160, getHeight() / 6 + 24 * (i >> 1), enumoptions, enumoptions.name(), 0));
            }
            i++;
        }

        controlList.add(new GuiButton(200, getWidth() / 2 - 100, getHeight() / 6 + 168, "Done"));
        controlList.add(new GuiButton(100, getWidth() / 2 - 100, getHeight() / 6 + 120 + 12, "Controls"));

    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if(!guibutton.enabled)
        {
            return;
        }
        if(guibutton.id < 100 && (guibutton instanceof GuiSmallButton))
        {
            // TODO: Set Value
            //guiGameSettings.setOptionValue(((GuiSmallButton)guibutton).returnEnumOptions(), 1);
            //guibutton.displayString = guiGameSettings.getKeyBinding(EnumOptions.getEnumOptions(guibutton.id));
        }
        if(guibutton.id < 100 && (guibutton instanceof GuiSlider))
        {
            // TODO: Set Value
            //guiGameSettings.setOptionValue(((GuiSmallButton)guibutton).returnEnumOptions(), 1);
            //guibutton.displayString = guiGameSettings.getKeyBinding(EnumOptions.getEnumOptions(guibutton.id));
        }
        if(guibutton.id == 100)
        {
            MenuManager.setGUIScreen(new GuiControls(this));
        }
        if(guibutton.id == 200)
        {
            Settings.singleton.saveSettings();
            MenuManager.setGUIScreen(parent);
            if (parent == null)
                Mouse.setGrabbed(true);

            //mc.displayGuiScreen(null);
//            mc.setIngameFocus();
        }
        GUIScale scaledresolution = new GUIScale(Display.getWidth(), Display.getHeight());
        int i = (int)scaledresolution.getScaledWidth();
        int j = (int)scaledresolution.getScaledHeight();
    }

    @Override
    public void drawScreen(int i, int j)
    {
        //controlList.clear();

        drawDefaultBackground();
        drawCenteredString(field_22107_a, getWidth() / 2, 20, 0xffffff);
        super.drawScreen(i, j);
    }

    private GuiScreen parent;
    protected String field_22107_a;
}
