package gg.codie.mineonline.client;

import gg.codie.common.utils.OSUtils;
import gg.codie.common.utils.TransferableImage;
import gg.codie.mineonline.*;
import gg.codie.mineonline.discord.DiscordRPCHandler;
import gg.codie.mineonline.gui.GUIScale;
import gg.codie.mineonline.gui.input.MouseHandler;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Font;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.screens.AbstractGuiScreen;
import gg.codie.mineonline.gui.screens.GuiDebugMenu;
import gg.codie.mineonline.gui.screens.GuiIngameMenu;
import gg.codie.mineonline.lwjgl.OnCreateListener;
import gg.codie.mineonline.lwjgl.OnDestroyListener;
import gg.codie.mineonline.lwjgl.OnUpdateListener;
import gg.codie.mineonline.patches.lwjgl.LWJGLDisplayPatch;
import gg.codie.mineonline.patches.lwjgl.LWJGLGL11GLOrthoAdvice;
import gg.codie.mineonline.patches.lwjgl.LWJGLGLUPatch;
import gg.codie.mineonline.patches.minecraft.FOVViewmodelAdvice;
import gg.codie.mineonline.patches.minecraft.InputPatch;
import gg.codie.mineonline.patches.minecraft.RubyDungConstructorAdvice;
import gg.codie.mineonline.patches.minecraft.RubyDungPatch;
import gg.codie.mineonline.protocol.MineOnlineURLStreamHandlerFactory;
import gg.codie.mineonline.utils.JREUtils;
import gg.codie.mineonline.utils.Logging;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

public class RubyDungLauncher implements IMinecraftAppletWrapper {

    private final String jarPath;
    private final MinecraftVersion minecraftVersion;

    boolean f1WasDown = false;
    boolean f2wasDown = false;
    boolean f11WasDown = false;
    boolean zoomWasDown = false;
    boolean menuWasDown = false;
    boolean firstUpdate = false;
    Class rubyDungClass;

    int startWidth;
    int startHeight;

    AbstractGuiScreen ingameMenu = new GuiIngameMenu();

    public static void startProcess(String jarPath) {
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
            launchArgs.add(LibraryManager.getClasspath(true, new String[] {
                    new File(RubyDungLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(),
                    jarPath,
                    LauncherFiles.DISCORD_RPC_JAR
            }));
            launchArgs.add(RubyDungLauncher.class.getCanonicalName());
            launchArgs.add(jarPath);
            launchArgs.add("" + Settings.singleton.getGameWidth());
            launchArgs.add("" + Settings.singleton.getGameHeight());

            ProcessBuilder processBuilder = new ProcessBuilder(launchArgs.toArray(new String[0]));

            Map<String, String> env = processBuilder.environment();
            for (String prop : props.stringPropertyNames()) {
                env.put(prop, props.getProperty(prop));
            }
            processBuilder.directory(new File(System.getProperty("user.dir")));
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

            if (DisplayManager.getFrame() != null)
                DisplayManager.getFrame().setVisible(false);

            processBuilder.inheritIO().start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        Logging.enableLogging();
        DiscordRPCHandler.initialize();
        new RubyDungLauncher(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2])).startRubyDung();
    }

    public RubyDungLauncher(String jarPath, int width, int height) {
        this.jarPath = jarPath;
        minecraftVersion = MinecraftVersionRepository.getSingleton().getVersion(jarPath);
        Settings.singleton.saveMinecraftOptions(minecraftVersion.optionsVersion);
        this.startWidth = width;
        this.startHeight = height;

        boolean premium = System.getProperty("mineonline.token") != null;
        new Session(System.getProperty("mineonline.username"), System.getProperty("mineonline.token"), System.getProperty("mineonline.uuid"), premium);
    }

    public void startRubyDung() throws Exception {
        System.setProperty("apple.awt.application.name", "MineOnline");

        URLClassLoader classLoader = new URLClassLoader(new URL[] { Paths.get(jarPath).toUri().toURL() });

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
                closeApplet();
            }
        });

//        DisplayManager.getCanvas().setVisible(false);

        try {
            LinkedList<String> args = new LinkedList<>();

            LibraryManager.updateNativesPath();

            LWJGLDisplayPatch.hijackLWJGLThreadPatch(minecraftVersion != null && minecraftVersion.useGreyScreenPatch);

            LegacyGameManager.createGameManager(minecraftVersion, this);

            if(minecraftVersion != null)
                DiscordRPCHandler.play(minecraftVersion.name, null, null);
            else
                DiscordRPCHandler.play(Paths.get(jarPath).getFileName().toString(), null, null);

            try {
                rubyDungClass = classLoader.loadClass("com.mojang.rubydung.RubyDung");
            } catch (ClassNotFoundException ex) {
                rubyDungClass = classLoader.loadClass("com.mojang.minecraft.RubyDung");
            }

            RubyDungPatch.getRubyDungInstance(rubyDungClass.getName());

            Method main = rubyDungClass.getMethod("main", String[].class);

            Field widthField = rubyDungClass.getDeclaredField("width");
            Field heightField = rubyDungClass.getDeclaredField("height");

            widthField.setAccessible(true);
            heightField.setAccessible(true);

            DisplayManager.getFrame().addComponentListener(new ComponentListener() {
                @Override
                public void componentResized(ComponentEvent e) {
                    try {
                        int w = DisplayManager.getFrame().getWidth() - (DisplayManager.getFrame().getInsets().left + DisplayManager.getFrame().getInsets().right);
                        int h = DisplayManager.getFrame().getHeight() - (DisplayManager.getFrame().getInsets().top + DisplayManager.getFrame().getInsets().bottom);
                        widthField.set(RubyDungConstructorAdvice.rubyDung, w);
                        heightField.set(RubyDungConstructorAdvice.rubyDung, h);
                    } catch (Exception ex) {

                    }
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


            LWJGLDisplayPatch.createListener = new OnCreateListener() {
                @Override
                public void onCreateEvent() {
                    DisplayManager.checkGLError("minecraft create hook start");
                    // The game doesn't scale so until that's fixed, there's no point in doing this.
                    // Display.setResizable(true);
                    new Loader();
                    InputPatch.isFocused = true;
                    try {
                        Display.setDisplayMode(new DisplayMode(startWidth, startHeight));
                    } catch (Exception ex) {

                    }
                    DisplayManager.checkGLError("minecraft create hook end");
                }
            };

            LWJGLDisplayPatch.destroyListener = new OnDestroyListener() {
                @Override
                public void onDestroyEvent() throws Throwable {
                    closeApplet();
                }
            };

            LWJGLDisplayPatch.updateListener = new OnUpdateListener() {
                @Override
                public void onUpdateEvent() {
                    MouseHandler.update();

                    if (!Display.isActive() && LegacyGameManager.mineonlineMenuOpen()) {
                        LegacyGameManager.setGUIScreen(null);
                    }

//                    if (firstUpdate) {
//                        try {
//                            if (fullscreen) {
//                                if (minecraftVersion != null && minecraftVersion.enableFullscreenPatch) {
//                                    setFullscreen(true);
//                                }
//                            }
//                        } catch (Exception ex) {
//
//                        }
//                    }

                    GL11.glViewport(0, 0, DisplayManager.getCanvas().getWidth(), DisplayManager.getCanvas().getHeight());

                    if (Loader.singleton != null) {
                        if (!Globals.BRANCH.equalsIgnoreCase("main")) {
                            int ypos = 2;
                            if (minecraftVersion.ingameVersionString != null && !Settings.singleton.getHideVersionString())
                                ypos = 12;
                            Font.minecraftFont.drawStringWithShadow("MineOnline " + (Globals.DEV ? "Dev " : "") + Globals.LAUNCHER_VERSION + " (" + Globals.BRANCH + ")", 2, ypos, 0xffffff);
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
                        int i1 = j - (Mouse.getY() * j) / Display.getHeight() - 1;

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
                            if (Keyboard.getEventKey() == Keyboard.KEY_F1 && !Keyboard.isRepeatEvent() && !Keyboard.getEventKeyState()) {
                                if (!zoomWasDown) {
                                    LWJGLGL11GLOrthoAdvice.hideHud = false;
                                    FOVViewmodelAdvice.hideViewModel = false;
                                }
                                f1WasDown = false;
                            }


                            if (Keyboard.getEventKey() == Keyboard.KEY_F2 && !Keyboard.isRepeatEvent() && Keyboard.getEventKeyState() && !f2wasDown) {
                                screenshot();
                                f2wasDown = true;
                            }
                            if (Keyboard.getEventKey() == Keyboard.KEY_F2 && !Keyboard.isRepeatEvent() && !Keyboard.getEventKeyState()) {
                                f2wasDown = false;
                            }

                            if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE && !Keyboard.isRepeatEvent() && Keyboard.getEventKeyState()) {
                                closeApplet();
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
                                    if (LegacyGameManager.getGuiScreen() == null) {
                                        LegacyGameManager.setGUIScreen(ingameMenu);
                                    } else if (LegacyGameManager.getGuiScreen() != null) {
                                        LegacyGameManager.setGUIScreen(null);
                                    }

                                    menuWasDown = true;
                                } else if (Keyboard.getEventKey() == Settings.singleton.getMineonlineMenuKeyCode() && !Keyboard.isRepeatEvent() && !Keyboard.getEventKeyState()) {
                                    menuWasDown = false;
                                }
                            }

                            if (Keyboard.getEventKey() == Keyboard.KEY_F6 && Globals.DEV && !Keyboard.isRepeatEvent() && Keyboard.getEventKeyState() && LegacyGameManager.getGuiScreen() == null && Mouse.isGrabbed()) {
                                LegacyGameManager.setGUIScreen(new GuiDebugMenu());
                            }
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

            URL.setURLStreamHandlerFactory(new MineOnlineURLStreamHandlerFactory());

            try {
                Display.setParent(DisplayManager.getCanvas());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            main.invoke(null, new Object[] {args.toArray(new String[0])});

            //closeApplet();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            ex.getTargetException().printStackTrace();

            System.exit(1);
        } catch (Throwable e) {
            e.printStackTrace();

            System.exit(1);
        }
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
                    int j2 = OSUtils.isM1System()
                            ? 0xff000000 | i2 << 16 | l1 << 8 | k1
                            : 0xff000000 | k1 << 16 | l1 << 8 | i2;
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
            Display.getParent().setPreferredSize(new Dimension(widthBeforeFullscreen, heightBeforeFullscreen));
            Display.getParent().resize(new Dimension(widthBeforeFullscreen , heightBeforeFullscreen));
            DisplayManager.getFrame().setSize(widthBeforeFullscreen + DisplayManager.getFrame().getInsets().left + DisplayManager.getFrame().getInsets().right, heightBeforeFullscreen + DisplayManager.getFrame().getInsets().top + DisplayManager.getFrame().getInsets().bottom);
        } else {
            Display.getParent().setPreferredSize(new Dimension(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight()));
            Display.getParent().resize(new Dimension(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight()));
            DisplayManager.getFrame().setPreferredSize(new Dimension(Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight()));
        }

        DisplayManager.getFrame().pack();
    }

    @Override
    public int getWidth() {
        int width = Display.getWidth();

        if (Display.isFullscreen()) {
            width = Display.getDisplayMode().getWidth();
        }

        return width;
    }

    @Override
    public int getHeight() {
        int height = Display.getHeight();

        if (Display.isFullscreen() ) {
            height = Display.getDisplayMode().getHeight();
        }

        return height;
    }

    @Override
    public void closeApplet() {
        Runtime.getRuntime().halt(0);
    }

    @Override
    public Class getMinecraftAppletClass() {
        return rubyDungClass;
    }
}
