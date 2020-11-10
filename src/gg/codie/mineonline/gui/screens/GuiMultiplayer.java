package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.Globals;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.api.MineOnlineServerRepository;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.FontRenderer;
import gg.codie.mineonline.utils.JREUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GuiMultiplayer extends GuiScreen
{

    public GuiMultiplayer(GuiScreen guiscreen)
    {
        field_35341_g = -1;
        field_35346_k = false;
        field_35353_s = false;
        field_35352_t = false;
        field_35351_u = false;
        field_35350_v = null;
        parentScreen = guiscreen;

        serverRepository.loadServers();

        initGui();
    }

    public void updateScreen()
    {
    }

    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        controlList.clear();
        guiSlotServer = new GuiSlotServer(this);
        func_35337_c();
    }

    public void func_35337_c()
    {
//        controlList.add(field_35347_h = new GuiButton(7, width / 2 - 154, height - 28, 70, 20, "Edit"));
//        controlList.add(field_35345_j = new GuiButton(2, width / 2 - 74, height - 28, 70, 20, "Delete"));
        controlList.add(connectButton = new GuiButton(1, getWidth() / 2 - 154, getHeight() - 52, 100, 20, "Join Server"));
        controlList.add(new GuiButton(4, getWidth() / 2 - 50, getHeight() - 52, 100, 20, "Direct Connect"));
        controlList.add(new GuiButton(3, getWidth() / 2 + 4 + 50, getHeight() - 52, 100, 20, "Cancel"));
//        controlList.add(new GuiButton(8, width / 2 + 4, height - 28, 70, 20, stringtranslate.translateKey("selectServer.refreshList")));
//        controlList.add(new GuiButton(0, width / 2 + 4 + 76, height - 28, 75, 20, stringtranslate.translateKey("gui.cancel")));
        boolean flag = field_35341_g >= 0 && field_35341_g < guiSlotServer.getSize();
        connectButton.enabled = flag;
        //field_35347_h.enabled = flag;
        //field_35345_j.enabled = flag;
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if(!guibutton.enabled)
        {
            return;
        }
//        if(guibutton.id == 2)
//        {
//            String s = ((ServerNBTStorage)serverSlots.get(field_35341_g)).field_35795_a;
//            if(s != null)
//            {
//                field_35346_k = true;
//                StringTranslate stringtranslate = StringTranslate.getInstance();
//                String s1 = stringtranslate.translateKey("selectServer.deleteQuestion");
//                String s2 = (new StringBuilder()).append("'").append(s).append("' ").append(stringtranslate.translateKey("selectServer.deleteWarning")).toString();
//                String s3 = stringtranslate.translateKey("selectServer.deleteButton");
//                String s4 = stringtranslate.translateKey("gui.cancel");
//                GuiYesNo guiyesno = new GuiYesNo(this, s1, s2, s3, s4, field_35341_g);
//                mc.displayGuiScreen(guiyesno);
//            }
//        } else
        if(guibutton.id == 1)
        {
            func_35322_a(field_35341_g);
        } else
        if(guibutton.id == 4)
        {
            //field_35351_u = true;
            MenuManager.setGUIScreen(new GuiDirectConnect(this));
        } else
        if(guibutton.id == 3)
        {
            //field_35353_s = true;
            MenuManager.setGUIScreen(parentScreen);
            if(parentScreen == null) {
                Mouse.setGrabbed(true);
            }
            //mc.displayGuiScreen(new GuiScreenAddServer(this, field_35349_w = new ServerNBTStorage(StatCollector.translateToLocal("selectServer.defaultName"), "")));
        } //else
//        if(guibutton.id == 7)
//        {
//            field_35352_t = true;
//            ServerNBTStorage servernbtstorage = (ServerNBTStorage)serverSlots.get(field_35341_g);
//            mc.displayGuiScreen(new GuiScreenAddServer(this, field_35349_w = new ServerNBTStorage(servernbtstorage.field_35795_a, servernbtstorage.field_35793_b)));
//        } else
//        if(guibutton.id == 0)
//        {
//            mc.displayGuiScreen(parentScreen);
//        } else
//        if(guibutton.id == 8)
//        {
//            mc.displayGuiScreen(new GuiMultiplayer(parentScreen));
//        } else
//        {
//            guiSlotServer.actionPerformed(guibutton);
//        }
    }

    protected void keyTyped(char c, int i)
    {
        if(c == '\r')
        {
            actionPerformed((GuiButton)controlList.get(2));
        }
    }

    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
    }

    public void drawScreen(int i, int j)
    {
        field_35350_v = null;
        drawDefaultBackground();
        guiSlotServer.drawScreen(i, j);
        drawCenteredString("Play Multiplayer", getWidth() / 2, 20, 0xffffff);
        super.drawScreen(i, j);
        if(field_35350_v != null)
        {
            func_35325_a(field_35350_v, i, j);
        }
    }

    private void func_35322_a(int i)
    {
        connect_probably(serverRepository.getServers().get(i));
    }

    private void connect_probably(MineOnlineServer server)
    {
        try {
            LinkedList<String> launchArgs = new LinkedList();
            launchArgs.add(JREUtils.getRunningJavaExecutable());
            launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
            launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");
            launchArgs.add("-cp");
            launchArgs.add(LibraryManager.getClasspath(true, new String[]{new File(MenuManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(), LauncherFiles.DISCORD_RPC_JAR}));
            launchArgs.add(MenuManager.class.getCanonicalName());
            launchArgs.add("-quicklaunch");
            launchArgs.add("-joinserver");
            launchArgs.add(server.ip + ":" + server.port);
            launchArgs.add("-skipupdates");

            java.util.Properties props = System.getProperties();
            ProcessBuilder processBuilder = new ProcessBuilder(launchArgs);

            Map<String, String> env = processBuilder.environment();
            for (String prop : props.stringPropertyNames()) {
                env.put(prop, props.getProperty(prop));
            }
            processBuilder.directory(new File(System.getProperty("user.dir")));

            Process launcherProcess = processBuilder.inheritIO().start();

            Thread.currentThread().interrupt();
            Display.destroy();
            DisplayManager.getFrame().dispose();
            System.exit(0);

//            // for unix debugging, capture IO.
//            if (Globals.DEV) {
//                int exitCode = 1;
//                try {
//                    exitCode = launcherProcess.waitFor();
//                    System.exit(exitCode);
//                } catch (Exception ex) {
//                    // ignore.
//                }
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // ignore for now
        }
        //TODO: connect probably
//        String s = servernbtstorage.field_35793_b;
//        String as[] = s.split(":");
//        if(s.startsWith("["))
//        {
//            int i = s.indexOf("]");
//            if(i > 0)
//            {
//                String s1 = s.substring(1, i);
//                String s2 = s.substring(i + 1).trim();
//                if(s2.startsWith(":") && s2.length() > 0)
//                {
//                    s2 = s2.substring(1);
//                    as = new String[2];
//                    as[0] = s1;
//                    as[1] = s2;
//                } else
//                {
//                    as = new String[1];
//                    as[0] = s1;
//                }
//            }
//        }
//        if(as.length > 2)
//        {
//            as = new String[1];
//            as[0] = s;
//        }
//        mc.displayGuiScreen(new GuiConnecting(mc, as[0], as.length <= 1 ? 25565 : parseIntWithDefault(as[1], 25565)));
    }

    private void func_35328_b()
        throws IOException
    {
////        String s = servernbtstorage.field_35793_b;
////        String as[] = s.split(":");
////        if(s.startsWith("["))
////        {
////            int i = s.indexOf("]");
////            if(i > 0)
////            {
////                String s2 = s.substring(1, i);
////                String s3 = s.substring(i + 1).trim();
////                if(s3.startsWith(":") && s3.length() > 0)
////                {
////                    s3 = s3.substring(1);
////                    as = new String[2];
////                    as[0] = s2;
////                    as[1] = s3;
////                } else
////                {
////                    as = new String[1];
////                    as[0] = s2;
////                }
////            }
////        }
////        if(as.length > 2)
////        {
////            as = new String[1];
////            as[0] = s;
////        }
////        String s1 = as[0];
////        int j = as.length <= 1 ? 25565 : parseIntWithDefault(as[1], 25565);
////        Socket socket = null;
////        DataInputStream datainputstream = null;
////        DataOutputStream dataoutputstream = null;
////        try
////        {
////            socket = new Socket();
////            socket.setSoTimeout(3000);
////            socket.setTcpNoDelay(true);
////            socket.setTrafficClass(18);
////            socket.connect(new InetSocketAddress(s1, j), 3000);
////            datainputstream = new DataInputStream(socket.getInputStream());
////            dataoutputstream = new DataOutputStream(socket.getOutputStream());
////            dataoutputstream.write(254);
////            if(datainputstream.read() != 255)
////            {
////                throw new IOException("Bad message");
////            }
////            String s4 = Packet.readString(datainputstream, 64);
////            char ac[] = s4.toCharArray();
////            for(int k = 0; k < ac.length; k++)
////            {
////                if(ac[k] != '\247' && ChatAllowedCharacters.allowedCharacters.indexOf(ac[k]) < 0)
////                {
////                    ac[k] = '?';
////                }
////            }
////
////            s4 = new String(ac);
////            String as1[] = s4.split("\247");
////            s4 = as1[0];
////            int l = -1;
////            int i1 = -1;
////            try
////            {
////                l = Integer.parseInt(as1[1]);
////                i1 = Integer.parseInt(as1[2]);
////            }
////            catch(Exception exception) { }
////            servernbtstorage.field_35791_d = (new StringBuilder()).append("\2477").append(s4).toString();
////            if(l >= 0 && i1 > 0)
////            {
////                servernbtstorage.field_35794_c = (new StringBuilder()).append("\2477").append(l).append("\2478/\2477").append(i1).toString();
////            } else
////            {
////                servernbtstorage.field_35794_c = "\2478???";
////            }
//        }
//        finally
//        {
//            try
//            {
//                if(datainputstream != null)
//                {
//                    datainputstream.close();
//                }
//            }
//            catch(Throwable throwable) { }
//            try
//            {
//                if(dataoutputstream != null)
//                {
//                    dataoutputstream.close();
//                }
//            }
//            catch(Throwable throwable1) { }
//            try
//            {
//                if(socket != null)
//                {
//                    socket.close();
//                }
//            }
//            catch(Throwable throwable2) { }
//        }
    }

    protected void func_35325_a(String s, int i, int j)
    {
        if(s == null)
        {
            return;
        } else
        {
            int k = i + 12;
            int l = j - 12;
            int i1 = FontRenderer.minecraftFontRenderer.getStringWidth(s);
            drawGradientRect(k - 3, l - 3, k + i1 + 3, l + 8 + 3, 0xc0000000, 0xc0000000);
            FontRenderer.minecraftFontRenderer.drawStringWithShadow(s, k, l, -1);
            return;
        }
    }

    public List<MineOnlineServer> getServers()
    {
        return serverRepository.getServers() != null ? serverRepository.getServers() : new LinkedList<>();
    }

    static int func_35326_a(GuiMultiplayer guimultiplayer, int i)
    {
        return guimultiplayer.field_35341_g = i;
    }

    static int func_35333_b(GuiMultiplayer guimultiplayer)
    {
        return guimultiplayer.field_35341_g;
    }

    static GuiButton func_35329_c(GuiMultiplayer guimultiplayer)
    {
        return guimultiplayer.connectButton;
    }

    static void func_35332_b(GuiMultiplayer guimultiplayer, int i)
    {
        guimultiplayer.func_35322_a(i);
    }

    static Object func_35321_g()
    {
        return field_35343_b;
    }

    static int func_35338_m()
    {
        return field_35344_a;
    }

    static int func_35331_n()
    {
        return field_35344_a++;
    }

    static void func_35336_a(GuiMultiplayer guimultiplayer)
        throws IOException
    {
        guimultiplayer.func_35328_b();
    }

    static int func_35335_o()
    {
        return field_35344_a--;
    }

    static String func_35327_a(GuiMultiplayer guimultiplayer, String s)
    {
        return guimultiplayer.field_35350_v = s;
    }

    private static int field_35344_a = 0;
    private static Object field_35343_b = new Object();
    private GuiScreen parentScreen;
    private GuiSlotServer guiSlotServer;
    private int field_35341_g;
    private GuiButton connectButton;
    private boolean field_35346_k;
    private boolean field_35353_s;
    private boolean field_35352_t;
    private boolean field_35351_u;
    private String field_35350_v;
    private MineOnlineServerRepository serverRepository = new MineOnlineServerRepository();
}
