package gg.codie.mineonline.gui;

import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.components.LargeButton;
import gg.codie.mineonline.gui.components.MediumButton;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.mineonline.gui.rendering.animation.WalkPlayerAnimation;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.common.utils.FileUtils;
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
    MediumButton modelButton;
    LargeButton doneButton;
    JFileChooser fileChooser = new JFileChooser();
    GUIText label;

    String skinPath = "";
    private boolean unsavedSkin = false;
    private boolean unsavedCloak = false;
    private boolean unsavedModel = false;
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

                                    unsavedSkin = false;

                                    try {
                                        BufferedImage bufferedImage = ImageIO.read(new File(skinPath));
                                        if (bufferedImage.getHeight() >= 64)
                                            bufferedImage = TextureHelper.cropImage(bufferedImage, 0, 0, 64, 64);
                                        else
                                            bufferedImage = TextureHelper.cropImage(bufferedImage, 0, 0, 64, 32);
                                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                                        ImageIO.write(bufferedImage, "png", os);
                                        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                                        //MineOnlineAPI.uploadSkin(Session.session.getUuid(), Session.session.getAccessToken(), is);
                                    } catch (IOException ex) {
                                        JOptionPane.showMessageDialog(null, "Failed to upload skin.");
                                        failed = true;
                                    }
                                    if(failed) {
                                        unsavedSkin = true;
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

                        unsavedModel = false;

                        try {
                            //MineOnlineAPI.setSkinMetadata(Session.session.getUuid(), Session.session.getAccessToken(), metadata);
                        } catch (Exception ex) {
                            unsavedModel = true;
                        }
                    }
                });
            }
        });

        doneButton = new LargeButton("Done", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                if(unsavedSkin || unsavedCloak || unsavedModel) {
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

        if(modelButton.getName() != "Model: " + (slim ? "Alex" : "Steve"))
            modelButton.setName("Model: " + (slim ? "Alex" : "Steve"));

        skinButton.update();
        modelButton.update();
        doneButton.update();
    }

    public void render(Renderer renderer) {
        GUIShader.singleton.start();
        GUIShader.singleton.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        skinButton.render(renderer, GUIShader.singleton);
        modelButton.render(renderer, GUIShader.singleton);
        doneButton.render(renderer, GUIShader.singleton);

        GUIShader.singleton.stop();
    }

    public boolean showPlayer() {
        return true;
    }

    public void resize() {
        skinButton.resize();
        doneButton.resize();
    }

    @Override
    public void cleanUp() {
        skinButton.cleanUp();
        modelButton.cleanUp();
        doneButton.cleanUp();
        label.remove();
    }
}
