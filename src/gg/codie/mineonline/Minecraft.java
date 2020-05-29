package gg.codie.mineonline;

import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.PlayerRendererTest;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Minecraft extends Applet implements AppletStub {

    Applet MinecraftApplet;
    Frame AppletFrame = new Frame("Minecraft (Applet)");

    //These variables are for storing the values of command line parameters, and setting default values when needed.
    String AppletClassName;
    int AppletW=854;
    int AppletH=480;
    String UserName;
    String SessionID;
    String ServerAddr;
    String ServerPort;
    String MPPass;
    boolean HasPaid=false;
    String md5;

    String jarPath = "D:\\Projects\\Local\\MinecraftBetaOfflineLauncher\\Binaries\\Old Game\\Old Files\\bin\\minecraft.jar";

    public static void main(String[] args) throws Exception {
        LibraryManager.updateClasspath();
        LibraryManager.updateNativesPath();
//        DisplayManager.createDisplay();
//        new Minecraft().runApplet(args);

        new Minecraft(args);
    }

    Minecraft(String[] args) throws Exception {
        ParseCommandLine(args);

        startMainThread("Codie", "test", "mc.codie.gg");
    }

    public void startMainThread(String username, String sessionID, String server) throws Exception
    {
        Field minecraftField = null;

//        this.AppletW = width;
//        this.AppletH = height;

        //panel.setSize(new Dimension(width, height));
//        minecraftApplet.setSize(new Dimension(width, height));

        Class AppletClass = Class.forName(AppletClassName);

        try {
            minecraftField = AppletClass.getDeclaredField("minecraft");
        } catch (NoSuchFieldException ne) {
            for(Field field : AppletClass.getDeclaredFields()) {
                if(field.getType().getPackage() == AppletClass.getPackage()) {
                    minecraftField = field;
                    continue;
                }
            }
        }

        Class minecraftClass = minecraftField.getType();

        Runnable minecraft = null;
//
//        try {
//            minecraft = (Runnable)minecraftClass.getConstructor(
//                    Canvas.class, AppletClass, int.class, int.class, boolean.class
//            ).newInstance(null, null, 0, 0, false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // CONSTRUCT MINECRAFT

        // Before Desktop
        // public Minecraft(Canvas var1, minecraftApplet var2, int var3, int var4, boolean var5) {

        // After Desktop
        // public MinecraftImpl(Component component, Canvas canvas, minecraftApplet minecraftapplet, int i, int j, boolean flag, Frame frame)

        boolean flag = false;

        DisplayManager.init();

        PlayerRendererTest.main(null);
        DisplayManager.closeDisplay();

        try {
            // Before the frame argument was added, applets wouldn't make a new frame, so these can be used.
            Constructor constructor = minecraftClass.getConstructor(
                    Canvas.class, AppletClass, int.class, int.class, boolean.class
            );

            MinecraftApplet.setStub(this);
            MinecraftApplet.setPreferredSize(new Dimension(Display.getWidth(), Display.getHeight()));
            MinecraftApplet.init();
            MinecraftApplet.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File jarFile = new File(jarPath);

            if(!jarFile.exists() || jarFile.isDirectory())
                return;

            java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile.getPath());
            java.util.Enumeration enumEntries = jar.entries();
            while (enumEntries.hasMoreElements()) {
                java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
                if(!file.getName().endsWith(".class")) {
                    continue;
                }

                Constructor constructor = null;

                // Ideally, we'd check if the class extends Minecraft
                // But due to obfuscation we have to settle for this.
                try {
                    Class clazz = Class.forName(file.getName().replace(".class", ""));
                    //java.awt.Component,java.awt.Canvas,net.minecraft.client.minecraftApplet,int,int,boolean,java.awt.Frame
                    constructor = clazz.getConstructor(
                            Component.class, Canvas.class, AppletClass, int.class, int.class, boolean.class, Frame.class
                    );

                } catch (Exception e) { }

                if(constructor != null) {
                    System.out.println("found MinecraftImpl");
                    minecraft = (Runnable) constructor.newInstance(null, DisplayManager.getCanvas(), null, 854, 480, false, DisplayManager.getFrame());
                    break;
                }

                if(minecraft != null) {

                    for (Field field : minecraftClass.getDeclaredFields()) {
                        try {
                            constructor = field.getType().getConstructor(
                                    String.class, String.class
                            );
                            if (constructor == null) {
                                continue;
                            }
                            System.out.println("Found session.");
                            field.set(minecraft, constructor.newInstance(username, sessionID));
                            break;
                        } catch (Exception e) {
                            continue;
                        }
                    }
                }

            }
            jar.close();

            if(minecraft == null) {
                try {
                    minecraft = (Runnable)minecraftClass.getConstructor(
                            Canvas.class, AppletClass, int.class, int.class, boolean.class
                    ).newInstance(DisplayManager.getCanvas(), null, 854, 480, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DisplayManager.getFrame().setVisible(true);
        //frame.addWindowListener(new GameWindowListener(minecraftimpl, thread));
        //thread.start();

        minecraft.run();
    }


    boolean f2wasDown = false;
    public Runnable runnableScreenshot = new Runnable() {
        public void run() {
            if(Keyboard.getEventKey() == Keyboard.KEY_F2 && !Keyboard.isRepeatEvent() && Keyboard.getEventKeyState() && !f2wasDown) {
                screenshot();
                f2wasDown = true;
            }
            if(Keyboard.getEventKey() == Keyboard.KEY_F2 && !Keyboard.isRepeatEvent() && !Keyboard.getEventKeyState()) {
                f2wasDown = false;
            }
            EventQueue.invokeLater(this);
        }
    };

    //This method handles the task of putting command line args into variables, or displaying the about box if there are no command line args.
    void ParseCommandLine(String[] args){
        int n=1;
        AppletClassName = args[0];
        while (n<args.length){
            System.out.println(args[n]);
            if (args[n].equalsIgnoreCase("-w")){
                //Set only the width of the applet, if you like the default height but want a different width.
                AppletW=Integer.parseInt(args[n+1]);
                n+=2;
            } else if (args[n].equalsIgnoreCase("-h")){
                //Set only the height of the applet, if you like the default width but want a different height.
                AppletH=Integer.parseInt(args[n+1]);
                n+=2;
            } else if (args[n].equalsIgnoreCase("-size")){
                //Set both the width and height of the applet.
                AppletW=Integer.parseInt(args[n+1]);
                AppletH=Integer.parseInt(args[n+2]);
                n+=3;
            } else if (args[n].equalsIgnoreCase("-login")){
                //Set both UserName and SessionID.
                UserName=args[n+1];
                n+=2;
                if (n<args.length && !args[n].startsWith("-")) {
                    SessionID = args[n];
                    n += 1;
                }
            } else if (args[n].equalsIgnoreCase("-server")){
                //Set the address and port of the Minecraft server to connect to, as well as the multiplayer password.
                ServerAddr=args[n+1];
                n+=2;
                if (n<args.length && !args[n].startsWith("-")) {
                    ServerPort = args[n];
                    n += 1;
                }
                if (n<args.length && !args[n].startsWith("-")) {
                    MPPass = args[n];
                    n += 1;
                }

            } else if (args[n].equalsIgnoreCase("-paid")){
                //This command line switch takes no additional parameters. The HasPaid variable is set to true if this switch is present.
                //For some Classic versions of Minecraft, HasPaid must be true in order to enable the server-save slot buttons.
                //Server-side saving saves to a minecraft.net URL, not to your local Classic server.
                //However that URL is no longer valid so these save slots no longer work. This feature is only included in this launcher for the sake of completeness.
                //Also, in addition to setting the "haspaid" applet parameter to true, it sets the "demo" applet parameter to false, for versions that support demo mode.
                HasPaid=true;
                n++;
            } else if (args[n].equalsIgnoreCase("-md5")){
                //Set only the height of the applet, if you like the default width but want a different height.
                md5=args[n+1];
                n+=2;
            } else {
                //Skip all other command line switches.
                n++;
            }
        }
    }

    void CloseApplet(){
        MinecraftApplet.stop();
        MinecraftApplet.destroy();
        AppletFrame.dispose();
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
        1. Look for unka field called "minecrafr". If it's there use it.
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

            this.AppletW = width;
            this.AppletH = height;

            //panel.setSize(new Dimension(width, height));
            MinecraftApplet.setSize(new Dimension(width, height));

            try {
                minecraftField = MinecraftApplet.getClass().getDeclaredField("minecraft");
            } catch (NoSuchFieldException ne) {
                for(Field field : MinecraftApplet.getClass().getDeclaredFields()) {
                    if(field.getType().getPackage() == MinecraftApplet.getClass().getPackage()) {
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

            Object minecraft = minecraftField.get(MinecraftApplet);
            widthField.setInt(minecraft, width);
            heightField.setInt(minecraft, height);

            //screenshotLabel.setBounds(30, (AppletH - 16) - 30, 204, 20);
        } catch (Exception e) {

        }
    }

    public void screenshot() {
        try {
//            int w = AppletFrame.getWidth();
//            int h = AppletFrame.getHeight();
//            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//            Graphics2D g = bi.createGraphics();
//            minecraftApplet.paint(g);

            File screenshotsFolder = new File(LauncherFiles.MINECRAFT_SCREENSHOTS_PATH);
            screenshotsFolder.mkdirs();

            File file;
            String s = (new StringBuilder()).append(new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date())).toString();
            for(int k = 1; (file = new File(screenshotsFolder, (new StringBuilder()).append(s).append(k != 1 ? (new StringBuilder()).append("_").append(k).toString() : "").append(".png").toString())).exists(); k++) { }

//            //ImageIO.write(bi, "png", image);
//
//            ByteBuffer buffer = BufferUtils.createByteBuffer(AppletW * AppletH * 4);
//            GL11.glReadPixels(0, 0, AppletW, AppletH, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );
//
//            BufferedImage image = new BufferedImage(AppletW, AppletH, BufferedImage.TYPE_INT_RGB);
//
//            for(int x = 0; x < AppletW; x++)
//            {
//                for(int y = 0; y < AppletH; y++)
//                {
//                    int i = (x + (AppletW * y)) * 4;
//                    int r = buffer.get(i) & 0xFF;
//                    int g = buffer.get(i + 1) & 0xFF;
//                    int b = buffer.get(i + 2) & 0xFF;
//                    image.setRGB(x, AppletH - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
//                }
//            }

            Robot robot = new Robot();
            Rectangle captureRect = new Rectangle(AppletFrame.getX() + AppletFrame.getInsets().left, AppletFrame.getY() + AppletFrame.getInsets().top, (AppletW - AppletFrame.getInsets().left) - AppletFrame.getInsets().right, (AppletH - AppletFrame.getInsets().top) - AppletFrame.getInsets().bottom);
            BufferedImage screenFullImage = robot.createScreenCapture(captureRect);

            try {
                ImageIO.write(screenFullImage, "png", file);
                System.out.println("Screenshot saved to " + file.getPath());
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

        if(md5 != null) {
            MinecraftVersionInfo.MinecraftVersion version = MinecraftVersionInfo.getVersionByMD5(md5);
            if(version != null && version.baseURLHasNoPort) {
                baseURL = baseURL.replace(":80", "");
            }
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
            case "username":
                if (UserName==null){
                    break;
                }
                if (UserName.equals("[[RANDOM]]")){
                    UserName = "Player" + String.valueOf((new Random()).nextInt(1000));
                }
                RetVal = UserName;
                break;
            case "sessionid":
                int n;
                if (SessionID==null){
                    break;
                }
                if (SessionID.equals("[[RANDOM]]")){
                    String ByteHex;
                    SessionID="";
                    for (n=0;n<16;n++){
                        ByteHex=Integer.toHexString((new Random()).nextInt(256));
                        if (ByteHex.length()==1){
                            ByteHex = "0" + ByteHex;
                        }
                        SessionID += ByteHex;
                    }
                }
                RetVal = SessionID;
                break;
            case "haspaid":
                RetVal = String.valueOf(HasPaid);
                break;
            case "demo":
                RetVal = String.valueOf(!HasPaid);
                break;
            case "server":
                RetVal = ServerAddr;
                break;
            case "port":
                RetVal = ServerPort;
                break;
            case "mppass":
                RetVal = MPPass;
                break;
            case "md5":
                RetVal = md5;
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
