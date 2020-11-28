package gg.codie.mineonline.gui.screens;

import gg.codie.minecraft.client.options.EMinecraftGUIScale;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiSlider;
import gg.codie.mineonline.gui.components.GuiSmallButton;
import gg.codie.mineonline.gui.rendering.FontRenderer;

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

        controlList.add(doneButton = new GuiButton(200, getWidth() / 2 - 100, getHeight() / 6 + 168, "Done", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                Settings.singleton.saveSettings();
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parent);
                else
                    MenuManager.setMenuScreen(parent);
            }
        }));
        controlList.add(controlsButton = new GuiButton(100, getWidth() / 2 - 100, getHeight() / 6 + 120 + 12, "Controls...", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(new GuiControls(thisScreen));
                else
                    MenuManager.setMenuScreen(new GuiControls(thisScreen));
            }
        }));

        controlList.add(fovSlider = new GuiSlider(0, getWidth() / 2 - 155, getHeight() / 6, getFOVLabel((int)Settings.singleton.getFOV()), (Settings.singleton.getFOV() - 40) / 70, new GuiSlider.SliderListener() {
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
            fovSlider.enabled = LegacyGameManager.getVersion() == null || LegacyGameManager.getVersion().useFOVPatch;

        controlList.add(guiScaleButton = new GuiSmallButton(0, getWidth() / 2 + 5, getHeight() / 6, "GUI Scale: " + Settings.singleton.getGUIScale().getName().toUpperCase(), new GuiButton.GuiButtonListener() {
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
            guiScaleButton.enabled = LegacyGameManager.getVersion() != null && (LegacyGameManager.getVersion().scaledResolutionClass != null || LegacyGameManager.getVersion().guiScreenClass != null);


        controlList.add(hideVersionNumberButton = new GuiSmallButton(0, getWidth() / 2 - 155, getHeight() / 6 + 24, "Hide Version Number: " + (Settings.singleton.getHideVersionString() ? "YES" : "NO"), new GuiButton.GuiButtonListener() {
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
            hideVersionNumberButton.enabled = LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().ingameVersionString != null;

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

        controlList.add(videoSettingsButton = new GuiButton(101, getWidth() / 2 - 100, getHeight() / 6 + 96 + 12, "Video Settings...", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(new GuiVideoSettings(thisScreen));
                else
                    MenuManager.setMenuScreen(new GuiVideoSettings(thisScreen));
            }
        }));

        if (!LegacyGameManager.isInGame()) {
            controlList.add(fullscreenButton = new GuiSmallButton(0, getWidth() / 2 + 5, getHeight() / 6 + 24, "Fullscreen: " + (Settings.singleton.getFullscreen() ? "ON" : "OFF"), new GuiButton.GuiButtonListener() {
                @Override
                public void OnButtonPress() {
                    Settings.singleton.setFullscreen(!Settings.singleton.getFullscreen());
                    ((GuiSmallButton) controlList.get(6)).displayString = "Fullscreen: " + (Settings.singleton.getFullscreen() ? "ON" : "OFF");
                }
            }));

            controlList.add(customCapesButton = new GuiSmallButton(0, getWidth() / 2 - 155, getHeight() / 6 + 48, "Custom Capes: " + (Settings.singleton.getHideVersionString() ? "YES" : "NO"), new GuiButton.GuiButtonListener() {
                @Override
                public void OnButtonPress() {
                    Settings.singleton.setCustomCapes(!Settings.singleton.getCustomCapes());
                    ((GuiSmallButton) controlList.get(7)).displayString = "Custom Capes: " + (Settings.singleton.getCustomCapes() ? "YES" : "NO");
                }
            }));

            controlList.add(logoutButton = new GuiSmallButton(0, getWidth() / 2 + 5, getHeight() / 6 + 48, "Logout", new GuiButton.GuiButtonListener() {
                @Override
                public void OnButtonPress() {
                    MenuManager.setMenuScreen(new GuiLogin());
                    Session.session.logout();
                }
            }));
        } else {
            videoSettingsButton.enabled = false;
            videoSettingsButton.setTooltip("Accessible in launcher.");
        }
    }

    public void resize() {
        doneButton.resize(getWidth() / 2 - 100, getHeight() / 6 + 168);
        controlsButton.resize(getWidth() / 2 - 100, getHeight() / 6 + 120 + 12);
        fovSlider.resize(getWidth() / 2 - 155, getHeight() / 6);
        guiScaleButton.resize(getWidth() / 2 + 5, getHeight() / 6);
        hideVersionNumberButton.resize(getWidth() / 2 - 155, getHeight() / 6 + 24);

        if (!LegacyGameManager.isInGame()) {
            videoSettingsButton.resize(getWidth() / 2 - 100, getHeight() / 6 + 96 + 12);
            fullscreenButton.resize(getWidth() / 2 + 5, getHeight() / 6 + 24);
            customCapesButton.resize(getWidth() / 2 - 155, getHeight() / 6 + 48);
            logoutButton.resize(getWidth() / 2 + 5, getHeight() / 6 + 48);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        resize();

        drawDefaultBackground();
        FontRenderer.minecraftFontRenderer.drawCenteredString(screenName, getWidth() / 2, 20, 0xffffff);
        super.drawScreen(mouseX, mouseY);
    }

    private AbstractGuiScreen parent;
    private String screenName;

    private GuiButton doneButton;
    private GuiButton controlsButton;
    private GuiSlider fovSlider;
    private GuiButton guiScaleButton;
    private GuiButton hideVersionNumberButton;
    private GuiButton videoSettingsButton;
    private GuiButton customCapesButton;
    private GuiButton fullscreenButton;
    private GuiButton logoutButton;
}
