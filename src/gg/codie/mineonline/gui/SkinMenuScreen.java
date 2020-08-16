package gg.codie.mineonline.gui;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.mineonline.gui.rendering.animation.WalkPlayerAnimation;
import gg.codie.mineonline.gui.components.LargeButton;
import gg.codie.mineonline.gui.components.MediumButton;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.utils.FileUtils;
import org.json.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

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
    MediumButton modelButton;
    LargeButton doneButton;
    JFileChooser fileChooser = new JFileChooser();
    GUIText label;

    String skinPath = "";
    String cloakPath = "";
    private boolean unsavedChanges = false;
    private boolean slim = PlayerGameObject.thePlayer.getSlim();

    public SkinMenuScreen() {
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

                            skinPath = file.getPath();

                            if(skinPath.isEmpty())
                                return;

                            File skinTexture = new File(skinPath);
                            if (skinTexture.exists() && PlayerGameObject.thePlayer != null) {
                                try {
                                    boolean failed = false;
                                    PlayerGameObject.thePlayer.setSkin(Paths.get(skinTexture.getPath()).toUri().toURL());

                                    try {
                                        BufferedImage bufferedImage = ImageIO.read(new File(skinPath));
                                        bufferedImage = TextureHelper.cropImage(bufferedImage, 0, 0, 64, 32);
                                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                                        ImageIO.write(bufferedImage, "png", os);
                                        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                                        MineOnlineAPI.uploadSkin(Session.session.getUuid(), Session.session.getSessionToken(), is);
                                    } catch (IOException ex) {
                                        JOptionPane.showMessageDialog(null, "Failed to upload skin.");
                                        failed = true;
                                    }
                                    if(failed) {
                                        unsavedChanges = true;
                                    }

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

                            cloakPath = file.getPath();

                            if(cloakPath.isEmpty())
                                return;

                            File cloakTexture = new File(cloakPath);
                            if (cloakTexture.exists() && PlayerGameObject.thePlayer != null) {
                                try {
                                    boolean failed = false;
                                    PlayerGameObject.thePlayer.setCloak(Paths.get(cloakTexture.getPath()).toUri().toURL());

                                    try {
                                        BufferedImage bufferedImage = ImageIO.read(new File(cloakPath));
                                        bufferedImage = TextureHelper.cropImage(bufferedImage, 0, 0, 64, 32);
                                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                                        ImageIO.write(bufferedImage, "png", os);
                                        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                                        MineOnlineAPI.uploadCloak(Session.session.getUuid(), Session.session.getSessionToken(), is);
                                    } catch (IOException ex) {
                                        JOptionPane.showMessageDialog(null, "Failed to upload skin.");
                                        failed = true;
                                    }
                                    if(failed) {
                                        unsavedChanges = true;
                                    }
                                } catch (MalformedURLException mx) {

                                }
                            }
                        }
                    }
                });
            }
        });

        modelButton = new MediumButton("Model: " + (slim ? "Alex" : "Steve"), new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) + 56), new IOnClickListener() {
            @Override
            public void onClick() {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        slim = !slim;
                        PlayerGameObject.thePlayer.setSlim(slim);

                        JSONObject metadata = new JSONObject();
                        metadata.put("slim", slim);

                        try {
                            MineOnlineAPI.setSkinMetadata(Session.session.getUuid(), Session.session.getSessionToken(), metadata);
                        } catch (Exception ex) {
                            unsavedChanges = true;
                        }
                    }
                });
            }
        });

        resetCloakButton = new MediumButton("Remove Cloak", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 30, (DisplayManager.getDefaultHeight() / 2) + 104), new IOnClickListener() {
            @Override
            public void onClick() {
                if(MineOnlineAPI.removecloak(Session.session.getSessionToken())) {
                    if(PlayerGameObject.thePlayer != null) {
                        PlayerGameObject.thePlayer.setCloak(LauncherFiles.TEMPLATE_CLOAK_PATH);
                        new File(LauncherFiles.CACHED_CLOAK_PATH).delete();
                    }
                }
            }
        });

        doneButton = new LargeButton("Done", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                if(unsavedChanges) {
                    Session.session.cacheSkin();
                }

                MenuManager.setMenuScreen(new MainMenuScreen());
                PlayerGameObject.thePlayer.scale(new Vector3f(1.25f, 1.25f, 1.25f));
                PlayerGameObject.thePlayer.translate(new Vector3f(0, -6, 0));
                PlayerGameObject.thePlayer.setPlayerAnimation(new IdlePlayerAnimation());
            }
        });

        label = new GUIText("Change Skin", 1.5f, TextMaster.minecraftFont, new Vector2f(0, 40), DisplayManager.getDefaultWidth(), true, true);

    }

    public void update() {
        if(!skinPath.isEmpty() && skinButton.getName() != "Skin: " + Paths.get(skinPath).getFileName().toString())
            skinButton.setName("Skin: " + Paths.get(skinPath).getFileName().toString());

        if(!cloakPath.isEmpty() && cloakButton.getName() != "Cloak: " + Paths.get(cloakPath).getFileName().toString())
            cloakButton.setName("Skin: " + Paths.get(cloakPath).getFileName().toString());

        if(modelButton.getName() != "Model: " + (slim ? "Alex" : "Steve"))
            modelButton.setName("Model: " + (slim ? "Alex" : "Steve"));

        skinButton.update();
        cloakButton.update();
        modelButton.update();
        resetCloakButton.update();
        doneButton.update();
    }

    public void render(Renderer renderer) {
        GUIShader.singleton.start();
        GUIShader.singleton.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        skinButton.render(renderer, GUIShader.singleton);
        cloakButton.render(renderer, GUIShader.singleton);
        modelButton.render(renderer, GUIShader.singleton);
        resetCloakButton.render(renderer, GUIShader.singleton);
        doneButton.render(renderer, GUIShader.singleton);
        GUIShader.singleton.stop();
    }

    public boolean showPlayer() {
        return true;
    }

    public void resize() {
        skinButton.resize();
        cloakButton.resize();
        resetCloakButton.resize();
        doneButton.resize();
    }

    @Override
    public void cleanUp() {
        skinButton.cleanUp();
        cloakButton.cleanUp();
        modelButton.cleanUp();
        resetCloakButton.cleanUp();
        doneButton.cleanUp();
        label.remove();
    }
}
