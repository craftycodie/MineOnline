package gg.codie.mineonline.gui;

import gg.codie.mineonline.Globals;
import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.mineonline.gui.font.FontType;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.animation.*;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import gg.codie.utils.LastLogin;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Random;

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
            DisplayManager.getFrame().dispose();
            System.exit(0);
        }

        @Override
        public void windowOpened(WindowEvent e) {
            DisplayManager.getFrame().getOwner().setBackground(java.awt.Color.black);
        }
    };

    private static GameObject playerScale;

    static boolean updateAvailable = false;
    public static boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public static void main(String[] args) throws Exception {
        formopen = true;

        try {
            updateAvailable = !MinecraftAPI.getLauncherVersion().replaceAll("\\s","").equals(Globals.LAUNCHER_VERSION);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        LibraryManager.updateClasspath();
        LibraryManager.updateNativesPath();

        DisplayManager.init();

        DisplayManager.createDisplay();

        DisplayManager.getFrame().addWindowListener(closeListener);

        Renderer renderer = new Renderer();
        Loader loader = new Loader();
        TextMaster.init(loader);

        GameObject playerPivot = new GameObject("player_origin", new Vector3f(), new Vector3f(0, 30, 0), new Vector3f(1, 1, 1));
        PlayerGameObject playerGameObject = new PlayerGameObject("player", loader, StaticShader.singleton, new Vector3f(0, -21, 0), new Vector3f(), new Vector3f(1, 1, 1));
        playerPivot.addChild(playerGameObject);

        playerScale = new GameObject("player scale", new Vector3f(-20, 0, -65), new Vector3f(), new Vector3f(1, 1, 1));
        playerScale.addChild(playerPivot);

        playerGameObject.setPlayerAnimation(new IdlePlayerAnimation());
        Camera camera = new Camera();

        String[] panoramaNames = new String[] {"sunset", "sunset", "sunset", "sunset", "sunset", "midnight", "midnight", "midnight", "sunset", "noon"};
        //String[] panoramaNames = new String[] {"noon"};

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
        GameObject backgroundImage = new GUIObject("Background", texturedModel, new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(75f, 75f, 75f));

        LastLogin lastLogin = LastLogin.readLastLogin();
        if(lastLogin != null ) {
            try {
                String sessionToken = MinecraftAPI.login(lastLogin.username, lastLogin.password);
                if (sessionToken != null) {
                    new Session(lastLogin.username, sessionToken);
                    LastLogin.writeLastLogin(lastLogin.username, lastLogin.password);
                    setMenuScreen(new MainMenuScreen());
                } else {
                    setMenuScreen(new LoginMenuScreen(false));
                }
            } catch (IOException ex) {
                setMenuScreen(new LoginMenuScreen(true));
            }
        } else {
            setMenuScreen(new LoginMenuScreen(false));
        }

//        FontType font = new FontType(loader.loadTexture(MenuManager.class.getResource("/font/font.png")), MenuManager.class.getResourceAsStream("/font/font.fnt"));
//        GUIText text = new GUIText("MineOnline Pre-Release", 1.5f, font, new Vector2f(0, 0), DisplayManager.getDefaultWidth(), false, true);
//        text.setColour(0.33f, 0.33f, 0.33f);

        int lastWidth = Display.getWidth();
        int lastHeight = Display.getHeight();
        // Game Loop
        while(!Display.isCloseRequested() && formopen) {
            MouseHandler.update();
            renderer.prepare();

            // Camera roll lock.
            // Broken and not necessary.
//            if(playerPivot.getLocalRotation().z > 0) {
//                playerPivot.increaseRotation(new Vector3f(0, 0, -playerPivot.getLocalRotation().z));
//            }

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

            ///playerPivot.scale(new Vector3f(2 - currentScale.x, 2 - currentScale.y, 2 - currentScale.z));
            //playerPivot.setScale(new Vector3f((float)DisplayManager.getScale(), (float)DisplayManager.getScale(), (float)DisplayManager.getScale()));

            if(Mouse.isButtonDown(0)) {
                Vector3f currentRotation = playerPivot.getLocalRotation();
                Vector3f rotation = new Vector3f();

                // Camera pitch rotation with lock.
                // Currently broken.

//                float dy = Mouse.getDY();

//                if(currentRotation.x + (dy * -0.3f) > 30) {
//                    rotation.x = 30 - currentRotation.x;
//                } else if(currentRotation.x + (dy * -0.3f) < -30) {
//                    rotation.x = -30 - currentRotation.x;
//                } else {
//                    rotation.x = dy * -0.3f;
//                }

                rotation.y = (Mouse.getDX() * 0.5f);

                if (menuScreen.showPlayer())
                    playerPivot.increaseRotation(rotation);
            }

            playerGameObject.update();
            menuScreen.update();
//
//            camera.move();
//
            if(!formopen) return;
//
            StaticShader.singleton.start();
            StaticShader.singleton.loadViewMatrix(camera);
//
            renderer.render(backgroundImage, StaticShader.singleton);
//
            if (menuScreen.showPlayer()) {
                //GL11.glPushMatrix();
                renderer.render(playerGameObject, StaticShader.singleton);
                //GL11.glPopMatrix();
            }
//
            StaticShader.singleton.stop();
//
            menuScreen.render(renderer);
//
            TextMaster.render();
//
            DisplayManager.updateDisplay();
        }

        StaticShader.singleton.cleanUp();
        loader.cleanUp();
        TextMaster.cleanUp();
        DisplayManager.closeDisplay();

        DisplayManager.getFrame().removeWindowListener(closeListener);
    }

}
