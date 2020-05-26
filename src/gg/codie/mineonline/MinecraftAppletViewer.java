package gg.codie.mineonline;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.applet.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MinecraftAppletViewer extends Applet implements AppletStub{

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

    public static void main(String[] args) throws Exception{
        LibraryManager.updateClasspath();

        new MinecraftAppletViewer().runApplet(args);
    }

    void runApplet(String[] args){
        Class AppletClass;
        ParseCommandLine(args);
        if (args.length>0){
            try{
                AppletClass = Class.forName(AppletClassName);
            } catch (Exception ex){
                ex.printStackTrace();
                return;
            }
            try{
                MinecraftApplet = (Applet)AppletClass.newInstance();
            } catch (Exception ex){
                ex.printStackTrace();
                return;
            }

            //Here we set the applet's stub to a custom stub, rather than letting it use the default.
            //And also we now set the width and height of the applet on the screen.
            MinecraftApplet.setStub(this);
            MinecraftApplet.setPreferredSize(new Dimension(AppletW,AppletH));
            Display.setResizable(true);


            //This puts the applet into a window so that it can be shown on the screen.
            AppletFrame.add(MinecraftApplet);
            AppletFrame.pack();
            AppletFrame.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    CloseApplet();
                }
            });
            AppletFrame.addComponentListener(new ComponentListener() {
                @Override
                public void componentResized(ComponentEvent e) {
                    appletResize(AppletFrame.getWidth(), AppletFrame.getHeight());
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
            AppletFrame.setLocationRelativeTo(null);
            AppletFrame.setVisible(true);
            AppletFrame.setResizable(true);

            //And this runs the applet.
            MinecraftApplet.init();
            MinecraftApplet.start();
        }
    }

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
        the MinecraftApplet holds an instance of the Minecraft class, and we have the MinecraftApplet,
        so we can search for it there.

        Searching for it involves:
        1. Look for a field called "minecrafr". If it's there use it.
        2. If "minecraft" is not found, find any field within the same package.
           - In every build I've checked, MinecraftApplet only has 1 instance variable from the same package,
             and it's Minecraft.

        Then we find the width and height values.
        These are (seemingly) always the first two public integers in the Minecraft class.
        These are obfuscated so cannot be found by name.

        If any of these searches fail, resizing should just do nothing.
    */
    public void appletResize(int width,int height){
        try {
            Field minecraftField = null;

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
        } catch (Exception e) {

        }
    }

    //Tells the applet that it is active.
    public boolean isActive(){
        return true;
    }

    //This sets the document base URL, which would normally be the URL of the webpage in which the applet was embedded.
    public URL getDocumentBase(){
        return StringToURL("http://" + Properties.properties.getString("baseUrl"));
    }

    //This sets the code base URL, which would normally be defined by the codebase attribute of the <applet> tag.
    public URL getCodeBase(){
        return StringToURL("http://" + Properties.properties.getString("baseUrl"));
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
