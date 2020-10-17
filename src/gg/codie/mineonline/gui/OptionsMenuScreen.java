package gg.codie.mineonline.gui;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.gui.components.LargeButton;
import gg.codie.mineonline.gui.components.MediumButton;
import gg.codie.mineonline.gui.components.ValueSlider;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.Camera;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.PlayerGameObject;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import org.lwjgl.util.vector.Vector2f;

import java.io.File;

public class OptionsMenuScreen implements IMenuScreen {
    MediumButton fullscreenButton;
    MediumButton versionStringsButton;
    MediumButton guiScaleButton;
    MediumButton logoutButton;
    ValueSlider fovSlider;
    MediumButton skinCustomizationButton;
    MediumButton aboutButton;
    MediumButton customCapesButton;
    LargeButton doneButton;
    GUIText label;

    private static final String[] guiScales = new String[] { "Auto", "Small", "Normal", "Large" };

    public OptionsMenuScreen() {
        Settings.loadSettings();

        fullscreenButton = new MediumButton("Fullscreen: " + (Settings.settings.getBoolean(Settings.FULLSCREEN) ? "ON" : "OFF"), new Vector2f((DisplayManager.getDefaultWidth() / 2) - 308, (DisplayManager.getDefaultHeight() / 2) - 40), new IOnClickListener() {
            @Override
            public void onClick() {
                boolean fullcreen = !Settings.settings.getBoolean(Settings.FULLSCREEN);
                Settings.settings.put(Settings.FULLSCREEN, fullcreen);
                Settings.saveSettings();
                fullscreenButton.setName("Fullscreen: " + (Settings.settings.getBoolean(Settings.FULLSCREEN) ? "ON" : "OFF"));
            }
        });

        guiScaleButton = new MediumButton("GUI Scale: " + guiScales[DisplayManager.getGuiScale()], new Vector2f((DisplayManager.getDefaultWidth() / 2) - 308, (DisplayManager.getDefaultHeight() / 2) + 8), new IOnClickListener() {
            @Override
            public void onClick() {
                if (DisplayManager.getGuiScale() == guiScales.length - 1) {
                    DisplayManager.setGuiScale(0);
                } else {
                    DisplayManager.setGuiScale(DisplayManager.getGuiScale() + 1);
                }

                MenuManager.resizeMenu();

                guiScaleButton.setName("GUI Scale: " + guiScales[DisplayManager.getGuiScale()]);
            }
        });

        fovSlider = new ValueSlider("FOV: " + getFOVLabel(), new Vector2f((DisplayManager.getDefaultWidth() / 2) - 308, (DisplayManager.getDefaultHeight() / 2) + 56), new IOnClickListener() {
            @Override
            public void onClick() {
                Settings.settings.put(Settings.FOV, fovSlider.getValue());
                Settings.saveSettings();
                fovSlider.setName("FOV: " + getFOVLabel());
            }
        }, Settings.settings.optInt(Settings.FOV, 70), 30, 110);

        skinCustomizationButton = new MediumButton("Skin Customization", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 8, (DisplayManager.getDefaultHeight() / 2) + 56), new IOnClickListener() {
            @Override
            public void onClick() {
                MenuManager.setMenuScreen(new SkinCustomizationMenuScreen());
            }
        });

        versionStringsButton = new MediumButton("Hide Version Number: " + (Settings.settings.getBoolean(Settings.HIDE_VERSION_STRING) ? "YES" : "NO"), new Vector2f((DisplayManager.getDefaultWidth() / 2) + 8, (DisplayManager.getDefaultHeight() / 2) - 40), new IOnClickListener() {
            @Override
            public void onClick() {
                boolean hideVersionStrings = !Settings.settings.getBoolean(Settings.HIDE_VERSION_STRING);
                Settings.settings.put(Settings.HIDE_VERSION_STRING, hideVersionStrings);
                Settings.saveSettings();
                versionStringsButton.setName("Hide Version Number: " + (Settings.settings.getBoolean(Settings.HIDE_VERSION_STRING) ? "YES" : "NO"));
            }
        });

        customCapesButton = new MediumButton("Use Custom Capes: " + (Settings.settings.optBoolean(Settings.CUSTOM_CAPES, false) ? "YES" : "NO"), new Vector2f((DisplayManager.getDefaultWidth() / 2) - 308, (DisplayManager.getDefaultHeight() / 2) + 104), new IOnClickListener() {
            @Override
            public void onClick() {
                boolean useCustomCapes = !Settings.settings.getBoolean(Settings.CUSTOM_CAPES);
                Settings.settings.put(Settings.CUSTOM_CAPES, useCustomCapes);
                Settings.saveSettings();
                customCapesButton.setName("Use Custom Capes: " + (Settings.settings.getBoolean(Settings.CUSTOM_CAPES) ? "YES" : "NO"));
                if(PlayerGameObject.thePlayer != null) {
                    PlayerGameObject.thePlayer.setCloak(LauncherFiles.TEMPLATE_CLOAK_PATH);
                    new File(LauncherFiles.CACHED_CLOAK_PATH).delete();
                }
                Session.session.cacheSkin();
            }
        });

        aboutButton = new MediumButton("About", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 8, (DisplayManager.getDefaultHeight() / 2) + 104), new IOnClickListener() {
            @Override
            public void onClick() {
                MenuManager.setMenuScreen(new AboutMenuScreen());
            }
        });

        logoutButton = new MediumButton("Logout", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 8, (DisplayManager.getDefaultHeight() / 2) + 8), new IOnClickListener() {
            @Override
            public void onClick() {
                Session.session.logout();
                MenuManager.setMenuScreen(new LoginMenuScreen(false));
                new File(LauncherFiles.CACHED_SKIN_PATH).delete();
                new File(LauncherFiles.CACHED_SKIN_METADATA_PATH).delete();
                new File(LauncherFiles.CACHED_CLOAK_PATH).delete();
            }
        });

        doneButton = new LargeButton("Done", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                MenuManager.setMenuScreen(new MainMenuScreen());
            }
        });

        label = new GUIText("Options", 1.5f, TextMaster.minecraftFont, new Vector2f(0, 40), DisplayManager.getDefaultWidth(), true, true);
    }

    public String getFOVLabel() {
        int fov = Settings.settings.optInt(Settings.FOV, 70);

        switch(fov) {
            case 70:
                return "Normal";
            case 110:
                return "Quake Pro";
            default:
                return "" + fov;
        }
    }

    public void update() {
        fullscreenButton.update();
        versionStringsButton.update();
        logoutButton.update();
        guiScaleButton.update();
        doneButton.update();
        fovSlider.update();
        customCapesButton.update();
        aboutButton.update();
        skinCustomizationButton.update();
    }

    public void render(Renderer renderer) {
        GUIShader.singleton.start();
        GUIShader.singleton.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        fullscreenButton.render(renderer, GUIShader.singleton);
        versionStringsButton.render(renderer, GUIShader.singleton);
        logoutButton.render(renderer, GUIShader.singleton);
        guiScaleButton.render(renderer, GUIShader.singleton);
        fovSlider.render(renderer, GUIShader.singleton);
        skinCustomizationButton.render(renderer, GUIShader.singleton);
        customCapesButton.render(renderer, GUIShader.singleton);
        aboutButton.render(renderer, GUIShader.singleton);
        doneButton.render(renderer, GUIShader.singleton);
        GUIShader.singleton.stop();
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        fullscreenButton.resize();
        versionStringsButton.resize();
        logoutButton.resize();
        doneButton.resize();
        guiScaleButton.resize();
        fovSlider.resize();
        customCapesButton.resize();
        aboutButton.resize();
        skinCustomizationButton.resize();
    }

    @Override
    public void cleanUp() {
        fullscreenButton.cleanUp();
        versionStringsButton.cleanUp();
        logoutButton.cleanUp();
        guiScaleButton.cleanUp();
        fovSlider.cleanUp();
        skinCustomizationButton.cleanUp();
        customCapesButton.cleanUp();
        aboutButton.cleanUp();
        doneButton.cleanUp();
        label.remove();
    }
}
