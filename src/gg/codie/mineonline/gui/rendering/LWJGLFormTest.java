package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

public class LWJGLFormTest {

    public static void main(String[] args) throws Exception {
        LibraryManager.updateClasspath();
        LibraryManager.updateNativesPath();

        DisplayManager.createDisplay();

        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer();

        Loader loader = new Loader();


        GameObject playerPivot = new GameObject("player_origin", new Vector3f(-20, 0, -65), new Vector3f(0, 30, 0), new Vector3f(1, 1, 1));

        PlayerGameObject playerGameObject = new PlayerGameObject("player", loader, shader, new Vector3f(0, -16, 0), new Vector3f(), new Vector3f(1, 1, 1));

        Session session = new Session("codie");


        playerPivot.addChild(playerGameObject);

        Camera camera = new Camera();

        // Game Loop
        while(!Display.isCloseRequested()) {
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

        }

        shader.cleanup();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

}
