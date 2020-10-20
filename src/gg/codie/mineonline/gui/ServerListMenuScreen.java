package gg.codie.mineonline.gui;

import gg.codie.minecraft.api.LauncherAPI;
import gg.codie.minecraft.client.Options;
import gg.codie.mineonline.*;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.gui.components.MediumButton;
import gg.codie.mineonline.gui.components.SelectableServer;
import gg.codie.mineonline.gui.components.SelectableServerList;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.Camera;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;
import java.util.Set;

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
                if (selectedServer != null && selectedServer.server.ip != null) {
                    try {
                        MinecraftVersion serverVersion = MinecraftVersionRepository.getSingleton().getVersionByMD5(selectedServer.server.md5);

                        Set<String> minecraftJars = MinecraftVersionRepository.getSingleton().getInstalledJars().keySet();

                        if(serverVersion != null) {
                            if (MinecraftVersionRepository.getSingleton().getLastSelectedJarPath() != null) {
                                MinecraftVersion selectedVersion = MinecraftVersionRepository.getSingleton().getVersion(MinecraftVersionRepository.getSingleton().getLastSelectedJarPath());
                                if (selectedVersion != null && selectedVersion.baseVersion.equals(serverVersion.baseVersion)) {
                                    String mppass = null;
                                    if (serverVersion != null && serverVersion.hasHeartbeat)
                                        mppass = MineOnlineAPI.getMpPass(Session.session.getSessionToken(), Session.session.getUsername(), Session.session.getUuid(), selectedServer.server.ip, "" + selectedServer.server.port);

                                    MinecraftVersion.launchMinecraft(MinecraftVersionRepository.getSingleton().getLastSelectedJarPath(), selectedServer.server.ip, "" + selectedServer.server.port, mppass);
                                }
                            }


                            for (String compatibleClientBaseVersion : serverVersion.clientVersions) {
                                for (String path : minecraftJars) {
                                    MinecraftVersion clientVersion = MinecraftVersionRepository.getSingleton().getInstalledJars().get(path);

                                    if (clientVersion != null && clientVersion.baseVersion.equals(compatibleClientBaseVersion)) {
                                        String mppass = null;
                                        if(serverVersion != null && serverVersion.hasHeartbeat)
                                            mppass = MineOnlineAPI.getMpPass(Session.session.getSessionToken(), Session.session.getUsername(), Session.session.getUuid(), selectedServer.server.ip, "" + selectedServer.server.port);

                                        MinecraftVersion.launchMinecraft(path, selectedServer.server.ip, "" + selectedServer.server.port, mppass);
                                        return;
                                    }
                                }


                                try {
                                    File clientJar = new File(LauncherFiles.MINECRAFT_VERSIONS_PATH + compatibleClientBaseVersion + File.separator + "client.jar");
                                    try {
                                        LauncherAPI.downloadVersion(compatibleClientBaseVersion);
                                    } catch (Exception ex) {
                                        System.err.println("Couldn't find " + compatibleClientBaseVersion + " in the official versions list.");
                                        ex.printStackTrace();
                                    }
                                    if (!clientJar.exists())
                                        MineOnlineAPI.downloadVersion(compatibleClientBaseVersion);

                                    MinecraftVersionRepository.getSingleton().addInstalledVersion(clientJar.getPath());
                                    String mppass = null;
                                    if (serverVersion != null && serverVersion.hasHeartbeat)
                                        mppass = MineOnlineAPI.getMpPass(Session.session.getSessionToken(), Session.session.getUsername(), Session.session.getUuid(), selectedServer.server.ip, "" + selectedServer.server.port);
                                    MinecraftVersion.launchMinecraft(clientJar.getPath(), selectedServer.server.ip, "" + selectedServer.server.port, mppass);
                                    return;
                                } catch (Exception ex) {
                                    ex.printStackTrace();
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
                                MinecraftVersion serverVersion = MinecraftVersionRepository.getSingleton().getVersionByMD5(selectedServer.server.md5);

                                String mppass = null;
                                if(serverVersion != null && serverVersion.hasHeartbeat)
                                    mppass = MineOnlineAPI.getMpPass(Session.session.getSessionToken(), Session.session.getUsername(), Session.session.getUuid(), selectedServer.server.ip, "" + selectedServer.server.port);
                                try {
                                    MinecraftVersion.launchMinecraft(selectVersionMenuScreen.getSelectedPath(), selectedServer.server.ip, "" + selectedServer.server.port, mppass);
                                } catch (Exception ex) {

                                }
                                return;
                            }
                        }, "Play");

                        MenuManager.setMenuScreen(selectVersionMenuScreen);
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
        connectButton.update();
        backButton.update();
        selectableServerList.update();

        if (selectableServerList.getSelected() != null && connectButton.getDisabled())
            connectButton.setDisabled(false);
    }

    public void render(Renderer renderer) {
        GUIShader.singleton.start();
        GUIShader.singleton.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();

        connectButton.render(renderer, GUIShader.singleton);
        backButton.render(renderer, GUIShader.singleton);
        selectableServerList.render(renderer, GUIShader.singleton);
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
