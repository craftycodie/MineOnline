package gg.codie.mineonline.gui;

import gg.codie.minecraft.client.Options;
import gg.codie.mineonline.*;
import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.components.InputField;
import gg.codie.mineonline.gui.components.LargeButton;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import org.lwjgl.util.vector.Vector2f;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class JoinServerScreen implements IMenuScreen {
    InputField serverIPField;
    LargeButton versionButton;
    LargeButton connectButton;
    LargeButton doneButton;
    LargeButton serverListButton;
    GUIText label;

    static SelectVersionMenuScreen selectVersionMenuScreen = null;

    public JoinServerScreen(String value) {
        String server = "";
        try {
            server = new Options(LauncherFiles.MINECRAFT_OPTIONS_PATH).getOption("lastServer").replace("_", ":");
        } catch (Exception e) {
            /// ignore
        }
        serverIPField = new InputField("Server IP Input", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 202, (DisplayManager.getDefaultHeight() / 2) - 42),  (value != null) ? value : server, new IOnClickListener() {
            @Override
            public void onClick() {
                try {
                    try {
                        new Options(LauncherFiles.MINECRAFT_OPTIONS_PATH).setOption("lastServer", serverIPField.getValue().replace(":", "_"));
                    } catch (Exception ex) {
                        // ignore
                    }
                    String[] split = serverIPField.getValue().split(":");

                    InetAddress inetAddress = InetAddress.getByName(split[0]);

                    String mppass = MinecraftAPI.getMpPass(Session.session.getSessionToken(), inetAddress.getHostAddress(), split.length > 1 ? split[1] : "25565");

                    new MinecraftLauncher(Settings.settings.getString("selectedJar"), split[0], split.length > 1 ? split[1] : "25565", mppass).startMinecraft();
                }
                catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Settings.loadSettings();
        String jarPath = Settings.settings.has("selectedJar") ? Settings.settings.getString("selectedJar") : null;

        String jarName = jarPath != null ? new File(jarPath).getName() : null;
        MinecraftVersionInfo.MinecraftVersion version = null;
        if(jarPath != null) {
            version = MinecraftVersionInfo.getVersion(jarPath);
        }

        versionButton = new LargeButton(jarPath != null ? (version != null ? "Version: " + version.name : jarName) : "Select Version", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) + 8), new IOnClickListener() {
            @Override
            public void onClick() {
                selectVersionMenuScreen = new SelectVersionMenuScreen(null, new IOnClickListener() {
                    @Override
                    public void onClick() {
                        Settings.settings.put("selectedJar", selectVersionMenuScreen.getSelectedPath());
                        Settings.saveSettings();
                        MenuManager.setMenuScreen(new JoinServerScreen(serverIPField.getValue()));
                    }
                }, null);
                MenuManager.setMenuScreen(selectVersionMenuScreen);
            }
        });

        connectButton = new LargeButton("Connect", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, DisplayManager.getDefaultHeight() - 66), new IOnClickListener() {
            @Override
            public void onClick() {
                try {
                    try {
                        new Options(LauncherFiles.MINECRAFT_OPTIONS_PATH).setOption("lastServer", serverIPField.getValue().replace(":", "_"));
                    } catch (Exception ex) {
                        // ignore
                    }
                    String[] split = serverIPField.getValue().split(":");


                    InetAddress inetAddress = InetAddress.getByName(split[0]);

                    String mppass = MinecraftAPI.getMpPass(Session.session.getSessionToken(), inetAddress.getHostAddress(), split.length > 1 ? split[1] : "25565");

                    new MinecraftLauncher(Settings.settings.getString("selectedJar"), split[0], split.length > 1 ? split[1] : "25565", mppass).startMinecraft();
                }
                catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        if(jarName == null) {
            connectButton.setDisabled(true);
        }

        serverListButton = new LargeButton("Server List", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) + 56), new IOnClickListener() {
            @Override
            public void onClick() {
                try {
                    MenuManager.setMenuScreen(new ServerListMenuScreen());
                } catch (Exception ex) {}
            }
        });

        if (!Session.session.isOnline()) {
            serverListButton.setDisabled(true);
        }

        doneButton = new LargeButton("Cancel", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                MenuManager.setMenuScreen(new MainMenuScreen());
            }
        });

        label = new GUIText("Play Multiplayer", 1.5f, TextMaster.minecraftFont, new Vector2f(0, 40), DisplayManager.getDefaultWidth(), true, true);
    }

    public void update() {
        if (serverIPField.getValue().isEmpty() && !connectButton.getDisabled()) {
            connectButton.setDisabled(true);
        } else if (!serverIPField.getValue().isEmpty() && connectButton.getDisabled()) {
            connectButton.setDisabled(false);
        }

        serverIPField.update();
        versionButton.update();
        connectButton.update();
        doneButton.update();
        serverListButton.update();
    }

    public void render(Renderer renderer) {
        GUIShader.singleton.start();
        GUIShader.singleton.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        serverIPField.render(renderer, GUIShader.singleton);
        versionButton.render(renderer, GUIShader.singleton);
        connectButton.render(renderer, GUIShader.singleton);
        doneButton.render(renderer, GUIShader.singleton);
        serverListButton.render(renderer, GUIShader.singleton);
        GUIShader.singleton.stop();
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        serverIPField.resize();
        connectButton.resize();
        versionButton.resize();
        doneButton.resize();
        serverListButton.resize();
    }

    @Override
    public void cleanUp() {
        label.remove();
        serverIPField.cleanUp();
        connectButton.cleanUp();
        versionButton.cleanUp();
        doneButton.cleanUp();
        serverListButton.cleanUp();
    }
}
