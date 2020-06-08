package gg.codie.mineonline.gui;

import gg.codie.mineonline.MinecraftLauncher;
import gg.codie.mineonline.MinecraftVersionInfo;
import gg.codie.mineonline.Properties;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.components.MediumButton;
import gg.codie.mineonline.gui.components.TinyButton;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;

public class MainMenuScreen implements IMenuScreen {
    GUIObject logo;
    MediumButton playButton;
    MediumButton joinServerButton;
    MediumButton versionButton;
    TinyButton optionsButton;
    TinyButton skinButton;

    static SelectVersionMenuScreen selectVersionMenuScreen = null;

    public MainMenuScreen() {
        RawModel logoModel = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) -200) + DisplayManager.getXBuffer(), Display.getHeight() - DisplayManager.scaledHeight(69)), new Vector2f(DisplayManager.scaledWidth(400), DisplayManager.scaledHeight(49)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 40), new Vector2f(400, 49)));
        ModelTexture logoTexture = new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")));
        TexturedModel texuredLogoModel =  new TexturedModel(logoModel, logoTexture);
        logo = new GUIObject("logo", texuredLogoModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

        Properties.loadProperties();
        String jarPath = Properties.properties.has("selectedJar") ? Properties.properties.getString("selectedJar") : null;

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
                        Properties.properties.put("selectedJar", selectVersionMenuScreen.getSelectedPath());
                        Properties.saveProperties();
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
    }

    public void render(Renderer renderer) {
        GUIShader guiShader = new GUIShader();
        guiShader.start();
        guiShader.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        renderer.renderGUI(logo, guiShader);
        playButton.render(renderer, guiShader);
        joinServerButton.render(renderer, guiShader);
        versionButton.render(renderer, guiShader);
        optionsButton.render(renderer, guiShader);
        skinButton.render(renderer, guiShader);
        guiShader.stop();
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
    }
}
