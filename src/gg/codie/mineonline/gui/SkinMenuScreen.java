package gg.codie.mineonline.gui;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.mineonline.gui.rendering.animation.WalkPlayerAnimation;
import gg.codie.mineonline.gui.rendering.components.LargeButton;
import gg.codie.mineonline.gui.rendering.components.MediumButton;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import gg.codie.utils.FileUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

public class SkinMenuScreen implements IMenuScreen {
    MediumButton skinButton;
    MediumButton cloakButton;
    MediumButton resetCloakButton;
    MediumButton submitButton;
    LargeButton doneButton;
    JFileChooser fileChooser = new JFileChooser();
    GUIText label;

    String skinPath = new String();
    String cloakPath = new String();
    private boolean unsavedChanges = false;

    public SkinMenuScreen() {
        RawModel logoModel = Loader.singleton.loadGUIToVAO(new Vector2f((DisplayManager.getDefaultWidth() / 2) -200, Display.getHeight() - 69), new Vector2f(400, 49), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 40), new Vector2f(400, 49)));
        ModelTexture logoTexture = new ModelTexture(Loader.singleton.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")));
        TexturedModel texuredLogoModel =  new TexturedModel(logoModel, logoTexture);

        PlayerGameObject.thePlayer.translate(new Vector3f(0, 6, 0));
        PlayerGameObject.thePlayer.scale(new Vector3f(0.8f, 0.8f, 0.8f));
        PlayerGameObject.thePlayer.setPlayerAnimation(new WalkPlayerAnimation());

        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                return FileUtils.getExtension(f) != null && FileUtils.getExtension(f).equals("png");
            }

            @Override
            public String getDescription() {
                return "PNG image file (.png)";
            }
        });

        skinButton = new MediumButton("Select Skin", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) - 40), new IOnClickListener() {
            @Override
            public void onClick() {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int returnVal = fileChooser.showOpenDialog(DisplayManager.getCanvas());

                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fileChooser.getSelectedFile();
                            skinButton.setName("Skin: " + file.getName());

                            skinPath = file.getPath();

                            if(skinPath.isEmpty())
                                return;

                            unsavedChanges = true;

                            File skinTexture = new File(skinPath);
                            if (skinTexture.exists() && PlayerGameObject.thePlayer != null) {
                                try {
                                    PlayerGameObject.thePlayer.setSkin(Paths.get(skinTexture.getPath()).toUri().toURL());
                                } catch (MalformedURLException mx) {

                                }
                            }
                        }
                    }
                });
            }
        });

        cloakButton = new MediumButton("Select Cloak", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) + 8), new IOnClickListener() {
            @Override
            public void onClick() {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int returnVal = fileChooser.showOpenDialog(DisplayManager.getCanvas());

                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fileChooser.getSelectedFile();
                            cloakButton.setName("Cloak: " + file.getName());

                            cloakPath = file.getPath();

                            if(cloakPath.isEmpty())
                                return;

                            unsavedChanges = true;

                            File cloakTexture = new File(cloakPath);
                            if (cloakTexture.exists() && PlayerGameObject.thePlayer != null) {
                                try {
                                    PlayerGameObject.thePlayer.setCloak(Paths.get(cloakTexture.getPath()).toUri().toURL());
                                } catch (MalformedURLException mx) {

                                }
                            }
                        }
                    }
                });
            }
        });

        resetCloakButton = new MediumButton("Remove Cloak", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) + 56), new IOnClickListener() {
            @Override
            public void onClick() {
                if(MinecraftAPI.removecloak(Session.session.getSessionToken())) {
                    if(PlayerGameObject.thePlayer != null) {
                        PlayerGameObject.thePlayer.setCloak(LauncherFiles.TEMPLATE_CLOAK_PATH);
                    }
                }
            }
        });

        submitButton = new MediumButton("Submit", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) + 104), new IOnClickListener() {
            @Override
            public void onClick() {
                boolean failed = false;
                if(!skinPath.isEmpty()) {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(new File(skinPath));
                        bufferedImage = TextureHelper.cropImage(bufferedImage, 0, 0, 64, 32);
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "png", os);
                        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                        MinecraftAPI.uploadSkin(Session.session.getUsername(), Session.session.getSessionToken(), is);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Failed to upload skin.");
                        failed = true;
                    }
                }
                if(!cloakPath.isEmpty()) {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(new File(cloakPath));
                        bufferedImage = TextureHelper.cropImage(bufferedImage, 0, 0, 64, 32);
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "png", os);
                        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                        MinecraftAPI.uploadCloak(Session.session.getUsername(), Session.session.getSessionToken(), is);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Failed to upload skin.");
                        failed = true;
                    }
                }
                if(!failed) {
                    unsavedChanges = false;
                }
            }
        });

        doneButton = new LargeButton("Done", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                if(unsavedChanges) {
                    Session.session.cacheSkin();
                }

                PlayerRendererTest.setMenuScreen(new MainMenuScreen());
                PlayerGameObject.thePlayer.scale(new Vector3f(1.25f, 1.25f, 1.25f));
                PlayerGameObject.thePlayer.translate(new Vector3f(0, -6, 0));
                PlayerGameObject.thePlayer.setPlayerAnimation(new IdlePlayerAnimation());
            }
        });

        label = new GUIText("Change Skin", 1.5f, TextMaster.minecraftFont, new Vector2f(0, 40), DisplayManager.getDefaultWidth(), true);

    }

    public void update() {
        skinButton.update();
        cloakButton.update();
        resetCloakButton.update();
        submitButton.update();
        doneButton.update();
    }

    public void render(Renderer renderer) {
        GUIShader guiShader = new GUIShader();
        guiShader.start();
        guiShader.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        skinButton.render(renderer, guiShader);
        cloakButton.render(renderer, guiShader);
        resetCloakButton.render(renderer, guiShader);
        submitButton.render(renderer, guiShader);
        doneButton.render(renderer, guiShader);
        guiShader.stop();
    }

    public boolean showPlayer() {
        return true;
    }

    public void resize() {
        skinButton.resize();
        cloakButton.resize();
        resetCloakButton.resize();
        submitButton.resize();
        doneButton.resize();
    }

    @Override
    public void cleanUp() {
        skinButton.cleanUp();
        cloakButton.cleanUp();
        resetCloakButton.cleanUp();
        submitButton.cleanUp();
        doneButton.cleanUp();
        label.remove();
    }
}
