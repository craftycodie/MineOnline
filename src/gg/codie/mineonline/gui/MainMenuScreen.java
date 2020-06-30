package gg.codie.mineonline.gui;

import gg.codie.mineonline.*;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.components.MediumButton;
import gg.codie.mineonline.gui.components.TinyButton;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.io.File;
import java.net.URI;

public class MainMenuScreen implements IMenuScreen {
    GUIObject logo;
    MediumButton playButton;
    MediumButton joinServerButton;
    MediumButton versionButton;
    TinyButton optionsButton;
    TinyButton skinButton;

    GUIText updateAvailableText;

    static SelectVersionMenuScreen selectVersionMenuScreen = null;

    public MainMenuScreen() {
        RawModel logoModel = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) -200) + DisplayManager.getXBuffer(), Display.getHeight() - DisplayManager.scaledHeight(69)), new Vector2f(DisplayManager.scaledWidth(400), DisplayManager.scaledHeight(49)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 40), new Vector2f(400, 49)));
        ModelTexture logoTexture = new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")));
        TexturedModel texuredLogoModel =  new TexturedModel(logoModel, logoTexture);
        logo = new GUIObject("logo", texuredLogoModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

        Settings.loadSettings();
        String jarPath = Settings.settings.has(Settings.SELECTED_JAR) ? Settings.settings.getString(Settings.SELECTED_JAR) : null;

        playButton = new MediumButton("Play", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) - 40), new IOnClickListener() {
            @Override
            public void onClick() {
                try {
                    //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\b1.7.3-modded.jar", null, null, null).startMinecraft();
                    new MinecraftLauncher(jarPath, null, null, null).startMinecraft();

                    //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\c0.0.11a-launcher.jar", null, null, null).startMinecraft();
                } catch (Exception ex) {}
            }
        });

        joinServerButton = new MediumButton("Join Server", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) + 8), new IOnClickListener() {
            @Override
            public void onClick() {
                MenuManager.setMenuScreen(new JoinServerScreen(null));
            }
        });

        String jarName = jarPath != null ? new File(jarPath).getName() : null;
        MinecraftVersionInfo.MinecraftVersion version = null;
        if(jarPath != null) {
            version = MinecraftVersionInfo.getVersion(jarPath);
        }

        if(jarName == null) {
            playButton.setDisabled(true);
        }

        versionButton = new MediumButton(jarPath != null ? (version != null ? "Version: " + version.name : jarName) : "Select Version", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) + 56), new IOnClickListener() {
            @Override
            public void onClick() {
                selectVersionMenuScreen = new SelectVersionMenuScreen(null, new IOnClickListener() {
                    @Override
                    public void onClick() {
                        Settings.settings.put(Settings.SELECTED_JAR, selectVersionMenuScreen.getSelectedPath());
                        Settings.saveSettings();
                        MenuManager.setMenuScreen(new MainMenuScreen());
                    }
                }, null);
                MenuManager.setMenuScreen(selectVersionMenuScreen);
            }
        });

        optionsButton = new TinyButton("Options...", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) + 112), new IOnClickListener() {
            @Override
            public void onClick() {
                MenuManager.setMenuScreen(new OptionsMenuScreen());
            }
        });

        skinButton = new TinyButton("Change Skin", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 188, (DisplayManager.getDefaultHeight() / 2) + 112), new IOnClickListener() {
            @Override
            public void onClick() {
                MenuManager.setMenuScreen(new SkinMenuScreen());
            }
        });

        if(MenuManager.isUpdateAvailable()) {
            updateAvailableText = new GUIText("Update available!", 1.5f, TextMaster.minecraftFont, new Vector2f(0, DisplayManager.getDefaultHeight() - 32), DisplayManager.getDefaultWidth(), true, true);
            updateAvailableText.setColour(1f, 1f, 0f);
        }

        if (!Session.session.isOnline()) {
            skinButton.setDisabled(true);
        }
    }

    public void update() {
        playButton.update();
        joinServerButton.update();
        versionButton.update();
        optionsButton.update();
        skinButton.update();

        if(MenuManager.isUpdateAvailable()) {
            int x = Mouse.getX();
            int y = Mouse.getY();

            boolean mouseIsOver =
                    x - ((Display.getWidth() / 2) - DisplayManager.scaledWidth(75)) <= DisplayManager.scaledWidth(150)
                            && x - ((Display.getWidth() / 2) - DisplayManager.scaledWidth(75)) >= 0
                            && y - DisplayManager.scaledHeight(18) - DisplayManager.getYBuffer() <= DisplayManager.scaledHeight(22)
                            && y - DisplayManager.scaledHeight(18) - DisplayManager.getYBuffer() >= 0;

            if (MouseHandler.didClick() && mouseIsOver) {
                try {
                    Desktop.getDesktop().browse(new URI("http://" + Globals.API_HOSTNAME + "/download.jsp"));
                } catch (Exception ex) {

                }
            }
        }
    }

    public void render(Renderer renderer) {
        GUIShader.singleton.start();
        GUIShader.singleton.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        renderer.renderGUI(logo, GUIShader.singleton);
        playButton.render(renderer, GUIShader.singleton);
        joinServerButton.render(renderer, GUIShader.singleton);
        versionButton.render(renderer, GUIShader.singleton);
        optionsButton.render(renderer, GUIShader.singleton);
        skinButton.render(renderer, GUIShader.singleton);
        GUIShader.singleton.stop();
    }

    public boolean showPlayer() {
        return true;
    }

    public void resize() {
        playButton.resize();
        joinServerButton.resize();
        versionButton.resize();
        optionsButton.resize();
        skinButton.resize();
        logo.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) -200) + DisplayManager.getXBuffer(), Display.getHeight() - DisplayManager.scaledHeight(69)), new Vector2f(DisplayManager.scaledWidth(400), DisplayManager.scaledHeight(49)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 40), new Vector2f(400, 49))));
    }

    @Override
    public void cleanUp() {
        playButton.cleanUp();
        joinServerButton.cleanUp();
        versionButton.cleanUp();
        skinButton.cleanUp();
        optionsButton.cleanUp();
        if(updateAvailableText != null)
            updateAvailableText.remove();
    }
}
