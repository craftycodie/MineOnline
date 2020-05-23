package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.gui.rendering.animation.*;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
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

        GameObject playerPivot = new GameObject("player_origin", new Vector3f(0, 0, -75), 0, 0, 0, 1 );

        PlayerGameObject playerGameObject = new PlayerGameObject("player", loader, shader, new Vector3f(0, -16, 0), 0, 0, 0, 1 );

        playerPivot.addChild(playerGameObject);

        Camera camera = new DebugCamera();

        IPlayerAnimation playerAnimation = new WalkPlayerAnimation();
        playerAnimation.reset(playerGameObject);

        // Game Loop
        while(!Display.isCloseRequested()) {
            //entity.increaseRotation(0, 1, 0);
            //entity.increasePosition(0, 0, -0.05f);

//            if(Mouse.isButtonDown(0)) {
//                playerPivot.increaseRotation(Mouse.getDY() * -0.3f, Mouse.getDX() * 0.3f, 0);
//
//                if(playerPivot.getLocalXRot() > 30) {
//                    playerPivot.setLocalXRot(30);
//                }
//
//                if(playerPivot.getLocalXRot() < -30) {
//                    playerPivot.setLocalXRot(-30);
//                }
//
//            }


            playerAnimation.animate(playerGameObject);

//            playerGameObject.playerRightArm.increaseRotation(1, 0, 0);
//            playerGameObject.playerLeftArm.increaseRotation(-1, 0, 0);
//            playerGameObject.playerLeftArm.increaseRotation(1, 0, 0);
//            playerGameObject.playerRightArm.increaseRotation(-1, 0, 0);
//
//            playerGameObject.playerLeftLeg.increaseRotation(1, 0, 0);
//            playerGameObject.playerRightLeg.increaseRotation(-1, 0, 0);
//
//            playerGameObject.playerCloak.increaseRotation(0, 0, 2);
//
//            playerGameObject.playerHead.increaseRotation(0, 2, 0);




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
