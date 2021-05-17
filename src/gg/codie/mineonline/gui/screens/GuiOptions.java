package gg.codie.mineonline.gui.screens;

import gg.codie.minecraft.client.options.EMinecraftGUIScale;
import gg.codie.mineonline.Globals;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiSlider;
import gg.codie.mineonline.gui.components.GuiSmallButton;
import gg.codie.mineonline.gui.rendering.Font;

public class GuiOptions extends AbstractGuiScreen
{

    public GuiOptions(AbstractGuiScreen guiscreen)
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
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parent);
                else
                    MenuManager.setMenuScreen(parent);
            }
        }));
        controlList.add(new GuiButton(100, getWidth() / 2 - 100, getHeight() / 6 + 120 + 12, "Controls", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(new GuiControls(thisScreen));
                else
                    MenuManager.setMenuScreen(new GuiControls(thisScreen));
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

        if (LegacyGameManager.isInGame())
            ((GuiButton)controlList.get(2)).enabled = LegacyGameManager.getVersion() == null || LegacyGameManager.getVersion().useFOVPatch;

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
//                    new GUIScale(getWidth(), getHeight());
//                    LegacyGameManager.setGUIScreen(new GuiIngameOptions(parent));
                } else {
                    Settings.singleton.setGUIScale(newGuiScale);
                }

                ((GuiSmallButton)controlList.get(3)).displayString = "GUI Scale: " + Settings.singleton.getGUIScale().getName().toUpperCase();
            }
        }));

        if (LegacyGameManager.isInGame())
            ((GuiButton)controlList.get(3)).enabled = LegacyGameManager.getVersion() != null && (LegacyGameManager.getVersion().scaledResolutionClass != null || LegacyGameManager.getVersion().guiScreenClass != null);


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

        if (LegacyGameManager.isInGame())
            ((GuiButton)controlList.get(4)).enabled = LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().ingameVersionString != null;

//        controlList.add(new GuiSmallButton(0, getWidth() / 2 + 5, getHeight() / 6 + 24, "Main Hand: " + Settings.singleton.getMainHand().name(), new GuiButton.GuiButtonListener() {
//            @Override
//            public void OnButtonPress() {
//                EMinecraftMainHand newMainHand;
//                if (Settings.singleton.getMainHand().ordinal() + 1 == EMinecraftMainHand.values().length) {
//                    newMainHand = EMinecraftMainHand.values()[0];
//                } else {
//                    newMainHand = EMinecraftMainHand.values()[Settings.singleton.getMainHand().ordinal() + 1];
//                }
//
//                if (LegacyGameManager.isInGame()) {
//                    LegacyGameManager.setMainHand(newMainHand);
//                } else {
//                    Settings.singleton.setMainHand(newMainHand);
//                }
//
//                ((GuiSmallButton) controlList.get(5)).displayString = "Main Hand: " + Settings.singleton.getMainHand().name();
//            }
//        }));

        controlList.add(new GuiButton(101, getWidth() / 2 - 100, getHeight() / 6 + 96 + 12, "About", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(new GuiAbout(thisScreen));
                else
                    MenuManager.setMenuScreen(new GuiAbout(thisScreen));
            }
        }));

        if (!LegacyGameManager.isInGame()) {
            controlList.add(new GuiSmallButton(0, getWidth() / 2 + 5, getHeight() / 6 + 24, "Fullscreen: " + (Settings.singleton.getFullscreen() ? "ON" : "OFF"), new GuiButton.GuiButtonListener() {
                @Override
                public void OnButtonPress() {
                    Settings.singleton.setFullscreen(!Settings.singleton.getFullscreen());
                    ((GuiSmallButton) controlList.get(6)).displayString = "Fullscreen: " + (Settings.singleton.getFullscreen() ? "ON" : "OFF");
                }
            }));

            controlList.add(new GuiSmallButton(0, getWidth() / 2 + 5, getHeight() / 6 + 48, "Logout", new GuiButton.GuiButtonListener() {
                @Override
                public void OnButtonPress() {
                    MenuManager.setMenuScreen(new GuiLoginLegacy());
                    Session.session.logout();
                }
            }));

            controlList.add(new GuiSmallButton(0, getWidth() / 2 - 155, getHeight() / 6 + 48, "Custom Capes: " + (Settings.singleton.getCustomCapes() ? "YES" : "NO"), new GuiButton.GuiButtonListener() {
                @Override
                public void OnButtonPress() {
                    Settings.singleton.setCustomCapes(!Settings.singleton.getCustomCapes());
                    ((GuiSmallButton) controlList.get(7)).displayString = "Custom Capes: " + (Settings.singleton.getCustomCapes() ? "YES" : "NO");
                }
            }));
        }
    }

    public void resize() {
        controlList.get(0).resize(getWidth() / 2 - 100, getHeight() / 6 + 168);
        controlList.get(1).resize(getWidth() / 2 - 100, getHeight() / 6 + 120 + 12);
        controlList.get(2).resize(getWidth() / 2 - 155, getHeight() / 6);
        controlList.get(3).resize(getWidth() / 2 + 5, getHeight() / 6);
        controlList.get(4).resize(getWidth() / 2 - 155, getHeight() / 6 + 24);
//        controlList.get(5).resize(getWidth() / 2 + 5, getHeight() / 6 + 24);
        controlList.get(5).resize(getWidth() / 2 - 100, getHeight() / 6 + 96 + 12);

        if (!LegacyGameManager.isInGame()) {
            controlList.get(6).resize(getWidth() / 2 + 5, getHeight() / 6 + 24);
            controlList.get(7).resize(getWidth() / 2 + 5, getHeight() / 6 + 48);
            if (!Globals.LTS)
                controlList.get(8).resize(getWidth() / 2 - 155, getHeight() / 6 + 48);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        resize();

        drawDefaultBackground();
        Font.minecraftFont.drawCenteredStringWithShadow(screenName, getWidth() / 2, 20, 0xffffff);
        super.drawScreen(mouseX, mouseY);
    }

    private AbstractGuiScreen parent;
    private String screenName;
}
