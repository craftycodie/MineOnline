package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.gui.rendering.animation.*;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class WindowText {

    public static void main(String[] args) {
        DisplayManager.createDisplay();

        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);

        GameObject playerPivot = new GameObject("player_origin", new Vector3f(-20, 0, -70), new Vector3f(), new Vector3f(1, 1, 1));

        PlayerGameObject playerGameObject = new PlayerGameObject("player", loader, shader, new Vector3f(0, -16, 0), new Vector3f(), new Vector3f(1, 1, 1));

        playerPivot.addChild(playerGameObject);

        Camera camera = new DebugCamera();

        IPlayerAnimation playerAnimation = new WalkPlayerAnimation();
        playerAnimation.reset(playerGameObject);

        // Game Loop
        while(!Display.isCloseRequested()) {
            //entity.increaseRotation(0, 1, 0);
            //entity.increasePosition(0, 0, -0.05f);

//            if(playerPivot.getLocalRotation().z > 0) {
//                playerPivot.increaseRotation(new Vector3f(0, 0, -playerPivot.getLocalRotation().z));
//            }

            if(Mouse.isButtonDown(0)) {
                Vector3f currentRotation = playerPivot.getLocalRotation();
                Vector3f rotation = new Vector3f();

                float dy = Mouse.getDY();

//                if(currentRotation.x + (dy * -0.3f) > 30) {
//                    rotation.x = 30 - currentRotation.x;
//                } else if(currentRotation.x + (dy * -0.3f) < -30) {
//                    rotation.x = -30 - currentRotation.x;
//                } else {
//                    rotation.x = dy * -0.3f;
//                }

                rotation.y = (Mouse.getDX() * 0.5f);

                System.out.println(rotation.toString());

                playerPivot.increaseRotation(rotation);
//                Vector3f rotation = playerPivot.getLocalRotation();



//                playerPivot.setLocalRotation(new Vector3f(rotation.x, rotation.y, 0));
//
//                if(rotation.x > 30) {
//                    System.out.println("player: x" + rotation.x + " y" + rotation.y + " z" + rotation.z);
//                    rotation.x = 30;
//                    playerPivot.setLocalRotation(rotation);
//                }
//
//                if(rotation.x < -30) {
//                    System.out.println("player: x" + rotation.x + " y" + rotation.y + " z" + rotation.z);
//                    rotation.x = -30;
//                    playerPivot.setLocalRotation(rotation);
//                }
            }


            playerAnimation.animate(playerGameObject);

            camera.move();

            renderer.prepare();

            shader.start();
            shader.loadViewMatrix(camera);

            renderer.render(playerGameObject, shader);

//            for(GameObject box : playerModelBoxes) {
//                renderer.render(box, shader);
//            }

            //renderer.render(entity, shader);

            shader.stop();

            DisplayManager.updateDisplay();

        }

        shader.cleanup();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

}
