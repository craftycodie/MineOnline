package gg.codie.mineonline.gui;

import gg.codie.mineonline.MinecraftVersionInfo;
import gg.codie.mineonline.Properties;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.mineonline.gui.rendering.components.LargeButton;
import gg.codie.mineonline.gui.rendering.components.MediumButton;
import gg.codie.mineonline.gui.rendering.components.SelectableVersion;
import gg.codie.mineonline.gui.rendering.components.SelectableVersionList;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import gg.codie.utils.JSONUtils;
import javafx.util.Pair;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

public class SelectVersionMenuScreen implements IMenuScreen {
    MediumButton doneButton;
    MediumButton browseButton;
    GUIText label;

    SelectableVersionList selectableVersionList;

    JFileChooser fileChooser = new JFileChooser();

    // Since browsing doesn't occur on the opengl thread, store the result here and check on update.
    String[] versionToAdd = null;

    public SelectVersionMenuScreen() {
        doneButton = new MediumButton("Done", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 8, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                Properties.properties.put("selectedJar", selectableVersionList.getSelected());
                Properties.saveProperties();
                PlayerRendererTest.setMenuScreen(new MainMenuScreen());
            }
        });

        browseButton = new MediumButton("Browse...", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 8 - 300, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int returnVal = fileChooser.showOpenDialog(DisplayManager.getCanvas());

                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fileChooser.getSelectedFile();

                            MinecraftVersionInfo.MinecraftVersion minecraftVersion = MinecraftVersionInfo.getVersion(file.getPath());

                            try {
                                if (!MinecraftVersionInfo.isRunnableJar(file.getPath())) {
                                    JOptionPane.showMessageDialog(null, "This jar file is incompatible:\nNo applet or main class found.");
                                    return;
                                }
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(null, "This jar file is incompatible:\nFailed to open.");
                                return;
                            }

                            String[] existingJars = Properties.properties.has("minecraftJars") ? JSONUtils.getStringArray(Properties.properties.getJSONArray("minecraftJars")) : new String[0];
                            String[] newJars = new String[existingJars.length + 1];

                            for (int i = 0; i < existingJars.length; i++) {
                                if(existingJars[i].equals(file.getPath())) {
                                    selectableVersionList.selectVersion(file.getPath());
                                    return;
                                } else {
                                    newJars[i] = existingJars[i];
                                }
                            }
                            newJars[newJars.length - 1] = file.getPath();

                            Properties.properties.put("minecraftJars", newJars);
                            Properties.saveProperties();

                            if(minecraftVersion != null) {
                                versionToAdd = new String[] { minecraftVersion.name, file.getPath(), minecraftVersion.info };
                            } else {
                                versionToAdd = new String[] { "Unknown Version (" + file.getName() + ")", file.getPath(), null };
                            }
                        }
                    }
                });
            }
        });

        selectableVersionList = new SelectableVersionList("version list", new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

        Properties.loadProperties();
        String[] minecraftJars = Properties.properties.has("minecraftJars") ? JSONUtils.getStringArray(Properties.properties.getJSONArray("minecraftJars")) : new String[0];
        for (String path : minecraftJars) {
            File file = new File(path);

            MinecraftVersionInfo.MinecraftVersion minecraftVersion = MinecraftVersionInfo.getVersion(path);

            try {
                if (!MinecraftVersionInfo.isRunnableJar(file.getPath())) {
                    continue;
                }
            } catch (IOException ex) {
                continue;
            }

            if(minecraftVersion != null) {
                selectableVersionList.addVersion(minecraftVersion.name, file.getPath(), minecraftVersion.info);
            } else {
                selectableVersionList.addVersion("Unknown Version (" + file.getName() + ")", file.getPath(), null);
            }
        }

        label = new GUIText("Select Version", 1.5f, TextMaster.minecraftFont, new Vector2f(0, 40), DisplayManager.getDefaultWidth(), true);

    }

    public void update() {
        if (versionToAdd != null) {
            selectableVersionList.addVersion(versionToAdd[0], versionToAdd[1], versionToAdd[2]);
            selectableVersionList.selectVersion(versionToAdd[1]);
            versionToAdd = null;
        }

        doneButton.update();
        browseButton.update();
        selectableVersionList.update();
    }

    public void render(Renderer renderer) {
        GUIShader guiShader = new GUIShader();
        guiShader.start();
        guiShader.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();

        doneButton.render(renderer, guiShader);
        browseButton.render(renderer, guiShader);
        selectableVersionList.render(renderer, guiShader);
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        doneButton.resize();
        browseButton.resize();
        selectableVersionList.resize();
    }

    @Override
    public void cleanUp() {
        doneButton.cleanUp();
        browseButton.cleanUp();
        selectableVersionList.cleanUp();
        label.remove();
    }
}
