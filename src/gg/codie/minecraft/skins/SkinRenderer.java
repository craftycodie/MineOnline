package gg.codie.minecraft.skins;

import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.gui.IMenuScreen;
import gg.codie.mineonline.gui.MouseHandler;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public class SkinRenderer {

//    public static boolean formopen = false;

//    private static GUIText playerName;
    public static void setMenuScreen(IMenuScreen menuScreen) {

        if(SkinRenderer.menuScreen != null)
            SkinRenderer.menuScreen.cleanUp();

        SkinRenderer.menuScreen = menuScreen;

//        if(playerName != null)
//            playerName.remove();
//
//        if(menuScreen.showPlayer())
//            playerName = new GUIText(Session.session.getUsername(), 1.5f, TextMaster.minecraftFont, new Vector2f(168, 100), 160, true, true);
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

    public static final int DEFAULT_HEIGHT = 480;
    public static final int DEFAULT_WIDTH = 350;

    public static void main(String[] args) throws Exception {
        LibraryManager.updateNativesPath();

        DisplayManager.init();

        DisplayManager.createDisplay(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        Display.setTitle("Skin Preview");

        DisplayManager.getFrame().addWindowListener(closeListener);

        Renderer renderer = new Renderer();
        Loader loader = new Loader();
        TextMaster.init(loader);

        GameObject playerPivot = new GameObject("player_origin", new Vector3f(), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
        PlayerGameObject playerGameObject = new PlayerGameObject("player", loader, new Vector3f(0, -16, 0), new Vector3f(), new Vector3f(1, 1, 1));
        playerPivot.addChild(playerGameObject);

        playerScale = new GameObject("player scale", new Vector3f(0, 0, -30), new Vector3f(), new Vector3f(1, 1, 1));
        playerScale.addChild(playerPivot);

        playerGameObject.setPlayerAnimation(new IdlePlayerAnimation());
        Camera camera = new Camera();

        String[] panoramaNames = new String[] {"sunset", "sunset", "sunset", "sunset", "sunset", "midnight", "midnight", "midnight", "sunset"};
        //String[] panoramaNames = new String[] {"noon"};

        RawModel model = loader.loadBoxToVAO(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1), TextureHelper.getCubeTextureCoords(new Vector2f(8192, 4096),
                new Vector2f(4161, 0), new Vector2f(1387, 1387),
                new Vector2f(1387, 0), new Vector2f(1387, 1387),
                new Vector2f(2774, 0), new Vector2f(1387, 1387),
                new Vector2f(0, 0), new Vector2f(1387, 1387),
                new Vector2f(0, 1387), new Vector2f(1387, 1387),
                new Vector2f(1387, 1387), new Vector2f(1387, 1387)
        ));
        ModelTexture modelTexture = new ModelTexture(loader.loadTexture(SkinRenderer.class.getResource("/img/panorama_" + panoramaNames[new Random().nextInt(panoramaNames.length)] + ".png")));
        TexturedModel texturedModel =  new TexturedModel(model, modelTexture);
        GameObject backgroundImage = new GUIObject("Background", texturedModel, new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(75f, 75f, 75f));

        setMenuScreen(new SkinPreviewMenuScreen());

//        FontType font = new FontType(loader.loadTexture(MenuManager.class.getResource("/font/font.png")), MenuManager.class.getResourceAsStream("/font/font.fnt"));
//        GUIText text = new GUIText("MineOnline Pre-Release", 1.5f, font, new Vector2f(0, 0), DisplayManager.getDefaultWidth(), false, true);
//        text.setColour(0.33f, 0.33f, 0.33f);

        int lastWidth = Display.getWidth();
        int lastHeight = Display.getHeight();
        // Game Loop
        while(!Display.isCloseRequested()) {
            MouseHandler.update();
            renderer.prepare();

            if(Display.getWidth() != lastWidth || Display.getHeight() != lastHeight) {
                menuScreen.resize();
            }

            lastWidth = Display.getWidth();
            lastHeight = Display.getHeight();

            backgroundImage.increaseRotation(new Vector3f(0, 0.025f, 0));

            // Handle player resizing.
            float maxScale = 1;
            float minScale = 1;
            if((double)Display.getWidth() / Display.getHeight() > DEFAULT_WIDTH / DEFAULT_HEIGHT){
                maxScale = Display.getWidth() / (float)DEFAULT_WIDTH;
                minScale = Display.getHeight() / (float)DEFAULT_HEIGHT;
            } else {
                maxScale = Display.getHeight() / (float)DEFAULT_HEIGHT;
                minScale = Display.getWidth() / (float)DEFAULT_WIDTH;
            }
            if(maxScale > 1) {
                playerScale.setScale(new Vector3f((1 / maxScale) * minScale, (1 / maxScale) * minScale, (1 / maxScale) * minScale));
            }

            if(Mouse.isButtonDown(0)) {
                Vector3f currentRotation = playerPivot.getLocalRotation();
                Vector3f rotation = new Vector3f();

                rotation.y = (Mouse.getDX() * 0.5f);

                if (menuScreen.showPlayer())
                    playerPivot.increaseRotation(rotation);
            }

            playerGameObject.update();
            menuScreen.update();

            StaticShader.singleton.start();
            StaticShader.singleton.loadViewMatrix(camera);

            renderer.render(backgroundImage, StaticShader.singleton);

            if (menuScreen.showPlayer()) {
                renderer.render(playerGameObject, StaticShader.singleton);
            }

            StaticShader.singleton.stop();

            menuScreen.render(renderer);

            TextMaster.render();

            DisplayManager.updateDisplay();
        }

        StaticShader.singleton.cleanUp();
        loader.cleanUp();
        TextMaster.cleanUp();
        DisplayManager.closeDisplay();

        DisplayManager.getFrame().removeWindowListener(closeListener);
    }

}
