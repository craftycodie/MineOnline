package gg.codie.mineonline.gui;

import gg.codie.mineonline.MinecraftLauncher;
import gg.codie.mineonline.MinecraftVersionInfo;
import gg.codie.mineonline.Properties;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.Camera;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.PlayerRendererTest;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.components.MediumButton;
import gg.codie.mineonline.gui.rendering.components.SelectableServer;
import gg.codie.mineonline.gui.rendering.components.SelectableServerList;
import gg.codie.mineonline.gui.rendering.components.SelectableVersionList;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.utils.JSONUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class ServerListMenuScreen implements IMenuScreen {
    MediumButton doneButton;
    MediumButton browseButton;
    GUIText label;

    SelectableServerList selectableServerList;

    static SelectVersionMenuScreen selectVersionMenuScreen = null;

    public ServerListMenuScreen() {
        IOnClickListener selectListener = new IOnClickListener() {
            @Override
            public void onClick() {
                SelectableServer selectedServer = selectableServerList.getSelected();
                if (selectedServer != null) {
                    try {
                        Properties.loadProperties();

                        MinecraftVersionInfo.MinecraftVersion serverVersion = MinecraftVersionInfo.getVersionByMD5(selectedServer.server.md5);

                        String[] minecraftJars = Properties.properties.has("minecraftJars") ? JSONUtils.getStringArray(Properties.properties.getJSONArray("minecraftJars")) : new String[0];

                        for (String compatibleClientMd5 : serverVersion.clientMd5s) {
                            for (String path : minecraftJars) {
                                File file = new File(path);

                                MinecraftVersionInfo.MinecraftVersion clientVersion = MinecraftVersionInfo.getVersion(path);

                                try {
                                    if (!MinecraftVersionInfo.isRunnableJar(file.getPath())) {
                                        continue;
                                    }
                                } catch (IOException ex) {
                                    continue;
                                }

                                if(clientVersion != null && clientVersion.md5.equals(compatibleClientMd5)) {
                                    Properties.properties.put("lastServer", selectedServer.server.ip + ":" + selectedServer.server.port);
                                    Properties.saveProperties();
                                    String mppass = MinecraftAPI.getMpPass(Session.session.getSessionToken(), selectedServer.server.ip, "" + selectedServer.server.port);

                                    new MinecraftLauncher(path, selectedServer.server.ip, "" + selectedServer.server.port, mppass).startMinecraft();
                                    return;
                                }
                            }
                        }


                        selectVersionMenuScreen = new SelectVersionMenuScreen(new IOnClickListener() {
                            @Override
                            public void onClick() {
                                PlayerRendererTest.setMenuScreen(new ServerListMenuScreen());
                            }
                        }, new IOnClickListener() {
                            @Override
                            public void onClick() {
                                Properties.properties.put("lastServer", selectedServer.server.ip + ":" + selectedServer.server.port);
                                Properties.saveProperties();
                                String mppass = MinecraftAPI.getMpPass(Session.session.getSessionToken(), selectedServer.server.ip, "" + selectedServer.server.port);
                                try {
                                    new MinecraftLauncher(selectVersionMenuScreen.getSelectedPath(), selectedServer.server.ip, "" + selectedServer.server.port, mppass).startMinecraft();
                                } catch (Exception ex) {

                                }
                                return;
                            }
                        }, "Play");

                        PlayerRendererTest.setMenuScreen(selectVersionMenuScreen);

                        //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\b1.7.3-modded.jar", null, null, null).startMinecraft();

                        //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\c0.0.11a-launcher.jar", null, null, null).startMinecraft();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        doneButton = new MediumButton("Connect", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 8, DisplayManager.getDefaultHeight() - 20), selectListener);

        browseButton = new MediumButton("Back", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 308, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                PlayerRendererTest.setMenuScreen(new JoinServerScreen(null));
            }
        });

        selectableServerList = new SelectableServerList("version list", new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1), selectListener);

        label = new GUIText("Server List", 1.5f, TextMaster.minecraftFont, new Vector2f(0, 40), DisplayManager.getDefaultWidth(), true, true);

    }

    public void update() {
        doneButton.update();
        browseButton.update();
        selectableServerList.update();
    }

    public void render(Renderer renderer) {
        GUIShader guiShader = new GUIShader();
        guiShader.start();
        guiShader.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();

        doneButton.render(renderer, guiShader);
        browseButton.render(renderer, guiShader);
        selectableServerList.render(renderer, guiShader);
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        doneButton.resize();
        browseButton.resize();
        selectableServerList.resize();
    }

    @Override
    public void cleanUp() {
        doneButton.cleanUp();
        browseButton.cleanUp();
        selectableServerList.cleanUp();
        label.remove();
    }
}
