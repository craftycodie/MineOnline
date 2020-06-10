package gg.codie.mineonline.gui;

import gg.codie.mineonline.Globals;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.Camera;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.components.LargeButton;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.net.URI;

public class AboutMenuScreen implements IMenuScreen {
    LargeButton discordButton;
    LargeButton websiteButton;
    LargeButton doneButton;
    GUIText label;
    GUIText info;


    public AboutMenuScreen() {
        discordButton = new LargeButton("Discord", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) + 8), new IOnClickListener() {
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

        websiteButton = new LargeButton("Website", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) + 56), new IOnClickListener() {
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
                MenuManager.setMenuScreen(new MainMenuScreen());
            }
        });

        label = new GUIText("About", 1.5f, TextMaster.minecraftFont, new Vector2f(0, 40), DisplayManager.getDefaultWidth(), true, true);
        info = new GUIText("MineOnline " + Globals.LAUNCHER_VERSION  + " by @codieradical.", 1.5f, TextMaster.minecraftFont, new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) - 100), 400, true, true);
        info.setMaxLines(0);
    }

    public void update() {
        discordButton.update();
        websiteButton.update();
        doneButton.update();
    }

    public void render(Renderer renderer) {
        GUIShader guiShader = new GUIShader();
        guiShader.start();
        guiShader.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        discordButton.render(renderer, guiShader);
        websiteButton.render(renderer, guiShader);
        doneButton.render(renderer, guiShader);
        guiShader.stop();
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        discordButton.resize();
        websiteButton.resize();
        doneButton.resize();
    }

    @Override
    public void cleanUp() {
        doneButton.cleanUp();
        websiteButton.cleanUp();
        discordButton.cleanUp();
        label.remove();
        info.remove();
    }
}
