package gg.codie.mineonline.gui.screens;

import gg.codie.minecraft.client.EMinecraftGUIScale;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.GUIScale;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiSlider;
import gg.codie.mineonline.gui.components.GuiSmallButton;
import org.lwjgl.input.Mouse;

public class GuiIngameOptions extends AbstractGuiScreen
{

    public GuiIngameOptions(AbstractGuiScreen guiscreen)
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
        AbstractGuiScreen thisScreen = this;

        controlList.add(new GuiButton(200, getWidth() / 2 - 100, getHeight() / 6 + 168, "Done", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                Settings.singleton.saveSettings();
                LegacyGameManager.setGUIScreen(parent);
            }
        }));
        controlList.add(new GuiButton(100, getWidth() / 2 - 100, getHeight() / 6 + 120 + 12, "Controls", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                LegacyGameManager.setGUIScreen(new GuiControls(thisScreen));
            }
        }));

        controlList.add(new GuiSlider(0, getWidth() / 2 - 155, getHeight() / 6, getFOVLabel((int)Settings.singleton.getFOV()), (Settings.singleton.getFOV() - 40) / 70, new GuiSlider.SliderListener() {
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

        ((GuiButton)controlList.get(2)).enabled = LegacyGameManager.getVersion().useFOVPatch;

        controlList.add(new GuiSmallButton(0, getWidth() / 2 + 5, getHeight() / 6, "GUI Scale: " + Settings.singleton.getGUIScale().getName().toUpperCase(), new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                EMinecraftGUIScale newGuiScale;
                if (Settings.singleton.getGUIScale().getIntValue() + 1 == EMinecraftGUIScale.values().length) {
                    newGuiScale = EMinecraftGUIScale.values()[0];
                } else {
                    newGuiScale = EMinecraftGUIScale.values()[Settings.singleton.getGUIScale().getIntValue() + 1];
                }

                if (LegacyGameManager.isInGame()) {
                    LegacyGameManager.setGUIScale(newGuiScale);
                    new GUIScale(getWidth(), getHeight());
                    LegacyGameManager.setGUIScreen(new GuiIngameOptions(parent));
                } else {
                    Settings.singleton.setGUIScale(newGuiScale);
                }

                ((GuiSmallButton)controlList.get(3)).displayString = "GUI Scale: " + Settings.singleton.getGUIScale().getName().toUpperCase();
            }
        }));

        ((GuiButton)controlList.get(3)).enabled = LegacyGameManager.getVersion().scaledResolutionClass != null && LegacyGameManager.getVersion().guiScreenClass != null;


        controlList.add(new GuiSmallButton(0, getWidth() / 2 - 155, getHeight() / 6 + 24, "Hide Version Number: " + (Settings.singleton.getHideVersionString() ? "YES" : "NO"), new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame()) {
                    LegacyGameManager.setHideVersionString(!Settings.singleton.getHideVersionString());
                } else {
                    Settings.singleton.setHideVersionString(!Settings.singleton.getHideVersionString());
                }

                ((GuiSmallButton) controlList.get(4)).displayString = "Hide Version Number: " + (Settings.singleton.getHideVersionString() ? "YES" : "NO");
            }
        }));

        ((GuiButton)controlList.get(4)).enabled = LegacyGameManager.getVersion().ingameVersionString != null;

        controlList.add(new GuiButton(101, getWidth() / 2 - 100, getHeight() / 6 + 96 + 12, "About", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                LegacyGameManager.setGUIScreen(new GuiAbout(thisScreen));
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

    private AbstractGuiScreen parent;
    protected String screenName;
}
