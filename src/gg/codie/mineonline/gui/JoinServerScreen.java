package gg.codie.mineonline.gui;

import gg.codie.mineonline.MinecraftLauncher;
import gg.codie.mineonline.MinecraftVersionInfo;
import gg.codie.mineonline.Properties;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.components.InputField;
import gg.codie.mineonline.gui.rendering.components.LargeButton;
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
        serverIPField = new InputField("Server IP Input", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 202, (DisplayManager.getDefaultHeight() / 2) - 42),  (value != null) ? value : Properties.properties.has("lastServer") ? Properties.properties.getString("lastServer") : "", new IOnClickListener() {
            @Override
            public void onClick() {
                try {
                    Properties.properties.put("lastServer", serverIPField.getValue());
                    Properties.saveProperties();
                    String[] split = serverIPField.getValue().split(":");

                    InetAddress inetAddress = InetAddress.getByName(split[0]);

                    String mppass = MinecraftAPI.getMpPass(Session.session.getSessionToken(), inetAddress.getHostAddress(), split.length > 1 ? split[1] : "25565");

                    new MinecraftLauncher(Properties.properties.getString("selectedJar"), split[0], split.length > 1 ? split[1] : "25565", mppass).startMinecraft();
                }
                catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Properties.loadProperties();
        String jarPath = Properties.properties.has("selectedJar") ? Properties.properties.getString("selectedJar") : null;

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
                        Properties.properties.put("selectedJar", selectVersionMenuScreen.getSelectedPath());
                        Properties.saveProperties();
                        PlayerRendererTest.setMenuScreen(new JoinServerScreen(serverIPField.getValue()));
                    }
                }, null);
                PlayerRendererTest.setMenuScreen(selectVersionMenuScreen);
            }
        });

        connectButton = new LargeButton("Connect", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, DisplayManager.getDefaultHeight() - 66), new IOnClickListener() {
            @Override
            public void onClick() {
                try {
                    Properties.properties.put("lastServer", serverIPField.getValue());
                    Properties.saveProperties();
                    String[] split = serverIPField.getValue().split(":");


                    InetAddress inetAddress = InetAddress.getByName(split[0]);

                    String mppass = MinecraftAPI.getMpPass(Session.session.getSessionToken(), inetAddress.getHostAddress(), split.length > 1 ? split[1] : "25565");

                    new MinecraftLauncher(Properties.properties.getString("selectedJar"), split[0], split.length > 1 ? split[1] : "25565", mppass).startMinecraft();
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
                    PlayerRendererTest.setMenuScreen(new ServerListMenuScreen());
                } catch (Exception ex) {}
            }
        });

        if (!Session.session.isOnline()) {
            serverListButton.setDisabled(true);
        }

        doneButton = new LargeButton("Cancel", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                PlayerRendererTest.setMenuScreen(new MainMenuScreen());
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
        GUIShader guiShader = new GUIShader();
        guiShader.start();
        guiShader.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        serverIPField.render(renderer, guiShader);
        versionButton.render(renderer, guiShader);
        connectButton.render(renderer, guiShader);
        doneButton.render(renderer, guiShader);
        serverListButton.render(renderer, guiShader);
        guiShader.stop();
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
