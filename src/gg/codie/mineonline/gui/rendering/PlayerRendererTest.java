package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.rendering.animation.*;
import gg.codie.mineonline.gui.rendering.components.LargeButton;
import gg.codie.mineonline.gui.rendering.components.MediumButton;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class PlayerRendererTest {

    static boolean formopen = false;

    public static void main(String[] args) throws Exception {
        formopen = true;

        DisplayManager.getFrame().setResizable(false);

        LibraryManager.updateClasspath();
        LibraryManager.updateNativesPath();

        DisplayManager.createDisplay();

        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);
        Loader loader = new Loader();
        GameObject playerPivot = new GameObject("player_origin", new Vector3f(-20, 0, -65), new Vector3f(0, 30, 0), new Vector3f(1, 1, 1));
        PlayerGameObject playerGameObject = new PlayerGameObject("player", loader, shader, new Vector3f(0, -16, 0), new Vector3f(), new Vector3f(1, 1, 1));
        new Session("codie");
        playerPivot.addChild(playerGameObject);
        playerGameObject.setPlayerAnimation(new IdlePlayerAnimation());
        Camera camera = new DebugCamera();

        RawModel model = loader.loadPlaneToVAO(new Vector3f(-2, -1, 0), new Vector3f(2, 1, 0), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(2048, 1024), new Vector2f(0, 0), new Vector2f(2048, 1024)));
        ModelTexture modelTexture = new ModelTexture(loader.loadTexture(PlayerRendererTest.class.getResource("/img/background.png")));
        TexturedModel texturedModel =  new TexturedModel(model, modelTexture);
        GameObject backgroundImage = new GUIObject("Background", texturedModel, new Vector3f(0, 0, -75), new Vector3f(), new Vector3f(37.5f, 37.5f, 37.5f));

        RawModel logoModel = loader.loadGUIToVAO(new Vector2f((Display.getWidth() / 2) -200, Display.getHeight() - 69), new Vector2f(400, 49), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 40), new Vector2f(400, 49)));
        ModelTexture logoTexture = new ModelTexture(loader.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")));
        TexturedModel texuredLogoModel =  new TexturedModel(logoModel, logoTexture);
        GUIObject logo = new GUIObject("logo", texuredLogoModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));



        MediumButton testButton = new MediumButton("Play", new Vector2f((Display.getWidth() / 2) + 30, (Display.getHeight() / 2) - 40), new IOnClickListener() {
            @Override
            public void onClick() {
                formopen = false;
            }
        });

        MediumButton logoutButton = new MediumButton("Join Server", new Vector2f((Display.getWidth() / 2) + 30, (Display.getHeight() / 2) + 20), null);

        MediumButton versionButton = new MediumButton("Version: b1.7.3", new Vector2f((Display.getWidth() / 2) + 30, (Display.getHeight() / 2) + 80), null);




        // Game Loop
        while(!Display.isCloseRequested() && formopen) {
            renderer.prepare();
            // Camera roll lock.
            // Broken and not necessary.

//            if(playerPivot.getLocalRotation().z > 0) {
//                playerPivot.increaseRotation(new Vector3f(0, 0, -playerPivot.getLocalRotation().z));
//            }

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

//                System.out.println(rotation.toString());

                playerPivot.increaseRotation(rotation);
            }

            playerGameObject.update();
            testButton.update();
            logoutButton.update();
            versionButton.update();

            camera.move();

            shader.start();
            shader.loadViewMatrix(camera);

            renderer.render(backgroundImage, shader);
            renderer.render(playerGameObject, shader);

            shader.stop();

            GUIShader guiShader = new GUIShader();
            guiShader.start();
            guiShader.loadViewMatrix(camera);
            renderer.prepareGUI();
//            renderer.render(backgroundImage, guiShader);
            renderer.renderGUI(logo, guiShader);
            testButton.render(renderer, guiShader);
            logoutButton.render(renderer, guiShader);
            versionButton.render(renderer, guiShader);
            guiShader.stop();

            //renderer.renderString(new Vector2f((Display.getWidth() / 2) + 65, (Display.getHeight() / 2) - 32), 18, "Play Minecraft", Color.white);


            DisplayManager.updateDisplay();

//            if (Mouse.isButtonDown(0)) {
//                int x = Mouse.getX();
//                int y = Mouse.getY();
//
//                System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);
//
//                formopen = false;
//            }

        }

        shader.cleanup();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

}
