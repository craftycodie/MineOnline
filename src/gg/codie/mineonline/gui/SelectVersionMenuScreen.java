package gg.codie.mineonline.gui;

import gg.codie.mineonline.MinecraftVersionInfo;
import gg.codie.mineonline.Properties;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.components.MediumButton;
import gg.codie.mineonline.gui.rendering.components.SelectableVersionList;
import gg.codie.mineonline.gui.rendering.components.TinyButton;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.utils.JSONUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class SelectVersionMenuScreen implements IMenuScreen {
    MediumButton doneButton;
    MediumButton browseButtonBig;
    TinyButton browseButtonSmall;
    TinyButton backButton;
    GUIText label;

    IOnClickListener backListener;
    IOnClickListener doneListener;

    SelectableVersionList selectableVersionList;

    JFileChooser fileChooser = new JFileChooser();

    // Since browsing doesn't occur on the opengl thread, store the result here and check on update.
    String[] versionToAdd = null;

    public SelectVersionMenuScreen(IOnClickListener backListener, IOnClickListener doneListener, String doneText) {
        doneButton = new MediumButton(doneText != null ? doneText : "Done", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 8, DisplayManager.getDefaultHeight() - 20), doneListener);

        IOnClickListener browseListener = new IOnClickListener() {
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
                                if (existingJars[i].equals(file.getPath())) {
                                    selectableVersionList.selectVersion(file.getPath());
                                    return;
                                } else {
                                    newJars[i] = existingJars[i];
                                }
                            }
                            newJars[newJars.length - 1] = file.getPath();

                            Properties.properties.put("minecraftJars", newJars);
                            Properties.saveProperties();

                            if (minecraftVersion != null) {
                                versionToAdd = new String[]{minecraftVersion.name, file.getPath(), minecraftVersion.info};
                            } else {
                                versionToAdd = new String[]{"Unknown Version", file.getPath(), null};
                            }
                        }
                    }
                });
            }
        };

        if (backListener == null) {
            browseButtonBig = new MediumButton("Browse...", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 308, DisplayManager.getDefaultHeight() - 20), browseListener);
        } else {
            browseButtonSmall = new TinyButton("Browse...", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 150, DisplayManager.getDefaultHeight() - 20), browseListener);
            backButton = new TinyButton("Back", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 308, DisplayManager.getDefaultHeight() - 20), backListener);
        }

        selectableVersionList = new SelectableVersionList("version list", new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1), new IOnClickListener() {
            @Override
            public void onClick() {
                Properties.properties.put("selectedJar", selectableVersionList.getSelected());
                Properties.saveProperties();
                PlayerRendererTest.setMenuScreen(new MainMenuScreen());
            }
        });

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
                selectableVersionList.addVersion("Unknown Version", file.getPath(), null);
            }
        }

        label = new GUIText("Select Version", 1.5f, TextMaster.minecraftFont, new Vector2f(0, 40), DisplayManager.getDefaultWidth(), true, true);

    }

    public String getSelectedPath() {
        return selectableVersionList.getSelected();
    }

    public void update() {
        if (versionToAdd != null) {
            selectableVersionList.addVersion(versionToAdd[0], versionToAdd[1], versionToAdd[2]);
            selectableVersionList.selectVersion(versionToAdd[1]);
            versionToAdd = null;
        }

        doneButton.update();
        if(browseButtonBig != null)
            browseButtonBig.update();
        if(browseButtonSmall != null)
            browseButtonSmall.update();
        if(backButton != null)
            backButton.update();
        selectableVersionList.update();
    }

    public void render(Renderer renderer) {
        GUIShader guiShader = new GUIShader();
        guiShader.start();
        guiShader.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();

        doneButton.render(renderer, guiShader);
        if(browseButtonBig != null)
            browseButtonBig.render(renderer, guiShader);
        if(browseButtonSmall != null)
            browseButtonSmall.render(renderer, guiShader);
        if(backButton != null)
            backButton.render(renderer, guiShader);
        selectableVersionList.render(renderer, guiShader);
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        doneButton.resize();
        if(browseButtonBig != null)
            browseButtonBig.resize();
        if(browseButtonSmall != null)
            browseButtonSmall.resize();
        if(backButton != null)
            backButton.resize();
        selectableVersionList.resize();
    }

    @Override
    public void cleanUp() {
        doneButton.cleanUp();
        if(browseButtonBig != null)
            browseButtonBig.cleanUp();
        if(browseButtonSmall != null)
            browseButtonSmall.cleanUp();
        if(backButton != null)
            backButton.cleanUp();
        selectableVersionList.cleanUp();
        label.remove();
    }
}
