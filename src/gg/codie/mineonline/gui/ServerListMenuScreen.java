package gg.codie.mineonline.gui;

import gg.codie.mineonline.*;
import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.Camera;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.components.MediumButton;
import gg.codie.mineonline.gui.components.SelectableServer;
import gg.codie.mineonline.gui.components.SelectableServerList;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.utils.JSONUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;
import java.io.IOException;

public class ServerListMenuScreen implements IMenuScreen {
    MediumButton connectButton;
    MediumButton backButton;
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
                                    try {
                                        new MinecraftOptions(LauncherFiles.MINECRAFT_OPTIONS_PATH).setOption("lastServer", selectedServer.server.ip + "_" + selectedServer.server.port);
                                    } catch (Exception ex) {
                                        // ignore
                                    }
                                    String mppass = MinecraftAPI.getMpPass(Session.session.getSessionToken(), selectedServer.server.ip, "" + selectedServer.server.port);

                                    new MinecraftLauncher(path, selectedServer.server.ip, "" + selectedServer.server.port, mppass).startMinecraft();
                                    return;
                                }
                            }
                        }


                        selectVersionMenuScreen = new SelectVersionMenuScreen(new IOnClickListener() {
                            @Override
                            public void onClick() {
                                MenuManager.setMenuScreen(new ServerListMenuScreen());
                            }
                        }, new IOnClickListener() {
                            @Override
                            public void onClick() {
                                try {
                                    new MinecraftOptions(LauncherFiles.MINECRAFT_OPTIONS_PATH).setOption("lastServer", selectedServer.server.ip + "_" + selectedServer.server.port);
                                } catch (Exception ex) {
                                    // ignore
                                }
                                String mppass = MinecraftAPI.getMpPass(Session.session.getSessionToken(), selectedServer.server.ip, "" + selectedServer.server.port);
                                try {
                                    new MinecraftLauncher(selectVersionMenuScreen.getSelectedPath(), selectedServer.server.ip, "" + selectedServer.server.port, mppass).startMinecraft();
                                } catch (Exception ex) {

                                }
                                return;
                            }
                        }, "Play");

                        MenuManager.setMenuScreen(selectVersionMenuScreen);

                        //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\b1.7.3-modded.jar", null, null, null).startMinecraft();

                        //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\c0.0.11a-launcher.jar", null, null, null).startMinecraft();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        connectButton = new MediumButton("Connect", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 8, DisplayManager.getDefaultHeight() - 20), selectListener);
        connectButton.setDisabled(true);

        backButton = new MediumButton("Back", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 308, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                MenuManager.setMenuScreen(new JoinServerScreen(null));
            }
        });

        selectableServerList = new SelectableServerList("version list", new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1), selectListener);

        label = new GUIText("Server List", 1.5f, TextMaster.minecraftFont, new Vector2f(0, 40), DisplayManager.getDefaultWidth(), true, true);

    }

    public void update() {
        if(this.connectButton.getDisabled() && selectableServerList.getSelected() != null)
            this.connectButton.setDisabled(false);

        connectButton.update();
        backButton.update();
        selectableServerList.update();
    }

    public void render(Renderer renderer) {
        GUIShader guiShader = new GUIShader();
        guiShader.start();
        guiShader.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();

        connectButton.render(renderer, guiShader);
        backButton.render(renderer, guiShader);
        selectableServerList.render(renderer, guiShader);
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        connectButton.resize();
        backButton.resize();
        selectableServerList.resize();
    }

    @Override
    public void cleanUp() {
        connectButton.cleanUp();
        backButton.cleanUp();
        selectableServerList.cleanUp();
        label.remove();
    }
}
