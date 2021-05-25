package gg.codie.mineonline.gui;

import gg.codie.minecraft.api.AuthServer;
import gg.codie.minecraft.api.MojangAPI;
import gg.codie.mineonline.*;
import gg.codie.mineonline.api.ClassicServerAuthService;
import gg.codie.mineonline.api.UpdateCheckerService;
import gg.codie.mineonline.discord.DiscordRPCHandler;
import gg.codie.mineonline.gui.input.MouseHandler;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.screens.*;
import gg.codie.mineonline.gui.textures.EGUITexture;
import gg.codie.mineonline.utils.LastLogin;
import gg.codie.mineonline.utils.Logging;
import org.json.JSONObject;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.imageio.ImageIO;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;

public class MenuManager {

    public static boolean formopen = false;
    private static ClassicServerAuthService classicAuthService = new ClassicServerAuthService();

    public static void setMenuScreen(AbstractGuiScreen guiScreen) {
        if(MenuManager.guiScreen != null)
            MenuManager.guiScreen.onGuiClosed();
        MenuManager.guiScreen = guiScreen;
    }

    public static void resizeMenu() {
        if(guiScreen != null)
            guiScreen.resize(Display.getParent().getWidth(), Display.getParent().getHeight());
    }

    private static AbstractGuiScreen guiScreen;

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


    static boolean updateAvailable = false;
    public static boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public static void scaledTessellator(int i, int j, int k, int l, int i1, int j1)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Renderer tessellator = Renderer.singleton;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(i + 0, j + j1, 0.0D, (float)(k + 0) * f, (float)(l + j1) * f1);
        tessellator.addVertexWithUV(i + i1, j + j1, 0.0D, (float)(k + i1) * f, (float)(l + j1) * f1);
        tessellator.addVertexWithUV(i + i1, j + 0, 0.0D, (float)(k + i1) * f, (float)(l + 0) * f1);
        tessellator.addVertexWithUV(i + 0, j + 0, 0.0D, (float)(k + 0) * f, (float)(l + 0) * f1);
        tessellator.draw();
    }

    static boolean skipUpdates = false;
    public static void main(String[] args) throws Exception {
        System.setProperty("apple.awt.application.name", "MineOnline");

        Logging.enableLogging();

        DiscordRPCHandler.initialize();

        if(Arrays.stream(args).anyMatch(arg -> arg.equals("-skipupdates")))
            skipUpdates = true;

        LibraryManager.updateNativesPath();

        formopen = true;

        if (!skipUpdates && Globals.BRANCH.equals("release")) {
            try {
                updateAvailable = !new UpdateCheckerService().getLauncherVersion().replaceAll("\\s", "").equals(Globals.LAUNCHER_VERSION);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // TODO: LTS CHANGE
        skipUpdates = true;

        boolean multiinstance = false;
        String quicklaunch = null;
        String joinserver = null;

        // If a user drags a jar onto the launcher, it'll be at arg 1, quicklaunch it.jo
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

        if (quicklaunch == null && !skipUpdates) {
            showLoadingScreen();
        }

        // Load this before showing the display.
        MinecraftVersionRepository.getSingleton(quicklaunch);

        if(quicklaunch != null) {
            MinecraftVersion version = MinecraftVersionRepository.getSingleton().getVersion(quicklaunch);

            if (version != null && version.type.equals("launcher")) {
                MinecraftVersion.launchMinecraft(quicklaunch, null, null, null);
                return;
            }
        }

        LastLogin lastLogin = null;

        if(!multiinstance)
            lastLogin = LastLogin.readLastLogin();

        String username = null;
        String sessionToken = null;
        String uuid = null;

        if(lastLogin != null) {
            try {
                // TODO: Add support for refreshing Microsoft auth.
                if (lastLogin.legacy) {
                    JSONObject login = AuthServer.refresh(lastLogin.accessToken, lastLogin.clientToken);

                    if (login.has("error"))
                        throw new Exception(login.getString("error"));
                    if (!login.has("accessToken") || !login.has("selectedProfile"))
                        throw new Exception("Failed to authenticate!");
                    if (MojangAPI.minecraftProfile(login.getJSONObject("selectedProfile").getString("name")).optBoolean("demo", false))
                        throw new Exception("Please buy Minecraft to use MineOnline.");

                    sessionToken = login.getString("accessToken");
                    username = login.getJSONObject("selectedProfile").getString("name");
                    uuid = login.getJSONObject("selectedProfile").getString("id");
                } else {
                    sessionToken = lastLogin.accessToken;
                    username = lastLogin.username;
                    uuid = lastLogin.uuid;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        Settings.singleton.loadSettings(true);

        if (sessionToken != null && username != null) {
            new Session(username, sessionToken, lastLogin.clientToken, uuid, true);
            LastLogin.writeLastLogin(sessionToken, lastLogin.clientToken, lastLogin.loginUsername, username, uuid, lastLogin.legacy);
        }

        if (Session.session != null && Session.session.isOnline() && quicklaunch != null) {
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
                mppass = classicAuthService.getMPPass(ip, port, Session.session.getAccessToken(), Session.session.getUuid(), Session.session.getUsername());
            }
            MinecraftVersion.launchMinecraft(quicklaunch, ip, port, mppass);
            return;
        }

        if (DisplayManager.getFrame() == null)
            showLoadingScreen();

        if(Session.session != null && Session.session.isOnline())
            if(joinserver != null)
                setMenuScreen(new GuiDirectConnect(null, joinserver));
            else
                setMenuScreen(new GuiMainMenu());
        else
            setMenuScreen(new GuiLoginLegacy());

        int lastWidth = Display.getWidth();
        int lastHeight = Display.getHeight();

        long startTime = System.currentTimeMillis();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(new java.awt.image.BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB), "png", os);                          // Passing: ​(RenderedImage im, String formatName, OutputStream output)
        InputStream is = new ByteArrayInputStream(os.toByteArray());

        int panoramaTexture = Loader.singleton.loadTexture("panorama buffer", is);

        // Game Loop
        while(!Display.isCloseRequested() && formopen) {
            MouseHandler.update();
//            renderer.prepare();

            if(Display.getParent().getWidth() != lastWidth || Display.getParent().getHeight() != lastHeight) {
                guiScreen.resize(Display.getParent().getWidth(), Display.getParent().getHeight());
            }

            lastWidth = Display.getParent().getWidth();
            lastHeight = Display.getParent().getHeight();


            if(!formopen) return;

            GUIScale scaledresolution = new GUIScale(Display.getParent().getWidth(), Display.getParent().getHeight());

            int i = (int)scaledresolution.getScaledWidth();
            int j = (int)scaledresolution.getScaledHeight();
            int k = (Mouse.getX() * i) / Display.getParent().getWidth();
            int i1 = j - (Mouse.getY() * j) / Display.getParent().getHeight() - 1;

            if (MenuManager.guiScreen != null) {


                GL11.glEnable(GL11.GL_TEXTURE_2D);

                new GUIScale(Display.getParent().getWidth(), Display.getParent().getHeight());
                GL11.glViewport(0, 0, Display.getParent().getWidth(), Display.getParent().getHeight());
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                GL11.glClear(256);
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();
                GL11.glOrtho(0.0D, GUIScale.lastScaledWidth(), GUIScale.lastScaledHeight(), 0.0D, 1000D, 3000D);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                GL11.glTranslatef(0.0F, 0.0F, -2000F);
                GL11.glClear(256);
                MenuManager.guiScreen.updateScreen();
                panorama_func(((float)(System.currentTimeMillis() - startTime) / 1000) * 20, panoramaTexture);

                MenuManager.guiScreen.drawScreen(k, i1);

                MenuManager.guiScreen.handleInput();
            }

            DisplayManager.updateDisplay();
        }

        DisplayManager.closeDisplay();

        DisplayManager.getFrame().removeWindowListener(closeListener);
    }

    private static void showLoadingScreen() throws LWJGLException {
        DisplayManager.init();
        DisplayManager.createDisplay();
        Mouse.create();
        Keyboard.create();

        new Loader();

        new GUIScale(Display.getParent().getWidth(), Display.getParent().getHeight());

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearDepth(1.0D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(515);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(516, 0.1F);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glClear(16640);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, GUIScale.lastScaledWidth(), GUIScale.lastScaledHeight(), 0.0D, 1000D, 3000D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000F);
        GL11.glViewport(0, 0, Display.getParent().getWidth(), Display.getParent().getHeight());
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Renderer tessellator = Renderer.singleton;
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.LOADING));
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, 255);
        tessellator.addVertexWithUV(0.0D, Display.getParent().getHeight(), 0.0D, 0.0D, 0.0D);
        tessellator.addVertexWithUV(Display.getParent().getWidth(), Display.getParent().getHeight(), 0.0D, 0.0D, 0.0D);
        tessellator.addVertexWithUV(Display.getParent().getWidth(), 0.0D, 0.0D, 0.0D, 0.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        char c = '\u0100';
        char c1 = '\u0100';
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.setColorRGBA(255, 255, 255, 255);
        scaledTessellator((GUIScale.lastScaledWidth() - c) / 2, (GUIScale.lastScaledHeight() - c1) / 2, 0, 0, c, c1);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(516, 0.1F);
        Display.swapBuffers();

        DisplayManager.getFrame().addWindowListener(closeListener);

        Keyboard.enableRepeatEvents(true);
    }

    private static void panorama(float f)
    {
        Renderer tessellator = Renderer.singleton;
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GLU.gluPerspective(120F, 1.0F, 0.05F, 10F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        int k = 8;
        for(int l = 0; l < k * k; l++)
        {
            GL11.glPushMatrix();
            float f1 = ((float)(l % k) / (float)k - 0.5F) / 64F;
            float f2 = ((float)(l / k) / (float)k - 0.5F) / 64F;
            float f3 = 0.0F;
            GL11.glTranslatef(f1, f2, f3);
            GL11.glRotatef((float)Math.sin(((float)0 + f) / 400F) * 25F + 20F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-((float)0 + f) * 0.1F, 0.0F, 1.0F, 0.0F);
            for(int i1 = 0; i1 < 6; i1++)
            {
                GL11.glPushMatrix();
                if(i1 == 1)
                {
                    GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
                }
                if(i1 == 2)
                {
                    GL11.glRotatef(180F, 0.0F, 1.0F, 0.0F);
                }
                if(i1 == 3)
                {
                    GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
                }
                if(i1 == 4)
                {
                    GL11.glRotatef(90F, 1.0F, 0.0F, 0.0F);
                }
                if(i1 == 5)
                {
                    GL11.glRotatef(-90F, 1.0F, 0.0F, 0.0F);
                }

                EGUITexture panoarma;
                switch(i1) {
                    case 5:
                        panoarma = EGUITexture.PANORAMA5;
                        break;
                    case 4:
                        panoarma = EGUITexture.PANORAMA4;
                        break;
                    case 3:
                        panoarma = EGUITexture.PANORAMA3;
                        break;
                    case 2:
                        panoarma = EGUITexture.PANORAMA2;
                        break;
                    case 1:
                        panoarma = EGUITexture.PANORAMA1;
                        break;
                    case 0:
                    default:
                        panoarma = EGUITexture.PANORAMA0;
                        break;
                }

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(panoarma));
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA(255, 255, 255, 255 / (l + 1));
                float f4 = 0.0F;
                tessellator.addVertexWithUV(-1D, -1D, 1.0D, 0.0F + f4, 0.0F + f4);
                tessellator.addVertexWithUV(1.0D, -1D, 1.0D, 1.0F - f4, 0.0F + f4);
                tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, 1.0F - f4, 1.0F - f4);
                tessellator.addVertexWithUV(-1D, 1.0D, 1.0D, 0.0F + f4, 1.0F - f4);
                tessellator.draw();
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
            GL11.glColorMask(true, true, true, false);
        }

        GL11.glColorMask(true, true, true, true);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private static void blur_related(int texture)
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        GL11.glColorMask(true, true, true, false);
        Renderer tessellator = Renderer.singleton;
        tessellator.startDrawingQuads();
        byte byte0 = 3;
        for(int i = 0; i < byte0; i++)
        {
            tessellator.setColorRGBA(255, 255, 255, (int)(255 / (float)(i + 1)));
            int j = GUIScale.lastScaledWidth();
            int k = GUIScale.lastScaledHeight();
            float f1 = (float)(i - byte0 / 2) / 256F;
            tessellator.addVertexWithUV(j, k, 0, 0.0F + f1, 0.0D);
            tessellator.addVertexWithUV(j, 0.0D, 0, 1.0F + f1, 0.0D);
            tessellator.addVertexWithUV(0.0D, 0.0D, 0, 1.0F + f1, 1.0D);
            tessellator.addVertexWithUV(0.0D, k, 0, 0.0F + f1, 1.0D);
        }

        tessellator.draw();
        GL11.glColorMask(true, true, true, true);
    }

    private static void panorama_func(float tick, int texture)
    {
        GL11.glViewport(0, 0, 256, 256);
        panorama(tick);
        blur_related(texture);
        blur_related(texture);
        blur_related(texture);
        blur_related(texture);
        blur_related(texture);
        blur_related(texture);
        blur_related(texture);
        blur_related(texture);
        GL11.glViewport(0, 0, Display.getParent().getWidth(), Display.getParent().getHeight());
        Renderer tessellator = Renderer.singleton;
        tessellator.startDrawingQuads();
        float f1 = GUIScale.lastScaledWidth() <= GUIScale.lastScaledHeight() ? 120F / GUIScale.lastScaledHeight() : 120F / GUIScale.lastScaledWidth();
        float f2 = ((float)GUIScale.lastScaledHeight() * f1) / 256F;
        float f3 = ((float)GUIScale.lastScaledWidth() * f1) / 256F;
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        tessellator.setColorRGBA(255, 255, 255, 255);
        int k = GUIScale.lastScaledWidth();
        int l = GUIScale.lastScaledHeight();
        tessellator.addVertexWithUV(0.0D, l, 0, 0.5F - f2, 0.5F + f3);
        tessellator.addVertexWithUV(k, l, 0, 0.5F - f2, 0.5F - f3);
        tessellator.addVertexWithUV(k, 0.0D, 0, 0.5F + f2, 0.5F - f3);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0, 0.5F + f2, 0.5F + f3);
        tessellator.draw();
    }

}
