// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package gg.codie.mineonline.gui.screens;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, StringTranslate, EnumOptions, GuiSmallButton, 
//            GameSettings, GuiSlider, GuiButton, ScaledResolution

import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiSlider;
import org.lwjgl.input.Mouse;

public class GuiIngameOptions extends GuiScreen
{

    public GuiIngameOptions(GuiScreen guiscreen)
    {
        screenName = "Options";
        parent = guiscreen;
        initGui();
    }

    String getFOVLabel(int fov) {
        String fovLabel = "" + fov;

        switch(fov) {
            case 70:
                fovLabel = "Normal";
                break;
            case 110:
                fovLabel = "Quake Pro";
                break;
        }

        return "FOV: " + fovLabel;
    }

    public void initGui()
    {
        GuiScreen thisScreen = this;

        controlList.add(new GuiButton(200, getWidth() / 2 - 100, getHeight() / 6 + 168, "Done", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                Settings.singleton.saveSettings();
                MenuManager.setGUIScreen(parent);
                if (parent == null)
                    Mouse.setGrabbed(true);
            }
        }));
        controlList.add(new GuiButton(100, getWidth() / 2 - 100, getHeight() / 6 + 120 + 12, "Controls", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                MenuManager.setGUIScreen(new GuiControls(thisScreen));
            }
        }));
        controlList.add(new GuiSlider(0, getWidth() / 2 - 155, getHeight() / 6 + 24, getFOVLabel((int)Settings.singleton.getFOV()), (Settings.singleton.getFOV() - 40) / 70, new GuiSlider.SliderListener() {
            @Override
            public String onValueChange(float sliderValue) {
                int fov = (int)(sliderValue * 70) + 40;
                if (LegacyGameManager.isInGame()) {
                    LegacyGameManager.setFOV(fov);
                } else {
                    Settings.singleton.setFOV(fov);
                }

                return getFOVLabel(fov);
            }
        }));

    }

    @Override
    public void drawScreen(int i, int j)
    {
        //controlList.clear();

        drawDefaultBackground();
        drawCenteredString(screenName, getWidth() / 2, 20, 0xffffff);
        super.drawScreen(i, j);
    }

    private GuiScreen parent;
    protected String screenName;
}
