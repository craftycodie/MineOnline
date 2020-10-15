package gg.codie.mineonline.gui;

import gg.codie.minecraft.api.AuthServer;
import gg.codie.minecraft.api.MojangAPI;
import gg.codie.minecraft.client.Options;
import gg.codie.mineonline.*;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import gg.codie.mineonline.patches.lwjgl.LWJGLDisplayPatch;
import gg.codie.mineonline.utils.Logging;
import gg.codie.utils.LastLogin;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Set;

public class MenuManager {

    public static boolean formopen = false;

    private static GUIText playerName;
    public static void setMenuScreen(IMenuScreen menuScreen) {

        if(MenuManager.menuScreen != null)
            MenuManager.menuScreen.cleanUp();

        MenuManager.menuScreen = menuScreen;

        if(playerName != null)
            playerName.remove();

        if(menuScreen.showPlayer())
            playerName = new GUIText(Session.session.getUsername(), 1.5f, TextMaster.minecraftFont, new Vector2f(168, 100), 160, true, true);
    }

    public static void resizeMenu() {
        if(menuScreen != null)
            menuScreen.resize();
    }

    private static IMenuScreen menuScreen;

    static WindowAdapter closeListener = new WindowAdapter(){
        public void windowClosing(WindowEvent e){
            if (DisplayManager.getFrame() != null)
                DisplayManager.getFrame().dispose();
            System.exit(0);
        }

        @Override
        public void windowOpened(WindowEvent e) {
            if (DisplayManager.getFrame() != null)
                DisplayManager.getFrame().getOwner().setBackground(java.awt.Color.black);
        }
    };

    private static GameObject playerScale;

    static boolean updateAvailable = false;
    public static boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public static void main(String[] args) throws Exception {
        Logging.enableLogging();

        ProgressDialog.showProgress("Loading MineOnline", closeListener);
        ProgressDialog.setMessage("Loading LWJGL");

        LibraryManager.updateNativesPath();

        ProgressDialog.setMessage("Checking for updates");
        ProgressDialog.setProgress(20);

        formopen = true;

        try {
            updateAvailable = !MineOnlineAPI.getLauncherVersion().replaceAll("\\s","").equals(Globals.LAUNCHER_VERSION);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ProgressDialog.setMessage("Loading Minecraft versions");
        ProgressDialog.setProgress(40);

        // Load this before showing the display.
        MinecraftVersionRepository.getSingleton();

        boolean multiinstance = false;
        String quicklaunch = null;
        String joinserver = null;

        // If a user drags a jar onto the launcher, it'll be at arg 1, quicklaunch it.
        if(args.length > 0) {
            File acceptedFile = new File(args[0]);
            if (acceptedFile.exists() && MinecraftVersion.isPlayableJar(acceptedFile.getPath())) {
                quicklaunch = acceptedFile.getPath();
            }
        }

        for(int i = 0; i < args.length ;i++) {
            if(args[i].equals("-multiinstance")) {
                multiinstance = true;
            }
            if(args[i].equals("-joinserver") || args[i].equals("-server")) {
                if(args.length > i + 1) {
                    joinserver = args[i + 1];
                }
            }
            if(args[i].equals("-quicklaunch")) {
                if(args.length > i + 1 && new File(args[i + 1]).exists())  {
                    quicklaunch = args[i + 1];
                } else {
                    if (MinecraftVersionRepository.getSingleton().getLastSelectedJarPath() != null) {
                        quicklaunch = MinecraftVersionRepository.getSingleton().getLastSelectedJarPath();
                    }
                }

            }
        }

        if(quicklaunch != null) {
            MinecraftVersion version = MinecraftVersionRepository.getSingleton().getVersion(quicklaunch);

            if (version != null && version.type.equals("launcher")) {
                MinecraftVersion.launchMinecraft(quicklaunch, null, null, null);
                return;
            }
        }

        ProgressDialog.setMessage("Logging in");
        ProgressDialog.setProgress(60);

        LastLogin lastLogin = null;

        if(!multiinstance)
            lastLogin = LastLogin.readLastLogin();

        String username = null;
        String sessionToken = null;
        String uuid = null;

        if(lastLogin != null ) {
            try {
                JSONObject login = Globals.USE_MOJANG_API
                        ? AuthServer.authenticate(lastLogin.username, lastLogin.password)
                        : MineOnlineAPI.authenticate(lastLogin.username, lastLogin.password);

                if (login.has("error"))
                    throw new Exception(login.getString("error"));
                if (!login.has("accessToken") || !login.has("selectedProfile"))
                    throw new Exception("Failed to authenticate!");
                if (MojangAPI.minecraftProfile(login.getJSONObject("selectedProfile").getString("name")).optBoolean("demo", false))
                    throw new Exception("Please buy Minecraft to use MineOnline.");

                sessionToken = login.getString("accessToken");
                uuid = login.getJSONObject("selectedProfile").getString("id");
                username = login.getJSONObject("selectedProfile").getString("name");
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JSONException ex) {
                // ignore
            }
        }

        ProgressDialog.setMessage("Preparing MineOnline");
        ProgressDialog.setProgress(80);

        DisplayManager.init();

        ProgressDialog.setMessage("Loading done.");
        ProgressDialog.setProgress(100);

        DisplayManager.createDisplay();

        DisplayManager.getFrame().addWindowListener(closeListener);

        Keyboard.enableRepeatEvents(true);

        Renderer renderer = new Renderer();
        Loader loader = new Loader();
        TextMaster.init(loader);

        if (sessionToken != null && username != null) {
            new Session(username, sessionToken, uuid, true);
            LastLogin.writeLastLogin(lastLogin.username, Globals.USE_MOJANG_API ? null : lastLogin.password, uuid);
        }

        if (Session.session != null && Session.session.isOnline() && joinserver != null) {
            String ip;
            String port;

            try {
                String[] split = joinserver.split(":");
                ip = split[0];
                port = split[1];
                MineOnlineServer mineOnlineServer = MineOnlineAPI.getServer(ip, port);

                MinecraftVersion serverVersion = MinecraftVersionRepository.getSingleton().getVersionByMD5(mineOnlineServer.md5);

                Set<String> minecraftJars = MinecraftVersionRepository.getSingleton().getInstalledJars().keySet();

                if (serverVersion != null) {
                    for (String compatibleClientMd5 : serverVersion.clientVersions) {
                        for (String path : minecraftJars) {
                            MinecraftVersion clientVersion = MinecraftVersionRepository.getSingleton().getInstalledJars().get(path);

                            if (clientVersion != null && clientVersion.baseVersion.equals(compatibleClientMd5)) {
                                try {
                                    new Options(LauncherFiles.MINECRAFT_OPTIONS_PATH).setOption("lastServer", mineOnlineServer.ip + "_" + mineOnlineServer.port);
                                } catch (Exception ex) {
                                    // ignore
                                }

                                String mppass = null;
                                if(serverVersion != null && serverVersion.hasHeartbeat) {
                                    mppass = MineOnlineAPI.getMpPass(Session.session.getSessionToken(), Session.session.getUsername(), Session.session.getUuid(), mineOnlineServer.ip, "" + mineOnlineServer.port);
                                }

                                MinecraftVersion.launchMinecraft(path, mineOnlineServer.ip, "" + mineOnlineServer.port, mppass);
                                return;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Failed to join game.");
            }
        }
        else if (Session.session != null && Session.session.isOnline() && quicklaunch != null) {
            String ip = null;
            String port = null;
            String mppass = null;

            if(joinserver != null) {
                String[] ipAndPort = joinserver.split(":");
                if(ipAndPort.length == 2) {
                    ip = ipAndPort[0];
                    port = ipAndPort[1];
                } else if(ipAndPort.length == 1) {
                    ip = ipAndPort[0];
                    port = "25565";
                }
                mppass = MineOnlineAPI.getMpPass(Session.session.getSessionToken(), Session.session.getUsername(), Session.session.getUuid(), ip, port);
            }
            MinecraftVersion.launchMinecraft(quicklaunch, ip, port, mppass);
            return;
        }
        else if (joinserver != null) {
            try {
                new Options(LauncherFiles.MINECRAFT_OPTIONS_PATH).setOption("lastServer", joinserver.replace(":", "_"));
            } catch (Exception ex) {

            }
        }

        GameObject playerPivot = new GameObject("player_origin", new Vector3f(), new Vector3f(0, 30, 0), new Vector3f(1, 1, 1));
        PlayerGameObject playerGameObject = new PlayerGameObject("player", loader, new Vector3f(0, -21, 0), new Vector3f(), new Vector3f(1, 1, 1));
        playerPivot.addChild(playerGameObject);

        playerScale = new GameObject("player scale", new Vector3f(-20, 0, -65), new Vector3f(), new Vector3f(1, 1, 1));
        playerScale.addChild(playerPivot);

        playerGameObject.setPlayerAnimation(new IdlePlayerAnimation());
        Camera camera = new Camera();

        String[] panoramaNames = new String[] {"beta5", "beta6", "sunset", "midnight"};
        //String[] panoramaNames = new String[] {"beta2"};

        RawModel model = loader.loadBoxToVAO(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1), TextureHelper.getCubeTextureCoords(new Vector2f(8192, 4096),
                new Vector2f(4161, 0), new Vector2f(1387, 1387),
                new Vector2f(1387, 0), new Vector2f(1387, 1387),
                new Vector2f(2774, 0), new Vector2f(1387, 1387),
                new Vector2f(0, 0), new Vector2f(1387, 1387),
                new Vector2f(0, 1387), new Vector2f(1387, 1387),
                new Vector2f(1387, 1387), new Vector2f(1387, 1387)
        ));
        ModelTexture modelTexture = new ModelTexture(loader.loadTexture(MenuManager.class.getResource("/img/panorama_" + panoramaNames[new Random().nextInt(panoramaNames.length)] + ".png")));
        TexturedModel texturedModel =  new TexturedModel(model, modelTexture);
        GameObject backgroundImage = new GUIObject("Background", texturedModel, new Vector3f(), new Vector3f(0, 180, 0), new Vector3f(75f, 75f, 75f));

        if (Globals.DEV) {
            new GUIText("MineOnline Dev " + Globals.LAUNCHER_VERSION, 1.5f, TextMaster.minecraftFont, new Vector2f(2, 2), DisplayManager.getDefaultWidth(), false, true);
        }

        if(Session.session != null && Session.session.isOnline())
            if(joinserver != null)
                setMenuScreen(new JoinServerScreen(joinserver));
            else
                setMenuScreen(new MainMenuScreen());
        else
            setMenuScreen(new LoginMenuScreen(false));

        int lastWidth = Display.getWidth();
        int lastHeight = Display.getHeight();
        // Game Loop
        while(!Display.isCloseRequested() && formopen) {
            MouseHandler.update();
            renderer.prepare();

            if(Display.getWidth() != lastWidth || Display.getHeight() != lastHeight) {
                menuScreen.resize();
            }

            lastWidth = Display.getWidth();
            lastHeight = Display.getHeight();

            backgroundImage.increaseRotation(new Vector3f(0, 0.025f, 0));

            // Handle player resizing.
            float maxScale = 1;
            if(DisplayManager.isTall()){
                maxScale = Display.getWidth() / (float)DisplayManager.getDefaultWidth();
            } else {
                maxScale = Display.getHeight() / (float)DisplayManager.getDefaultHeight();
            }
            if(maxScale > 1) {
                playerScale.setScale(new Vector3f((1 / maxScale) * (float)DisplayManager.getScale(), (1 / maxScale) * (float)DisplayManager.getScale(), (1 / maxScale) * (float)DisplayManager.getScale()));
            }
            float xScale = Display.getWidth() / (float)DisplayManager.getDefaultWidth();
            if(xScale > 1) {
                playerScale.setLocalPosition(new Vector3f(-20 * (1 / xScale) * (float)DisplayManager.getScale(), 0, -65));
            }

            if(Mouse.isButtonDown(0)) {
                Vector3f currentRotation = playerPivot.getLocalRotation();
                Vector3f rotation = new Vector3f();

                rotation.y = (Mouse.getDX() * 0.5f);

                if (menuScreen.showPlayer())
                    playerPivot.increaseRotation(rotation);
            }

            playerGameObject.update();
            menuScreen.update();

            if(!formopen) return;

            StaticShader.singleton.start();
            StaticShader.singleton.loadViewMatrix(camera);

            renderer.render(backgroundImage, StaticShader.singleton);

            if (menuScreen.showPlayer()) {
                renderer.render(playerGameObject, StaticShader.singleton);
            }

            StaticShader.singleton.stop();

            menuScreen.render(renderer);

            TextMaster.render();

            DisplayManager.updateDisplay();
        }

        StaticShader.singleton.cleanUp();
        loader.cleanUp();
        TextMaster.cleanUp();
        DisplayManager.closeDisplay();

        DisplayManager.getFrame().removeWindowListener(closeListener);
    }

}
