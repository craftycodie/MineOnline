package gg.codie.mineonline.client;

import gg.codie.mineonline.*;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.lwjgl.OnCreateListener;
import gg.codie.mineonline.lwjgl.OnUpdateListener;
import gg.codie.mineonline.patches.ClassPatch;
import gg.codie.mineonline.patches.StringPatch;
import gg.codie.mineonline.patches.URLPatch;
import gg.codie.mineonline.patches.lwjgl.LWJGLDisplayPatch;
import gg.codie.mineonline.patches.lwjgl.LWJGLMouseSetNativeCursorAdvice;
import gg.codie.mineonline.patches.lwjgl.LWJGLGLUPatch;
import gg.codie.mineonline.patches.minecraft.MousePatch;
import gg.codie.mineonline.utils.JREUtils;
import gg.codie.mineonline.utils.Logging;
import gg.codie.utils.OSUtils;
import gg.codie.utils.TransferableImage;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

public class RubyDungLauncher {

    private final String jarPath;
    private final MinecraftVersion minecraftVersion;

    boolean f2wasDown = false;
    Renderer renderer;

    public static void startProcess(String jarPath) {
        try {

            java.util.Properties props = System.getProperties();

            LinkedList<String> launchArgs = new LinkedList();
            launchArgs.add(JREUtils.getJavaExecutable());
            launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
            launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");
            launchArgs.add("-Djava.net.preferIPv4Stack=true");
            launchArgs.add("-Dmineonline.username=" + Session.session.getUsername());
            launchArgs.add("-Dmineonline.token=" + Session.session.getAccessToken());
            launchArgs.add("-Dmineonline.uuid=" + Session.session.getUuid());
            if (Settings.singleton.getClientLaunchArgs().isEmpty())
                launchArgs.addAll(Arrays.asList(Settings.singleton.getClientLaunchArgs().split(" ")));
            launchArgs.add("-cp");
            launchArgs.add(LibraryManager.getClasspath(true, new String[] {
                    new File(RubyDungLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(),
                    jarPath
            }));
            launchArgs.add(RubyDungLauncher.class.getCanonicalName());
            launchArgs.add(jarPath);

            ProcessBuilder processBuilder = new ProcessBuilder(launchArgs.toArray(new String[0]));

            Map<String, String> env = processBuilder.environment();
            for (String prop : props.stringPropertyNames()) {
                env.put(prop, props.getProperty(prop));
            }
            processBuilder.directory(new File(System.getProperty("user.dir")));
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

            DisplayManager.getFrame().setVisible(false);

            processBuilder.inheritIO().start();

            Runtime.getRuntime().halt(0);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Logging.enableLogging();

        new RubyDungLauncher(args[0]).startRubyDung();
    }

    public RubyDungLauncher(String jarPath) {
        this.jarPath = jarPath;
        minecraftVersion = MinecraftVersionRepository.getSingleton(true).getVersion(jarPath);
        Settings.singleton.saveMinecraftOptions(minecraftVersion.optionsVersion);
    }

    public void startRubyDung() throws Exception {
        URLClassLoader classLoader = new URLClassLoader(new URL[] { Paths.get(jarPath).toUri().toURL() });

        try {
            LinkedList<String> args = new LinkedList<>();

            LibraryManager.updateNativesPath();

            LWJGLDisplayPatch.hijackLWJGLThreadPatch(minecraftVersion != null && minecraftVersion.useGreyScreenPatch);

            if (minecraftVersion != null && minecraftVersion.enableCursorPatch && OSUtils.isMac())
                MousePatch.fixMouseIssues();

            Class rubyDungClass;
            try {
                rubyDungClass = classLoader.loadClass("com.mojang.rubydung.RubyDung");
            } catch (ClassNotFoundException ex) {
                rubyDungClass = classLoader.loadClass("com.mojang.minecraft.RubyDung");
            }

            Method main = rubyDungClass.getMethod("main", String[].class);


            LWJGLDisplayPatch.createListener = new OnCreateListener() {
                @Override
                public void onCreateEvent() {
                    DisplayManager.checkGLError("minecraft create hook start");
                    renderer = new Renderer();
                    // The game doesn't scale so until that's fixed, there's no point in doing this.
                    // Display.setResizable(true);
                    DisplayManager.checkGLError("minecraft create hook end");
                }
            };

            LWJGLDisplayPatch.updateListener = new OnUpdateListener() {
                @Override
                public void onUpdateEvent() {
                    DisplayManager.checkGLError("minecraft update hook start");

                    if (!OSUtils.isWindows() && minecraftVersion != null && minecraftVersion.enableCursorPatch) {
                        if (Mouse.isGrabbed() != LWJGLMouseSetNativeCursorAdvice.isFocused)
                            Mouse.setGrabbed(LWJGLMouseSetNativeCursorAdvice.isFocused);
                    }

                    // DEBUG: Frees the cursor when pressing tab.
//                    if (Keyboard.getEventKey() == Keyboard.KEY_TAB) {
//                        Mouse.setGrabbed(false);
//                    }

                    if (renderer != null) {
                        if (Globals.DEV) {
                            //renderer.renderStringIngame(new Vector2f(1, 1), 8, "MineOnline Dev " + Globals.LAUNCHER_VERSION, org.newdawn.slick.Color.white);
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
                                        renderer.renderStringIngame(new Vector2f(2, 190), 8, "Saved screenshot as " + lastScreenshotName, new org.newdawn.slick.Color(1, 1, 1, 1 * opacityMultiplier));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (Keyboard.getEventKey() == Keyboard.KEY_F2 && !Keyboard.isRepeatEvent() && Keyboard.getEventKeyState() && !f2wasDown) {
                                screenshot();
                                f2wasDown = true;
                            }
                            if (Keyboard.getEventKey() == Keyboard.KEY_F2 && !Keyboard.isRepeatEvent() && !Keyboard.getEventKeyState()) {
                                f2wasDown = false;
                            }
                        }
                    }

                    DisplayManager.checkGLError("minecraft update hook end");
                }
            };




            URLPatch.redefineURL();
            // Allow texture packs in versions before Alpha 1.2.2
            if (minecraftVersion != null && minecraftVersion.useTexturepackPatch)
                ClassPatch.useTexturePacks(Settings.singleton.getTexturePack());
            if (minecraftVersion != null && minecraftVersion.useFOVPatch)
                LWJGLGLUPatch.useCustomFOV();

            // Hide version strings from the HUD
            if (minecraftVersion != null && minecraftVersion.ingameVersionString != null && Settings.singleton.getHideVersionString())
                StringPatch.hideVersionStrings(minecraftVersion.ingameVersionString);

            main.invoke(null, new Object[] {args.toArray(new String[0])});

            //System.exit(0);
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
            int width = Display.getWidth();
            int height = Display.getHeight();

            if(buffer == null || buffer.capacity() < width * height)
            {
                buffer = BufferUtils.createByteBuffer(width * height * 3);
            }
            if(imageData == null || imageData.length < width * height * 3)
            {
                pixelData = new byte[width * height * 3];
                imageData = new int[width * height];
            }
            GL11.glPixelStorei(3333 /*GL_PACK_ALIGNMENT*/, 1);
            GL11.glPixelStorei(3317 /*GL_UNPACK_ALIGNMENT*/, 1);
            buffer.clear();
            GL11.glReadPixels(0, 0, width, height, 6407 /*GL_RGB*/, 5121 /*GL_UNSIGNED_BYTE*/, buffer);


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
}
