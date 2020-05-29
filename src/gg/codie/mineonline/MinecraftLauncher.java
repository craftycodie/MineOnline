package gg.codie.mineonline;

import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import gg.codie.mineonline.lwjgl.OnCreateListener;
import gg.codie.mineonline.lwjgl.OnUpdateListener;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import javax.imageio.ImageIO;
import java.applet.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MinecraftLauncher extends Applet implements AppletStub{

    private static final boolean DEBUG = true;

    Applet minecraftApplet;

    String jarPath;
    String serverAddress;
    String serverPort;
    String MPPass;

    Renderer renderer;

    MinecraftVersionInfo.MinecraftVersion minecraftVersion;

    public static void main(String[] args) throws Exception{
        LibraryManager.updateClasspath();
        LibraryManager.updateNativesPath();

        DisplayManager.init();

        PlayerRendererTest.main(null);

        DisplayManager.closeDisplay();
        new Session("codie", "1213");

        new MinecraftLauncher("D:\\Projects\\Local\\MinecraftBetaOfflineLauncher\\Binaries\\Old Game\\Old Files\\bin\\minecraft.jar", null, null, null).startMinecraft();
    }

    public MinecraftLauncher(String jarPath, String serverAddress, String serverPort, String MPPass) {
        this.jarPath = jarPath;
        this.serverAddress = serverAddress;
        this.serverAddress = serverAddress;
        this.MPPass = MPPass;

        minecraftVersion = MinecraftVersionInfo.getVersion(jarPath);

        try {
            LibraryManager.addJarToClasspath(Paths.get(jarPath).toUri().toURL());
        } catch (Exception e) {
            System.err.println("Couldn't load jar file " + jarPath);
            e.printStackTrace();
            System.exit(1);
        }
    }

    void startMinecraft() throws Exception {
        String appletClassName = MinecraftVersionInfo.getAppletClass(jarPath);

        Frame frame = DisplayManager.getFrame();

        Display.setCreateListener(new OnCreateListener() {
            @Override
            public void onCreateEvent() {
                renderer = new Renderer(new StaticShader());
            }
        });

        Class appletClass;

        try{
            appletClass = Class.forName(appletClassName);
        } catch (Exception ex){
            ex.printStackTrace();
            return;
        }

        Field minecraftField = null;

        try {
            minecraftField = appletClass.getDeclaredField("minecraft");
        } catch (NoSuchFieldException ne) {
            for(Field field : appletClass.getDeclaredFields()) {
                if(field.getType().getPackage() == appletClass.getPackage()) {
                    minecraftField = field;
                    continue;
                }
            }
        }

        Class minecraftClass = null;

        if(minecraftField != null)
            minecraftClass = minecraftField.getType();

        Runnable minecraftImpl = null;

        if(minecraftClass != null) {
            try {
                File jarFile = new File(jarPath);

                if (!jarFile.exists() || jarFile.isDirectory())
                    return;

                java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile.getPath());
                java.util.Enumeration enumEntries = jar.entries();
                while (enumEntries.hasMoreElements()) {
                    java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
                    if (!file.getName().endsWith(".class")) {
                        continue;
                    }

                    Constructor constructor = null;

                    // Ideally, we'd check if the class extends Minecraft
                    // But due to obfuscation we have to settle for this.
                    try {
                        Class clazz = Class.forName(file.getName().replace(".class", ""));
                        //java.awt.Component,java.awt.Canvas,net.minecraft.client.minecraftApplet,int,int,boolean,java.awt.Frame
                        constructor = clazz.getConstructor(
                                Component.class, Canvas.class, appletClass, int.class, int.class, boolean.class, Frame.class
                        );

                    } catch (Exception e) {
                    }

                    if (constructor != null) {
                        System.out.println("found MinecraftImpl");
                        minecraftImpl = (Runnable) constructor.newInstance(null, DisplayManager.getCanvas(), null, DisplayManager.getWidth(), DisplayManager.getHeight(), false, frame);
                    }

                    if (minecraftImpl != null) {

                        for (Field field : minecraftClass.getDeclaredFields()) {
                            try {
                                constructor = field.getType().getConstructor(
                                        String.class, String.class
                                );
                                if (constructor == null) {
                                    continue;
                                }
                                field.set(minecraftImpl, constructor.newInstance(Session.session.getUsername(), Session.session.getSessionToken()));
                                break;
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }

                }
                jar.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if(minecraftImpl == null) {
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
            Display.setResizable(true);
//
//            DisplayManager.getCanvas().setVisible(false);

            //This puts the applet into a window so that it can be shown on the screen.
            frame.add(minecraftApplet);

            DisplayManager.getCanvas().setVisible(false);
        }

        frame.pack();
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                CloseApplet();
            }
        });
        frame.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                appletResize(frame.getWidth(), frame.getHeight());
            }

            @Override
            public void componentMoved(ComponentEvent e) {}

            @Override
            public void componentShown(ComponentEvent e) {}

            @Override
            public void componentHidden(ComponentEvent e) {}
        });
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//        frame.setResizable(true);

        frame.setBackground(Color.black);

        //And this runs the applet.
        if(minecraftApplet != null)
            minecraftApplet.init();

        Display.setUpdateListener(new OnUpdateListener() {
            @Override
            public void onUpdateEvent() {
                if(renderer != null) {
                    if(DEBUG) {
                        renderer.renderString(new Vector2f(2, 10), 8, "MineOnline Pre-Release", org.newdawn.slick.Color.white); //x, y, string to draw, color
                    }
                    if (minecraftVersion != null && minecraftVersion.enableScreenshotPatch) {
                        try {
                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

                            float opacityMultiplier = System.currentTimeMillis() - lastScreenshotTime;
                            if (opacityMultiplier > 5000) {
                                opacityMultiplier -= 5000;
                                opacityMultiplier = -(opacityMultiplier / 500);
                                opacityMultiplier += 1;
                            } else {
                                opacityMultiplier = 1;
                            }

                            if (opacityMultiplier != 0)
                                renderer.renderString(new Vector2f(2, 190), 8, "Saved screenshot as " + lastScreenshotName, new org.newdawn.slick.Color(1, 1, 1, 1 * opacityMultiplier)); //x, y, string to draw, color
                        } catch (Exception e) {
                            e.printStackTrace();
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
            }
        });



        if(DEBUG && minecraftVersion != null) {
            frame.setTitle("Minecraft " + minecraftVersion.name + " Debug");
        } else {
            frame.setTitle("Minecraft");
        }

        if (minecraftImpl != null)
            minecraftImpl.run();
        else
            minecraftApplet.start();
    }

    boolean f2wasDown = false;

    void CloseApplet(){
        if(minecraftApplet != null) {
            minecraftApplet.stop();
            minecraftApplet.destroy();
        }
        try {
            DisplayManager.closeDisplay();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        DisplayManager.getFrame().dispose();
        System.exit(0);
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
        the minecraftApplet holds an instance of the Minecraft class, and we have the minecraftApplet,
        so we can search for it there.

        Searching for it involves:
        1. Look for unka field called "minecraft". If it's there use it.
        2. If "minecraft" is not found, find any field within the same package.
           - In every build I've checked, minecraftApplet only has 1 instance variable from the same package,
             and it's Minecraft.

        Then we find the width and height values.
        These are (seemingly) always the first two public integers in the Minecraft class.
        These are obfuscated so cannot be found by name.

        If any of these searches fail, resizing should just do nothing.
    */
    public void appletResize(int width,int height){
        try {
            Field minecraftField = null;

            //panel.setSize(new Dimension(width, height));
            //minecraftApplet.setSize(new Dimension(width, height));

            try {
                minecraftField = minecraftApplet.getClass().getDeclaredField("minecraft");
            } catch (NoSuchFieldException ne) {
                for(Field field : minecraftApplet.getClass().getDeclaredFields()) {
                    if(field.getType().getPackage() == minecraftApplet.getClass().getPackage()) {
                        minecraftField = field;
                        continue;
                    }
                }
            }

            Class<?> minecraftClass = minecraftField.getType();

            Field widthField = null;
            Field heightField = null;

            // Since Minecraft is obfuscated we can't just get the width and height fields by name.
            // Hopefully, they're always the first two ints. Seems likely.
            for(Field field : minecraftClass.getDeclaredFields()) {
                if(int.class.equals(field.getType()) || Integer.class.equals(field.getType()) && Modifier.isPublic(field.getModifiers())) {
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
            widthField.setInt(minecraft, width);
            heightField.setInt(minecraft, height - DisplayManager.getFrame().getInsets().top - DisplayManager.getFrame().getInsets().bottom);

            //screenshotLabel.setBounds(30, (AppletH - 16) - 30, 204, 20);
        } catch (Exception e) {

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
            File screenshotsFolder = new File(LauncherFiles.MINECRAFT_SCREENSHOTS_PATH);
            screenshotsFolder.mkdirs();

            File file;
            String s = (new StringBuilder()).append(new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date())).toString();
            for(int k = 1; (file = new File(screenshotsFolder, (new StringBuilder()).append(s).append(k != 1 ? (new StringBuilder()).append("_").append(k).toString() : "").append(".png").toString())).exists(); k++) { }

            if(buffer == null || buffer.capacity() < Display.getWidth() * Display.getHeight())
            {
                buffer = BufferUtils.createByteBuffer(Display.getWidth() * Display.getHeight() * 3);
            }
            if(imageData == null || imageData.length < Display.getWidth() * Display.getHeight() * 3)
            {
                pixelData = new byte[Display.getWidth() * Display.getHeight() * 3];
                imageData = new int[Display.getWidth() * Display.getHeight()];
            }
            GL11.glPixelStorei(3333 /*GL_PACK_ALIGNMENT*/, 1);
            GL11.glPixelStorei(3317 /*GL_UNPACK_ALIGNMENT*/, 1);
            buffer.clear();
            GL11.glReadPixels(0, 0, Display.getWidth(), Display.getHeight(), 6407 /*GL_RGB*/, 5121 /*GL_UNSIGNED_BYTE*/, buffer);
            buffer.clear();

            buffer.get(pixelData);
            for(int l = 0; l < Display.getWidth(); l++)
            {
                for(int i1 = 0; i1 < Display.getHeight(); i1++)
                {
                    int j1 = l + (Display.getHeight() - i1 - 1) * Display.getWidth();
                    int k1 = pixelData[j1 * 3 + 0] & 0xff;
                    int l1 = pixelData[j1 * 3 + 1] & 0xff;
                    int i2 = pixelData[j1 * 3 + 2] & 0xff;
                    int j2 = 0xff000000 | k1 << 16 | l1 << 8 | i2;
                    imageData[l + i1 * Display.getWidth()] = j2;
                }

            }

            BufferedImage bufferedimage = new BufferedImage(Display.getWidth(), Display.getHeight(), 1);
            bufferedimage.setRGB(0, 0, Display.getWidth(), Display.getHeight(), imageData, 0, Display.getWidth());

            try {
                ImageIO.write(bufferedimage, "png", file);
                System.out.println("Screenshot saved to " + file.getPath());
                lastScreenshotTime = System.currentTimeMillis();
                lastScreenshotName = file.getName();
            } catch (IOException e) { e.printStackTrace(); }
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
    public String getParameter(String name){
        String RetVal=null;
        switch(name){
            case "stand-alone":
                RetVal = "true";
                break;
            case "username":
                RetVal = Session.session.getUsername();
                break;
            case "sessionid":
                int n;
                if (!Session.session.isOnline()){
                    break;
                }
                RetVal = Session.session.getSessionToken();
                break;
            case "haspaid":
                RetVal = String.valueOf(Properties.properties.getBoolean("isPremium"));
                break;
            case "demo":
                RetVal = String.valueOf(!Properties.properties.getBoolean("isPremium"));
                break;
            case "server":
                RetVal = serverAddress;
                break;
            case "port":
                RetVal = serverPort;
                break;
            case "mppass":
                RetVal = MPPass;
                break;
            default:
                //don't do anything
        }
        if (RetVal==null){
            System.out.println(name + " =	" + "");
        }else{
            System.out.println(name + " =	" + RetVal);
        }
        return RetVal;
    }
}
