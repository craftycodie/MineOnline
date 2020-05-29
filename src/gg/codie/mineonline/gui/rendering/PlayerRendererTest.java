package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.rendering.animation.*;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

public class PlayerRendererTest {

    public static void main(String[] args) throws Exception {
        LibraryManager.updateClasspath();
        LibraryManager.updateNativesPath();

        DisplayManager.createDisplay();

        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);
        Loader loader = new Loader();
        GameObject playerPivot = new GameObject("player_origin", new Vector3f(-20, 0, -65), new Vector3f(0, 30, 0), new Vector3f(1, 1, 1));
        PlayerGameObject playerGameObject = new PlayerGameObject("player", loader, shader, new Vector3f(0, -16, 0), new Vector3f(), new Vector3f(1, 1, 1));
        Session session = new Session("codie");
        playerPivot.addChild(playerGameObject);
        playerGameObject.setPlayerAnimation(new IdlePlayerAnimation());
        Camera camera = new DebugCamera();

        //System.out.println(Arrays.toString(TextureHelper.getPlaneTextureCoords(new Vector2f(32, 32), new Vector2f(0, 0), new Vector2f(32, 32))));

        RawModel model = loader.loadPlaneToVAO(new Vector3f(-2, -1, 0), new Vector3f(2, 1, 0), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(2048, 1024), new Vector2f(0, 0), new Vector2f(2048, 1024)));
        ModelTexture modelTexture = new ModelTexture(loader.loadTexture(PlayerRendererTest.class.getResource("/img/background.png")));
        TexturedModel texturedModel =  new TexturedModel(model, modelTexture);
        System.out.println(texturedModel.getTexture().getTextureID());
        GameObject backgroundImage = new GUIObject("Background", texturedModel, new Vector3f(0, 0, -75), new Vector3f(), new Vector3f(37.5f, 37.5f, 37.5f));

        RawModel testButtonModel = loader.loadGUIToVAO(new Vector2f((Display.getWidth() / 2) + 50, Display.getHeight() / 2), new Vector2f(200, 40), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 0), new Vector2f(100, 20)));
        ModelTexture testButtonTexture = new ModelTexture(loader.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")));
        TexturedModel texturedTestButtonModel =  new TexturedModel(testButtonModel, testButtonTexture);
        System.out.println(texturedTestButtonModel.getTexture().getTextureID());
        GUIObject testButton = new GUIObject("Test Button", texturedTestButtonModel, new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1));


        boolean formopen = true;

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
            renderer.renderGUI(testButton, guiShader);
            guiShader.stop();

            renderer.renderString(new Vector2f((Display.getWidth() / 2) + 65, (Display.getHeight() / 2) - 32), 18, "Play Minecraft", Color.white);


            DisplayManager.updateDisplay();

            if (Mouse.isButtonDown(0)) {
                int x = Mouse.getX();
                int y = Mouse.getY();

                System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);

                formopen = false;
            }

        }

        shader.cleanup();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

}
