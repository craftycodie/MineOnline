package gg.codie.mineonline.gui;

import gg.codie.minecraft.client.Options;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.Session;
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
                            for (String compatibleClientMd5 : serverVersion.clientVersions) {
                                for (String path : minecraftJars) {
                                    File file = new File(path);

                                    MinecraftVersion clientVersion = MinecraftVersionRepository.getSingleton().getInstalledJars().get(path);

//                                    try {
//                                        if (!MinecraftVersion.isPlayableJar(file.getPath())) {
//                                            continue;
//                                        }
//                                    } catch (IOException ex) {
//                                        continue;
//                                    }

                                    if (clientVersion != null && clientVersion.baseVersion.equals(compatibleClientMd5)) {
                                        try {
                                            new Options(LauncherFiles.MINECRAFT_OPTIONS_PATH).setOption("lastServer", selectedServer.server.ip + "_" + selectedServer.server.port);
                                        } catch (Exception ex) {
                                            // ignore
                                        }

                                        String mppass = null;
                                        if(serverVersion != null && serverVersion.hasHeartbeat)
                                            mppass = MineOnlineAPI.getMpPass(Session.session.getSessionToken(), selectedServer.server.ip, "" + selectedServer.server.port);

                                        MinecraftVersion.launchMinecraft(path, selectedServer.server.ip, "" + selectedServer.server.port, mppass);
                                        return;
                                    }
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
                                    new Options(LauncherFiles.MINECRAFT_OPTIONS_PATH).setOption("lastServer", selectedServer.server.ip + "_" + selectedServer.server.port);
                                } catch (Exception ex) {
                                    // ignore
                                }

                                MinecraftVersion serverVersion = MinecraftVersionRepository.getSingleton().getVersionByMD5(selectedServer.server.md5);

                                String mppass = null;
                                if(serverVersion != null && serverVersion.hasHeartbeat)
                                    mppass = MineOnlineAPI.getMpPass(Session.session.getSessionToken(), selectedServer.server.ip, "" + selectedServer.server.port);

                                try {
                                    MinecraftVersion.launchMinecraft(selectVersionMenuScreen.getSelectedPath(), selectedServer.server.ip, "" + selectedServer.server.port, mppass);
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
        if(this.connectButton.getDisabled() && (selectableServerList.getSelected() != null && selectableServerList.getSelected().server.status.canJoin()))
            this.connectButton.setDisabled(false);
        else if(!this.connectButton.getDisabled() && (selectableServerList.getSelected() == null || !selectableServerList.getSelected().server.status.canJoin()))
            this.connectButton.setDisabled(true);
        connectButton.update();
        backButton.update();
        selectableServerList.update();
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
