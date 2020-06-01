package gg.codie.mineonline.gui;

import gg.codie.mineonline.Properties;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.components.LargeButton;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;

public class OptionsMenuScreen implements IMenuScreen {
    LargeButton fullscreenButton;
    LargeButton aboutButton;
    LargeButton logoutButton;
    LargeButton doneButton;


    public OptionsMenuScreen() {
        RawModel logoModel = Loader.singleton.loadGUIToVAO(new Vector2f((DisplayManager.getDefaultWidth() / 2) -200, DisplayManager.getDefaultHeight() - 69), new Vector2f(400, 49), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 40), new Vector2f(400, 49)));
        ModelTexture logoTexture = new ModelTexture(Loader.singleton.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")));
        TexturedModel texuredLogoModel =  new TexturedModel(logoModel, logoTexture);

        fullscreenButton = new LargeButton("Fullscreen: " + (Properties.properties.getBoolean("fullscreen") ? "ON" : "OFF"), new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) - 40), new IOnClickListener() {
            @Override
            public void onClick() {
                //formopen = false;
                boolean fullcreen = !Properties.properties.getBoolean("fullscreen");
                Properties.properties.put("fullscreen", fullcreen);
                Properties.saveProperties();
                fullscreenButton.name = "Fullscreen: " + (Properties.properties.getBoolean("fullscreen") ? "ON" : "OFF");
            }
        });

        aboutButton = new LargeButton("About", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) + 8), null);

        logoutButton = new LargeButton("Logout", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) + 56), null);

        doneButton = new LargeButton("Done", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                PlayerRendererTest.setMenuScreen(new MainMenuScreen());
            }
        });
    }

    public void update() {
        fullscreenButton.update();
        aboutButton.update();
        logoutButton.update();
        doneButton.update();
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
        guiShader.stop();

        renderer.renderCenteredString(new Vector2f(DisplayManager.getDefaultWidth() / 2, 50), "Options", Color.white); //x, y, string to draw, color
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        fullscreenButton.resize();
        aboutButton.resize();
        logoutButton.resize();
        doneButton.resize();
    }
}
