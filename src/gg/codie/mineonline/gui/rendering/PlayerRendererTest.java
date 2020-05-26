package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.rendering.animation.*;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        playerGameObject.setPlayerAnimation(new WalkPlayerAnimation());

        Camera camera = new DebugCamera();

        //System.out.println(Arrays.toString(TextureHelper.getPlaneTextureCoords(new Vector2f(32, 32), new Vector2f(0, 0), new Vector2f(32, 32))));

        RawModel model = loader.loadGUIToVAO(new Vector2f(-1, -1), new Vector2f(1, 1), TextureHelper.getPlaneTextureCoords(new Vector2f(32, 32), new Vector2f(0, 0), new Vector2f(32, 32)));
        ModelTexture modelTexture = new ModelTexture(loader.loadTexture(LauncherFiles.MISSING_TEXTURE));
        TexturedModel texturedModel =  new TexturedModel(model, modelTexture);
        System.out.println(texturedModel.getTexture().getTextureID());
        GUIObject backgroundImage = new GUIObject("Background", texturedModel, new Vector3f(0, 0, -1), new Vector3f(), new Vector3f(1, 1, 1));

        RawModel testButtonModel = loader.loadGUIToVAO(new Vector2f(0, -0.1f), new Vector2f(1, 1), TextureHelper.getPlaneTextureCoords(new Vector2f(32, 32), new Vector2f(0, 0), new Vector2f(32, 32)));
        ModelTexture testButtonTexture = new ModelTexture(loader.loadTexture(LauncherFiles.MISSING_TEXTURE));
        TexturedModel texturedTestButtonModel =  new TexturedModel(testButtonModel, testButtonTexture);
        System.out.println(texturedTestButtonModel.getTexture().getTextureID());
        GUIObject testButton = new GUIObject("Test Button", texturedTestButtonModel, new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1));



        // Game Loop
        while(!Display.isCloseRequested()) {

            GUIShader guiShader = new GUIShader();
            guiShader.start();
            guiShader.loadViewMatrix(camera);
            renderer.prepareGUI();
            renderer.renderGUI(backgroundImage, guiShader);
            guiShader.stop();

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

            renderer.render(playerGameObject, shader);

            shader.stop();

            DisplayManager.updateDisplay();

            guiShader.start();
            guiShader.loadViewMatrix(camera);
            renderer.prepareGUI();
            renderer.renderGUI(testButton, guiShader);
            guiShader.stop();

        }

        shader.cleanup();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

}
