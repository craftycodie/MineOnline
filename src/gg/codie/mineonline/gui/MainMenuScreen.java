package gg.codie.mineonline.gui;

import gg.codie.mineonline.*;
import gg.codie.mineonline.gui.components.MediumButton;
import gg.codie.mineonline.gui.components.TinyButton;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;

public class MainMenuScreen implements IMenuScreen {
    GUIObject logo;
    MediumButton playButton;
    MediumButton joinServerButton;
    MediumButton versionButton;
    MediumButton texturePacksButton;
    MediumButton optionsButton;
//    TinyButton skinButton;

    GUIText updateAvailableText;

    static SelectVersionMenuScreen selectVersionMenuScreen = null;

    public MainMenuScreen() {
        RawModel logoModel = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) -200) + DisplayManager.getXBuffer(), Display.getHeight() - DisplayManager.scaledHeight(69)), new Vector2f(DisplayManager.scaledWidth(400), DisplayManager.scaledHeight(49)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 40), new Vector2f(400, 49)));
        ModelTexture logoTexture = new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")));
        TexturedModel texuredLogoModel =  new TexturedModel(logoModel, logoTexture);
        logo = new GUIObject("logo", texuredLogoModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

        Settings.loadSettings();
        String jarPath = MinecraftVersionRepository.getSingleton().getLastSelectedJarPath();
        boolean jarSelected = jarPath != null && !jarPath.isEmpty();

        playButton = new MediumButton("Play", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) - 40), new IOnClickListener() {
            @Override
            public void onClick() {
                try {
                    MinecraftVersion.launchMinecraft(jarPath, null, null, null);
                } catch (Exception ex) {
                    System.out.println(ex);
                    JOptionPane.showMessageDialog(null, "Failed to launch!");
                }
            }
        });

        joinServerButton = new MediumButton("Join Server", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) + 8), new IOnClickListener() {
            @Override
            public void onClick() {
                MenuManager.setMenuScreen(new JoinServerScreen(null));
            }
        });

        String jarName = jarSelected ? new File(jarPath).getName() : null;
        MinecraftVersion version = null;
        if(jarPath != null) {
            version = MinecraftVersionRepository.getSingleton().getVersion(jarPath);
        }

        if(!jarSelected) {
            playButton.setDisabled(true);
        }

        String versionLabel = jarName;
        if(jarSelected && version != null) {
            versionLabel = version.name;
            GUIText sizeTest = new GUIText(versionLabel, 1.5f, TextMaster.minecraftFont, new Vector2f(0, 0), 300f, true, true);
            sizeTest.setMaxLines(0);
            if(sizeTest.getNumberOfLines() > 1)
                versionLabel = version.baseVersion;
            sizeTest.remove();
        }
        versionButton = new MediumButton(jarSelected ? versionLabel : "Select Version", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) + 56), new IOnClickListener() {
            @Override
            public void onClick() {
                selectVersionMenuScreen = new SelectVersionMenuScreen(null, new IOnClickListener() {
                    @Override
                    public void onClick() {
                        MinecraftVersionRepository.getSingleton().selectJar(selectVersionMenuScreen.getSelectedPath());
                        MenuManager.setMenuScreen(new MainMenuScreen());
                    }
                }, null);
                MenuManager.setMenuScreen(selectVersionMenuScreen);
            }
        });

        texturePacksButton = new MediumButton("Texture Packs", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) + 104), new IOnClickListener() {
            @Override
            public void onClick() {
                MenuManager.setMenuScreen(new TexturePacksMenuScreen());
            }
        });

        optionsButton = new MediumButton("Options...", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) + 152), new IOnClickListener() {
            @Override
            public void onClick() {
                MenuManager.setMenuScreen(new OptionsMenuScreen());
            }
        });

//        skinButton = new TinyButton("Change Skin", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 188, (DisplayManager.getDefaultHeight() / 2) + 160), new IOnClickListener() {
//            @Override
//            public void onClick() {
//                MenuManager.setMenuScreen(new SkinMenuScreen());
//            }
//        });
//
//        // TODO: Restore this.
//        skinButton.setDisabled(true);

        if(MenuManager.isUpdateAvailable() && !Globals.DEV) {
            updateAvailableText = new GUIText("Update available!", 1.5f, TextMaster.minecraftFont, new Vector2f(0, DisplayManager.getDefaultHeight() - 32), DisplayManager.getDefaultWidth(), true, true);
            updateAvailableText.setColour(1f, 1f, 0f);
        }
    }

    public void update() {
        playButton.update();
        joinServerButton.update();
        versionButton.update();
        texturePacksButton.update();
        optionsButton.update();
//        skinButton.update();

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
                    Desktop.getDesktop().browse(new URI(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/download"));
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
        texturePacksButton.render(renderer, GUIShader.singleton);
        optionsButton.render(renderer, GUIShader.singleton);
//        skinButton.render(renderer, GUIShader.singleton);
        GUIShader.singleton.stop();
    }

    public boolean showPlayer() {
        return true;
    }

    public void resize() {
        playButton.resize();
        joinServerButton.resize();
        versionButton.resize();
        texturePacksButton.resize();
        optionsButton.resize();
//        skinButton.resize();
        logo.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) -200) + DisplayManager.getXBuffer(), Display.getHeight() - DisplayManager.scaledHeight(69)), new Vector2f(DisplayManager.scaledWidth(400), DisplayManager.scaledHeight(49)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 40), new Vector2f(400, 49))));
    }

    @Override
    public void cleanUp() {
        playButton.cleanUp();
        joinServerButton.cleanUp();
        versionButton.cleanUp();
        texturePacksButton.cleanUp();
//        skinButton.cleanUp();
        optionsButton.cleanUp();
        if(updateAvailableText != null)
            updateAvailableText.remove();
    }
}
