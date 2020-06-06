package gg.codie.mineonline.gui;

import gg.codie.mineonline.Properties;
import gg.codie.mineonline.gui.IMenuScreen;
import gg.codie.mineonline.gui.MainMenuScreen;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.rendering.Camera;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.PlayerRendererTest;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.components.LargeButton;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;

import java.awt.*;
import java.net.URI;

public class AboutMenuScreen implements IMenuScreen {
    LargeButton aboutButton;
    LargeButton logoutButton;
    LargeButton doneButton;


    public AboutMenuScreen() {
        aboutButton = new LargeButton("Discord", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) + 8), new IOnClickListener() {
            @Override
            public void onClick() {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI("http://discord.codie.gg"));
                    } catch (Exception ex) {

                    }
                }
            }
        });

        logoutButton = new LargeButton("Website", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) + 56), new IOnClickListener() {
            @Override
            public void onClick() {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI("http://mineonline.codie.gg"));
                    } catch (Exception ex) {

                    }
                }
            }
        });

        doneButton = new LargeButton("Done", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                PlayerRendererTest.setMenuScreen(new MainMenuScreen());
            }
        });
    }

    public void update() {
        aboutButton.update();
        logoutButton.update();
        doneButton.update();
    }

    public void render(Renderer renderer) {
        GUIShader guiShader = new GUIShader();
        guiShader.start();
        guiShader.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        aboutButton.render(renderer, guiShader);
        logoutButton.render(renderer, guiShader);
        doneButton.render(renderer, guiShader);
        guiShader.stop();

        renderer.renderString(new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) - 100), "MineOnline Pre-Release by @codieradical.", Color.lightGray);

        renderer.renderCenteredString(new Vector2f(DisplayManager.getDefaultWidth() / 2, 40), "About", Color.white); //x, y, string to draw, color
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        aboutButton.resize();
        logoutButton.resize();
        doneButton.resize();
    }
}
