package gg.codie.mineonline.client;

import gg.codie.common.utils.TransferableImage;
import gg.codie.minecraft.client.options.EMinecraftOptionsVersion;
import gg.codie.mineonline.*;
import gg.codie.mineonline.discord.DiscordRPCHandler;
import gg.codie.mineonline.gui.GUIScale;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.input.MouseHandler;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Font;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.screens.AbstractGuiScreen;
import gg.codie.mineonline.gui.screens.GuiDebugMenu;
import gg.codie.mineonline.gui.screens.GuiIngameMenu;
import gg.codie.mineonline.gui.PlayerList;
import gg.codie.mineonline.lwjgl.OnCreateListener;
import gg.codie.mineonline.lwjgl.OnDestroyListener;
import gg.codie.mineonline.lwjgl.OnUpdateListener;
import gg.codie.mineonline.patches.*;
import gg.codie.mineonline.patches.lwjgl.LWJGLDisplayPatch;
import gg.codie.mineonline.patches.lwjgl.LWJGLGL11GLOrthoAdvice;
import gg.codie.mineonline.patches.lwjgl.LWJGLGLUPatch;
import gg.codie.mineonline.patches.minecraft.ColorizerPatch;
import gg.codie.mineonline.patches.minecraft.FOVViewmodelAdvice;
import gg.codie.mineonline.patches.minecraft.InputPatch;
import gg.codie.mineonline.protocol.MineOnlineURLStreamHandlerFactory;
import gg.codie.mineonline.sound.SoundExtractionService;
import gg.codie.mineonline.utils.JREUtils;
import gg.codie.mineonline.utils.Logging;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

public class LegacyMinecraftClientLauncher extends Applet implements AppletStub, IMinecraftAppletWrapper {
    Applet minecraftApplet;

    String jarPath;
    String serverAddress;
    String serverPort;
    String MPPass;

    MinecraftVersion minecraftVersion;

    int startWidth;
    int startHeight;

    AbstractGuiScreen ingameMenu = new GuiIngameMenu();
    PlayerList playerList = null;


    public static void startProcess(String jarPath, String serverIP, String serverPort, String mpPass) {
        // Launch normal jars.
        try {
            java.util.Properties props = System.getProperties();

            LinkedList<String> launchArgs = new LinkedList();
            launchArgs.add(JREUtils.getJavaExecutable());
            launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
            launchArgs.add("-Djava.net.preferIPv4Stack=true");
            launchArgs.add("-Dmineonline.username=" + Session.session.getUsername());
            launchArgs.add("-Dmineonline.token=" + Session.session.getAccessToken());
            launchArgs.add("-Dmineonline.uuid=" + Session.session.getUuid());
            if (!Settings.singleton.getClientLaunchArgs().isEmpty())
                launchArgs.addAll(Arrays.asList(Settings.singleton.getClientLaunchArgs().split(" ")));
            launchArgs.add("-cp");
            launchArgs.add(LibraryManager.getClasspath(true, false, new String[] {
                    new File(LegacyMinecraftClientLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(),
                    jarPath,
                    LauncherFiles.DISCORD_RPC_JAR
            }));
            launchArgs.add(LegacyMinecraftClientLauncher.class.getCanonicalName());
            launchArgs.add(jarPath);
            launchArgs.add("" + Settings.singleton.getGameWidth());
            launchArgs.add("" + Settings.singleton.getGameHeight());
            if (serverIP != null) {
                launchArgs.add(serverIP);
                if (serverPort != null)
                    launchArgs.add(serverPort);
                else launchArgs.add("25565");

                if (mpPass != null)
                    launchArgs.add(mpPass);
            }

            ProcessBuilder processBuilder = new ProcessBuilder(launchArgs.toArray(new String[launchArgs.size()]));

            Map<String, String> env = processBuilder.environment();
            for (String prop : props.stringPropertyNames()) {
                env.put(prop, props.getProperty(prop));
            }
            processBuilder.directory(new File(System.getProperty("user.dir")));

            if (DisplayManager.getFrame() != null)
                DisplayManager.getFrame().setVisible(false);

            Process gameProcess = processBuilder.inheritIO().start();

            // for unix debugging, capture IO.
            if (Globals.DEV) {
                int exitCode = 1;
                try {
                    exitCode = gameProcess.waitFor();
                    Runtime.getRuntime().halt(exitCode);
                } catch (Exception ex) {
                    // ignore.
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // [ jarPath, width, height, ip, port, mppass ]
    public static void main(String[] args) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
//        System.setProperty("org.lwjgl.util.NoChecks", "true");
        if (Globals.DEV)
            System.setProperty("org.lwjgl.util.Debug", "true");

        Logging.enableLogging();
        DiscordRPCHandler.initialize();

        String serverAddress = args.length > 3 ? args[3] : null;
        String serverPort = args.length > 4 ? args[4] : null;
        String mpPass = args.length > 5 ? args[5] : null;

        try {
            new LegacyMinecraftClientLauncher(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), serverAddress, serverPort, mpPass).startMinecraft();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to launch Minecraft.");
        }
    }


    public LegacyMinecraftClientLauncher(String jarPath, int width, int height, String serverAddress, String serverPort, String MPPass) {
        this.jarPath = jarPath;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.MPPass = MPPass;
        this.startWidth = widthBeforeFullscreen = width;
        this.startHeight = heightBeforeFullscreen = height;

        boolean premium = System.getProperty("mineonline.token") != null;
        new Session(System.getProperty("mineonline.username"), System.getProperty("mineonline.token"), System.getProperty("mineonline.uuid"), premium);

        if(serverAddress != null && serverPort == null)
            this.serverPort = "25565";

        minecraftVersion = MinecraftVersionRepository.getSingleton(jarPath).getVersion(jarPath);
        Settings.singleton.saveMinecraftOptions(minecraftVersion != null ? minecraftVersion.optionsVersion : EMinecraftOptionsVersion.DEFAULT);

        if (minecraftVersion != null)
            try {
                new SoundExtractionService().downloadSoundpack(minecraftVersion.resourcesVersion);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }

    boolean firstUpdate = true;
    public void startMinecraft() throws Exception {
        System.setProperty("apple.awt.application.name", "MineOnline");

        LibraryManager.updateNativesPath();

        LWJGLDisplayPatch.hijackLWJGLThreadPatch(minecraftVersion != null && minecraftVersion.useGreyScreenPatch);

        LegacyGameManager.createGameManager(minecraftVersion, this);

        if(minecraftVersion != null)
            DiscordRPCHandler.play(minecraftVersion.name, serverAddress, serverPort);
        else
            DiscordRPCHandler.play(Paths.get(jarPath).getFileName().toString(), serverAddress, serverPort);

        DisplayManager.init();
        DisplayManager.getCanvas().setPreferredSize(new Dimension(startWidth, startHeight));
        DisplayManager.getFrame().setPreferredSize(new Dimension(startWidth + DisplayManager.getFrame().getInsets().left + DisplayManager.getFrame().getInsets().right, startHeight + DisplayManager.getFrame().getInsets().top + DisplayManager.getFrame().getInsets().bottom));
        DisplayManager.getCanvas().setSize(startWidth, startHeight);
        DisplayManager.getFrame().setSize(startWidth + DisplayManager.getFrame().getInsets().left + DisplayManager.getFrame().getInsets().right, startHeight + DisplayManager.getFrame().getInsets().top + DisplayManager.getFrame().getInsets().bottom);
        DisplayManager.getFrame().pack();
        DisplayManager.getFrame().setVisible(true);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - DisplayManager.getFrame().getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - DisplayManager.getFrame().getHeight()) / 2);
        DisplayManager.getFrame().setLocation(x, y);

        DisplayManager.getFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LegacyGameManager.closeGame();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                LegacyGameManager.closeGame();
            }
        });

        String appletClassName = MinecraftVersion.getAppletClass(jarPath);

        Frame frame = DisplayManager.getFrame();

        LWJGLDisplayPatch.createListener = new OnCreateListener() {
            @Override
            public void onCreateEvent() {
                DisplayManager.checkGLError("minecraft create hook start");
                new Loader();
                Font.reloadFont();
                DisplayManager.checkGLError("minecraft create hook end");
            }
        };

        LWJGLDisplayPatch.destroyListener = new OnDestroyListener() {
            @Override
            public void onDestroyEvent() throws Throwable {
                System.exit(0);
            }
        };

        Class appletClass;

        try {
            appletClass = Class.forName(appletClassName);
        } catch (Exception ex) {
            ex.printStackTrace();
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, "This version is currently unsupported.");
                }
            });
            return;
        }

        try {
            minecraftApplet = (Applet) appletClass.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        //Here we set the applet's stub to a custom stub, rather than letting it use the default.
        //And also we now set the width and height of the applet on the screen.
        minecraftApplet.setStub(this);
        minecraftApplet.setPreferredSize(new Dimension(Display.getWidth(), Display.getHeight()));

        //This puts the applet into a window so that it can be shown on the screen.
        frame.add(minecraftApplet);
        frame.pack();

        DisplayManager.getCanvas().setVisible(false);

        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = DisplayManager.getFrame().getWidth() - (DisplayManager.getFrame().getInsets().left + DisplayManager.getFrame().getInsets().right);
                int h = DisplayManager.getFrame().getHeight() - (DisplayManager.getFrame().getInsets().top + DisplayManager.getFrame().getInsets().bottom);
                appletResize(w, h);
                //minecraftApplet.resize(w, h);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });

        frame.setBackground(Color.black);

        MenuManager.formOpen = false;

        LWJGLDisplayPatch.updateListener = new OnUpdateListener() {
            @Override
            public void onUpdateEvent() {
                MouseHandler.update();

                if (!Display.isActive() && LegacyGameManager.mineonlineMenuOpen()) {
                    LegacyGameManager.setGUIScreen(null);
                }

                LegacyGameManager.update();

                if (firstUpdate) {
                    try {
                        if (fullscreen) {
                            if (minecraftVersion != null && minecraftVersion.enableFullscreenPatch) {
                                setFullscreen(true);
                            }
                        }
                    } catch (Exception ex) {

                    }

                    if (minecraftVersion.useTexturepackPatch) {
                        ColorizerPatch.updateColorizers();
                    }
                }

                if (Loader.singleton != null) {
                    if (!Globals.BRANCH.equalsIgnoreCase("main")) {
                        int ypos = 2;
                        if (minecraftVersion.ingameVersionString != null && !Settings.singleton.getHideVersionString())
                            ypos = 12;
                        Font.minecraftFont.drawStringWithShadow("MineOnline " + (Globals.DEV ? "Dev " : "") + Globals.LAUNCHER_VERSION  + " (" + Globals.BRANCH + ")", 2, ypos, 0xffffff);
                    }

                    GUIScale scaledresolution = new GUIScale(getWidth(), getHeight());
//                    GL11.glClear(256);
                    GL11.glMatrixMode(GL11.GL_PROJECTION);
                    GL11.glLoadIdentity();
                    GL11.glOrtho(0.0D, scaledresolution.scaledWidth, scaledresolution.scaledHeight, 0.0D, 1000D, 3000D);
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glLoadIdentity();
                    GL11.glTranslatef(0.0F, 0.0F, -2000F);

                    int i = (int) scaledresolution.getScaledWidth();
                    int j = (int) scaledresolution.getScaledHeight();
                    int k = (Mouse.getX() * i) / getWidth();
                    int i1 = j - (Mouse.getY() * j) / Display.getParent().getHeight() - 1;

                    if (LegacyGameManager.getGuiScreen() != null) {
                        LegacyGameManager.getGuiScreen().updateScreen();
                        LegacyGameManager.getGuiScreen().drawScreen(k, i1);

                        LegacyGameManager.getGuiScreen().handleInput();
                    }

                    LegacyGameManager.renderToast();

                    DisplayManager.checkGLError("minecraft update hook start");

                    if (minecraftVersion != null && minecraftVersion.enableCursorPatch) {
                        if (Mouse.isGrabbed() != InputPatch.isFocused)
                            Mouse.setGrabbed(InputPatch.isFocused);
                    }

                    if (minecraftVersion != null) {
                        // Make sure classic is behaving
                        if (minecraftVersion.guiClass != null)
                            appletResize(Display.getParent().getWidth(), Display.getParent().getHeight());

                        if (minecraftVersion.enableScreenshotPatch) {
                            try {
                                float opacityMultiplier = System.currentTimeMillis() - lastScreenshotTime;
                                if (opacityMultiplier > 5000) {
                                    opacityMultiplier -= 5000;
                                    opacityMultiplier = -(opacityMultiplier / 500);
                                    opacityMultiplier += 1;
                                } else {
                                    opacityMultiplier = 1;
                                }

                                if (opacityMultiplier > 0) {
                                    Font.minecraftFont.drawStringWithShadow("Saved screenshot as " + lastScreenshotName, 2, GUIScale.lastScaledHeight() - 100, 0xffffff + ((int) (0xff * opacityMultiplier) << 24));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (Keyboard.getEventKey() == Keyboard.KEY_F1 && !Keyboard.isRepeatEvent() && Keyboard.getEventKeyState() && !f1WasDown) {
                            if (minecraftVersion.enableScreenshotPatch) {
                                LWJGLGL11GLOrthoAdvice.hideHud = true;
                                FOVViewmodelAdvice.hideViewModel = true;
                            }

                            f1WasDown = true;
                        }
                    }

                    if (Keyboard.getEventKey() == Keyboard.KEY_F1 && !Keyboard.isRepeatEvent() && !Keyboard.getEventKeyState()) {
                        if (!zoomWasDown) {
                            LWJGLGL11GLOrthoAdvice.hideHud = false;
                            FOVViewmodelAdvice.hideViewModel = false;
                        }
                        f1WasDown = false;
                    }

                    if (Settings.singleton.getPlayerListKey() != 0 && Keyboard.isKeyDown(Settings.singleton.getPlayerListKey())) {
                        if (playerList == null)
                            playerList = new PlayerList();

                        if (Settings.singleton.getPlayerListToast()) {
                            Settings.singleton.setPlayerListToast(false);
                            Settings.singleton.saveSettings();
                        }

                        playerList.drawScreen();
                    } else {
                        playerList = null;
                    }

                    if (Settings.singleton.getZoomKeyCode() != 0) {
                        if (Mouse.isGrabbed() && Keyboard.getEventKey() == Settings.singleton.getZoomKeyCode() && !Keyboard.isRepeatEvent() && Keyboard.getEventKeyState() && !zoomWasDown) {
                            LWJGLGLUPatch.zoom();
                            LWJGLGL11GLOrthoAdvice.hideHud = true;
                            FOVViewmodelAdvice.hideViewModel = true;
                            zoomWasDown = true;
                        } else if (Keyboard.getEventKey() == Settings.singleton.getZoomKeyCode() && !Keyboard.isRepeatEvent() && !Keyboard.getEventKeyState()) {
                            LWJGLGLUPatch.unZoom();
                            if (!f1WasDown) {
                                LWJGLGL11GLOrthoAdvice.hideHud = false;
                                FOVViewmodelAdvice.hideViewModel = false;
                            }
                            zoomWasDown = false;
                        }
                    }

                    if (Settings.singleton.getMineonlineMenuKeyCode() != 0) {
                        if (Keyboard.getEventKey() == Settings.singleton.getMineonlineMenuKeyCode() && !Keyboard.isRepeatEvent() && Keyboard.getEventKeyState() && !menuWasDown) {
                            if (LegacyGameManager.getGuiScreen() == null && Mouse.isGrabbed()) {
                                LegacyGameManager.setGUIScreen(ingameMenu);
                            } else if (LegacyGameManager.getGuiScreen() != null) {
//                                LegacyGameManager.setGUIScreen(null);
                            }

                            menuWasDown = true;
                        } else if (Keyboard.getEventKey() == Settings.singleton.getMineonlineMenuKeyCode() && !Keyboard.isRepeatEvent() && !Keyboard.getEventKeyState()) {
                            menuWasDown = false;
                        }
                    }

                    if (Keyboard.getEventKey() == Keyboard.KEY_F6 && Globals.DEV && !Keyboard.isRepeatEvent() && Keyboard.getEventKeyState() && LegacyGameManager.getGuiScreen() == null && Mouse.isGrabbed()) {
                        LegacyGameManager.setGUIScreen(new GuiDebugMenu());
                    }

                    if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE && LegacyGameManager.getGuiScreen() != null)
                        LegacyGameManager.setGUIScreen(null);

                    if (Keyboard.getEventKey() == 1 && !Keyboard.isRepeatEvent() && Keyboard.getEventKeyState()) {
                        if (LegacyGameManager.getGuiScreen() != null) {
                            LegacyGameManager.setGUIScreen(null);
                        }
                    }

                    if (Keyboard.getEventKey() == Keyboard.KEY_F2 && !Keyboard.isRepeatEvent() && Keyboard.getEventKeyState() && !f2wasDown) {
                        screenshot();
                        f2wasDown = true;
                    }
                    if (Keyboard.getEventKey() == Keyboard.KEY_F2 && !Keyboard.isRepeatEvent() && !Keyboard.getEventKeyState()) {
                        f2wasDown = false;
                    }

                    if (minecraftVersion != null && minecraftVersion.enableFullscreenPatch) {
                        if (Keyboard.getEventKey() == Keyboard.KEY_F11 && !Keyboard.isRepeatEvent() && Keyboard.getEventKeyState() && !f11WasDown) {
                            setFullscreen(!fullscreen);
                            f11WasDown = true;
                        }
                        if (Keyboard.getEventKey() == Keyboard.KEY_F11 && !Keyboard.isRepeatEvent() && !Keyboard.getEventKeyState()) {
                            f11WasDown = false;
                        }
                    }

                    if (firstUpdate) {
                        firstUpdate = false;
                    }

                    DisplayManager.checkGLError("minecraft update hook end");
                }
            }
        };

        DisplayManager.getFrame().setTitle("Minecraft");
        if (LegacyGameManager.getVersion() != null)
            DisplayManager.getFrame().setTitle("Minecraft " + (LegacyGameManager.getVersion().ingameVersionString != null
                    ? LegacyGameManager.getVersion().ingameVersionString.replace("Minecraft ", "")
                    : LegacyGameManager.getVersion().name.replace("Release ", "")));

        Settings.singleton.saveMinecraftOptions(minecraftVersion != null ? minecraftVersion.optionsVersion : EMinecraftOptionsVersion.DEFAULT);

        // Patches
        SocketPatch.watchSockets();
        URL.setURLStreamHandlerFactory(new MineOnlineURLStreamHandlerFactory());
        FilePatch.relocateFiles(minecraftVersion != null ? minecraftVersion.resourcesVersion : "default");

        // Allows c0.0.15a to connect to servers.
        InetSocketAddressPatch.allowCustomServers(serverAddress, serverPort);

        minecraftApplet.init();

        DisplayManager.getCanvas().setFocusable(true);

        fullscreen = Settings.singleton.getFullscreen();

        minecraftApplet.start();
    }

    boolean f1WasDown = false;
    boolean f2wasDown = false;
    boolean f11WasDown = false;
    boolean zoomWasDown = false;
    boolean menuWasDown = false;

    @Override
    public int getWidth() {
        int width = Display.getParent().getWidth();

        if (Display.isFullscreen() || fullscreen) {
            width = Display.getDisplayMode().getWidth();
        } else if (minecraftApplet != null) {
            width = minecraftApplet.getWidth();
        }

        return width;
    }

    @Override
    public int getHeight() {
        int height = Display.getParent().getHeight();

        if (Display.isFullscreen() || fullscreen) {
            height = Display.getDisplayMode().getHeight();
        } else if (minecraftApplet != null) {
            height = minecraftApplet.getHeight();
        }

        return height;
    }

    @Override
    public void closeApplet(){
        Runtime.getRuntime().halt(0);
    }

    @Override
    public Class getMinecraftAppletClass() {
        return minecraftApplet.getClass();
    }


    URL StringToURL(String URLString){
        try{
            return new URL(URLString);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }



    // HACKY RESIZING, needs testing.
    /*
        Minecraft applets never had any resizing code, so to implement it I've used a lot of reflection.
        In an ordinary codebase that'd be pretty bad, but with obfuscated code like Minecraft's, it's basically
        impossible to maintain.
        That said, we know what minecraft builds look like, so this doesn't need to be maintained,
        and we can feasibly cover all builds.

        As it stands, this method will first search for the Minecraft class.
        the minecraftApplet holds an singleton of the Minecraft class, and we have the minecraftApplet,
        so we can search for it there.

        Searching for it involves:
        1. Look for unka field called "minecraft". If it's there use it.
        2. If "minecraft" is not found, find any field within the same package.
           - In every build I've checked, minecraftApplet only has 1 singleton variable from the same package,
             and it's Minecraft.

        Then we find the width and height values.
        These are (seemingly) always the first two public integers in the Minecraft class.
        These are obfuscated so cannot be found by name.

        If any of these searches fail, resizing should just do nothing.
    */
    @Override
    public void appletResize(int width, int height){
        try {
            new GUIScale(width, height);

            Field minecraftField = null;

            try {
                minecraftField = minecraftApplet.getClass().getDeclaredField("minecraft");
            } catch (NoSuchFieldException ne) {
                for(Field field : minecraftApplet.getClass().getDeclaredFields()) {
                    if(field.getType().getPackage() == minecraftApplet.getClass().getPackage()) {
                        minecraftField = field;
                        break;
                    }
                }
            }

            // If it's not in the same package as the applet, it might be in the default package.
            if (minecraftField == null) {
                for (Field field : minecraftApplet.getClass().getDeclaredFields()) {
                    if (field.getType().getPackage() == Package.getPackage("")) {
                        minecraftField = field;
                        break;
                    }
                }
            }

            Class<?> minecraftClass = minecraftField.getType();

            Field widthField = null;
            Field heightField = null;

            Field guiWidthField = null;
            Field guiHeightField = null;

            // Since Minecraft is obfuscated we can't just get the width and height fields by name.
            // Hopefully, they're always the first two ints. Seems likely.
            for(Field field : minecraftClass.getDeclaredFields()) {
                if ((int.class.equals(field.getType()) || Integer.class.equals(field.getType()))) {
                    if (widthField == null) {
                        widthField = field;
                    } else if (heightField == null) {
                        heightField = field;
                        break;
                    }
                }
            }

            minecraftField.setAccessible(true);
            widthField.setAccessible(true);
            heightField.setAccessible(true);

            Object minecraft = minecraftField.get(minecraftApplet);

            Field guiField = null;

            if (minecraftVersion != null && minecraftVersion.guiClass != null) {
                try {
                    Class guiClass = Class.forName(minecraftVersion.guiClass);
                    for (Field field : guiClass.getDeclaredFields()) {
                        if ((int.class.equals(field.getType()) || Integer.class.equals(field.getType())) && Modifier.isPrivate(field.getModifiers())) {
                            if (guiWidthField == null) {
                                guiWidthField = field;
                            } else if (guiHeightField == null) {
                                guiHeightField = field;
                                break;
                            }
                        }
                    }

                    guiWidthField.setAccessible(true);
                    guiHeightField.setAccessible(true);

                    for (Field field : minecraftClass.getDeclaredFields()) {
                        if (field.getType().getCanonicalName().equals(minecraftVersion.guiClass)) {
                            guiField = field;
                            guiField.setAccessible(true);
                            break;
                        }
                    }
                } catch (Exception ex) {
                    if (Globals.DEV)
                        System.err.println("Couldn't find GUI class " + minecraftVersion.guiClass);
                }
            }

            Object gui = guiField != null & minecraft != null ? guiField.get(minecraft) : null;

            if (minecraftVersion.useResizePatch) {
                widthField.setInt(minecraft, width);
                heightField.setInt(minecraft, height);
            }

            if(gui != null && guiHeightField != null && guiWidthField != null) {
                guiHeightField.setInt(gui, height * 240 / height);
                guiWidthField.setInt(gui, width * 240 / height);
            }

            if (gui != null && guiHeightField != null && guiWidthField != null && minecraftVersion != null && minecraftVersion.guiScreenClass != null) {
                guiWidthField.setInt(gui, GUIScale.lastScaledWidth());
                guiHeightField.setInt(gui, GUIScale.lastScaledHeight());
            }

            // Resizable versions of Minecraft check the canvas size against the game size, if it changes the game is resized.
            Display.getParent().setSize(width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    boolean fullscreen;

    int widthBeforeFullscreen;
    int heightBeforeFullscreen;

    void setFullscreen(boolean newFullscreen) {
        if(!fullscreen && newFullscreen) {
            widthBeforeFullscreen = Display.getWidth();
            heightBeforeFullscreen = Display.getHeight();
        }
        DisplayManager.fullscreen(newFullscreen);
        fullscreen = newFullscreen;
        if(!fullscreen) {
            minecraftApplet.setPreferredSize(new Dimension(widthBeforeFullscreen, heightBeforeFullscreen));
            minecraftApplet.resize(new Dimension(widthBeforeFullscreen , heightBeforeFullscreen));
            DisplayManager.getFrame().setSize(widthBeforeFullscreen + DisplayManager.getFrame().getInsets().left + DisplayManager.getFrame().getInsets().right, heightBeforeFullscreen + DisplayManager.getFrame().getInsets().top + DisplayManager.getFrame().getInsets().bottom);
        } else {
            minecraftApplet.setPreferredSize(new Dimension(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight()));
            minecraftApplet.resize(new Dimension(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight()));
            DisplayManager.getFrame().setPreferredSize(new Dimension(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight()));
        }

        DisplayManager.getFrame().pack();
        appletResize(Display.getParent().getWidth(), Display.getParent().getHeight());
    }

    private static ByteBuffer buffer;
    private static byte pixelData[];
    private static int imageData[];
    long lastScreenshotTime = 0;
    String lastScreenshotName = "";

    // this MUST be called from the OpenGL thread.
    public void screenshot() {
        try {
            int width = Display.getParent().getWidth();
            int height = Display.getParent().getHeight();

            if(buffer == null || buffer.capacity() != (width * height * 3))
            {
                buffer = BufferUtils.createByteBuffer(width * height * 3);
            }
            if(imageData == null || imageData.length != width * height * 3)
            {
                pixelData = new byte[width * height * 3];
                imageData = new int[width * height];
            }
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            buffer.clear();
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
            
            buffer.clear();

            buffer.get(pixelData);
            for(int l = 0; l < width; l++)
            {
                for(int i1 = 0; i1 < height; i1++)
                {
                    int j1 = l + (height - i1 - 1) * width;
                    int k1 = pixelData[j1 * 3 + 0] & 0xff;
                    int l1 = pixelData[j1 * 3 + 1] & 0xff;
                    int i2 = pixelData[j1 * 3 + 2] & 0xff;
                    int j2 = 0xff000000 | k1 << 16 | l1 << 8 | i2;
                    imageData[l + i1 * width] = j2;
                }

            }

            BufferedImage bufferedimage = new BufferedImage(width, height, 1);
            bufferedimage.setRGB(0, 0, width, height, imageData, 0, width);

            // Copy image to clipboard.
            TransferableImage trans = new TransferableImage( bufferedimage );
            Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
            c.setContents( trans, null );

            if (minecraftVersion.enableScreenshotPatch) {
                File screenshotsFolder = new File(LauncherFiles.MINECRAFT_SCREENSHOTS_PATH);
                screenshotsFolder.mkdirs();

                File file;
                String s = (new StringBuilder()).append(new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date())).toString();
                for(int k = 1; (file = new File(screenshotsFolder, (new StringBuilder()).append(s).append(k != 1 ? (new StringBuilder()).append("_").append(k).toString() : "").append(".png").toString())).exists(); k++) { }

                try {
                    ImageIO.write(bufferedimage, "png", file);
                    System.out.println("Screenshot saved to " + file.getPath());
                    lastScreenshotTime = System.currentTimeMillis();
                    lastScreenshotName = file.getName();
                } catch (IOException e) { e.printStackTrace(); }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Tells the applet that it is active.
    public boolean isActive(){
        return true;
    }

    //This sets the document base URL, which would normally be the URL of the webpage in which the applet was embedded.
    public URL getDocumentBase(){
        String baseURL = "http://www.minecraft.net:80/game/";

        if(minecraftVersion != null && minecraftVersion.baseURLHasNoPort) {
            baseURL = baseURL.replace(":80", "");
        }

        return StringToURL(baseURL);
    }

    //This sets the code base URL, which would normally be defined by the codebase attribute of the <applet> tag.
    public URL getCodeBase(){
        return getDocumentBase();
    }

    //This sets parameters that would normally be set by <param> tags within the applet block defined by <applet> and </applet> tags.
    public String getParameter(String key){
        String value = null;
        switch(key){
            case "stand-alone":
                value = "true";
                break;
            case "username":
                value = Session.session.getUsername();
                break;
            case "sessionid":
                if (!Session.session.isOnline())
                    break;
                value = Session.session.getAccessToken();
                break;
            case "haspaid":
                value = "" + Session.session.isPremium();
                break;
            case "demo":
                if (!Session.session.isPremium())
                    value = "true";
                break;
            case "server":
                value = serverAddress;
                break;
            case "port":
                value = serverPort;
                break;
            case "mppass":
                value = MPPass;
                break;

            // Only used by the NFC mod.
            case "uuid":
                value = Session.session.getUuid();
            default:
                //don't do anything
        }
        if (Globals.DEV) {
            if (value == null) {
                System.out.println(key + " = " + "");
            } else {
                System.out.println(key + " = " + value);
            }
        }
        return value;
    }
}
