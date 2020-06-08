package gg.codie.mineonline.gui;

import gg.codie.mineonline.Properties;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.components.LargeButton;
import gg.codie.mineonline.gui.rendering.components.MediumButton;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.utils.LastLogin;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;

public class OptionsMenuScreen implements IMenuScreen {
    MediumButton fullscreenButton;
    MediumButton guiScaleButton;
    MediumButton aboutButton;
    MediumButton logoutButton;
    MediumButton resetSettings;
    LargeButton doneButton;
    GUIText label;

    private static final String[] guiScales = new String[] { "Auto", "Small", "Normal", "Large" };

    public OptionsMenuScreen() {
        fullscreenButton = new MediumButton("Fullscreen: " + (Properties.properties.getBoolean("fullscreen") ? "ON" : "OFF"), new Vector2f((DisplayManager.getDefaultWidth() / 2) - 308, (DisplayManager.getDefaultHeight() / 2) - 40), new IOnClickListener() {
            @Override
            public void onClick() {
                boolean fullcreen = !Properties.properties.getBoolean("fullscreen");
                Properties.properties.put("fullscreen", fullcreen);
                Properties.saveProperties();
                fullscreenButton.setName("Fullscreen: " + (Properties.properties.getBoolean("fullscreen") ? "ON" : "OFF"));
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

                PlayerRendererTest.resizeMenu();

                guiScaleButton.setName("GUI Scale: " + guiScales[DisplayManager.getGuiScale()]);
            }
        });

        resetSettings = new MediumButton("Reset Settings", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 308, (DisplayManager.getDefaultHeight() / 2) + 56), new IOnClickListener() {
            @Override
            public void onClick() {
                Properties.resetSettings();
            }
        });

        aboutButton = new MediumButton("About", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 8, (DisplayManager.getDefaultHeight() / 2) - 40), new IOnClickListener() {
            @Override
            public void onClick() {
                PlayerRendererTest.setMenuScreen(new AboutMenuScreen());
            }
        });

        logoutButton = new MediumButton("Logout", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 8, (DisplayManager.getDefaultHeight() / 2) + 8), new IOnClickListener() {
            @Override
            public void onClick() {
                LastLogin.deleteLastLogin();
                Session.session.logout();
                PlayerRendererTest.setMenuScreen(new LoginMenuScreen(false));
            }
        });

        doneButton = new LargeButton("Done", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                PlayerRendererTest.setMenuScreen(new MainMenuScreen());
            }
        });

        label = new GUIText("Options", 1.5f, TextMaster.minecraftFont, new Vector2f(0, 40), DisplayManager.getDefaultWidth(), true, true);
    }

    public void update() {
        fullscreenButton.update();
        aboutButton.update();
        logoutButton.update();
        guiScaleButton.update();
        doneButton.update();
        resetSettings.update();
    }

    public void render(Renderer renderer) {
        GUIShader guiShader = new GUIShader();
        guiShader.start();
        guiShader.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        fullscreenButton.render(renderer, guiShader);
        aboutButton.render(renderer, guiShader);
        logoutButton.render(renderer, guiShader);
        doneButton.render(renderer, guiShader);
        guiScaleButton.render(renderer, guiShader);
        resetSettings.render(renderer, guiShader);
        guiShader.stop();
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        fullscreenButton.resize();
        aboutButton.resize();
        logoutButton.resize();
        doneButton.resize();
        guiScaleButton.resize();
        resetSettings.resize();
    }

    @Override
    public void cleanUp() {
        fullscreenButton.cleanUp();
        aboutButton.cleanUp();
        logoutButton.cleanUp();
        doneButton.cleanUp();
        guiScaleButton.cleanUp();
        label.remove();
        resetSettings.cleanUp();
    }
}
