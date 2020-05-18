package gg.codie.mineonline;

import java.applet.*;
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


            //This puts the applet into a window so that it can be shown on the screen.
            AppletFrame.add(MinecraftApplet);
            AppletFrame.pack();
            AppletFrame.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    CloseApplet();
                }
            });
            AppletFrame.setLocationRelativeTo(null);
            AppletFrame.setVisible(true);

            //And this runs the applet.
            MinecraftApplet.init();
            MinecraftApplet.start();
        }
    }

    //This method handles the task of putting command line args into variables, or displaying the about box if there are no command line args.
    void ParseCommandLine(String[] args){
        int n=1;
        if (args.length==0){
            Frame AboutBox = new Frame("About");
            AboutBox.setLayout(new GridLayout(0,1));
            AboutBox.add(new Label("Minecraft Applet Launcher",Label.CENTER));
            AboutBox.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    System.exit(0);
                }
            });
            AboutBox.setSize(400,120);
            AboutBox.setResizable(false);
            AboutBox.setLocationRelativeTo(null);
            AboutBox.setVisible(true);
        }else{
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
    }

    void CloseApplet(){
        MinecraftApplet.stop();
        MinecraftApplet.destroy();
        //System.exit(0);
        AppletFrame.dispose();
    }


    URL StringToURL(String URLString){
        try{
            return new URL(URLString);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }



    //Below here are the custom applet stub functions. Using these it is possible to set the applet's parameters.
    //Typically this stuff would be configured in the <applet> and <param> HTML tags.
    //However, since the Minecraft applet is going to be running outside a browser here, we need a custom stub to configure the applet.

    //This would normally perform some action when the applet is resized, but right now it does nothing.
    public void appletResize(int width,int height){
        //do nothing
    }

    //Tells the applet that it is active.
    public boolean isActive(){
        return true;
    }

    //This sets the document base URL, which would normally be the URL of the webpage in which the applet was embedded.
    public URL getDocumentBase(){
        return StringToURL("http://" + Properties.properties.getProperty("baseUrl"));
    }

    //This sets the code base URL, which would normally be defined by the codebase attribute of the <applet> tag.
    public URL getCodeBase(){
        return StringToURL("http://" + Properties.properties.getProperty("baseUrl"));
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
