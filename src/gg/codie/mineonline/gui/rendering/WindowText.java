package gg.codie.mineonline.gui.rendering;

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

        //PlayerGameObject playerModel = new PlayerGameObject(0, 0);


//        float[] vertices = {
//                -0.5f,0.5f,-0.5f,
//                -0.5f,-0.5f,-0.5f,
//                0.5f,-0.5f,-0.5f,
//                0.5f,0.5f,-0.5f,
//
//                -0.5f,0.5f,0.5f,
//                -0.5f,-0.5f,0.5f,
//                0.5f,-0.5f,0.5f,
//                0.5f,0.5f,0.5f,
//
//                0.5f,0.5f,-0.5f,
//                0.5f,-0.5f,-0.5f,
//                0.5f,-0.5f,0.5f,
//                0.5f,0.5f,0.5f,
//
//                -0.5f,0.5f,-0.5f,
//                -0.5f,-0.5f,-0.5f,
//                -0.5f,-0.5f,0.5f,
//                -0.5f,0.5f,0.5f,
//
//                -0.5f,0.5f,0.5f,
//                -0.5f,0.5f,-0.5f,
//                0.5f,0.5f,-0.5f,
//                0.5f,0.5f,0.5f,
//
//                -0.5f,-0.5f,0.5f,
//                -0.5f,-0.5f,-0.5f,
//                0.5f,-0.5f,-0.5f,
//                0.5f,-0.5f,0.5f
//
//        };

        float[] textureCoords = {

                0,0,
                0,0.5f,
                0.5f,0.5f,
                0.5f,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0,
                0,0,
                0,1,
                1,1,
                1,0


        };

//        int[] indices = {
//                0,1,3,
//                3,1,2,
//                4,5,7,
//                7,5,6,
//                8,9,11,
//                11,9,10,
//                12,13,15,
//                15,13,14,
//                16,17,19,
//                19,17,18,
//                20,21,23,
//                23,21,22
//
//        };



//        RawModel testModel = loader.loadBoxToVAO(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(0.5f, 0.5f, 0.5f), textureCoords);
//        ModelTexture texture = new ModelTexture(loader.loadTexture("missing"));
//        TexturedModel texturedModel = new TexturedModel(testModel, texture);

        PlayerGameObject playerGameObject = new PlayerGameObject(loader, shader, new Vector3f(0, -24, -75), 0, 0, 0, 1 );

        Camera camera = new Camera();


//        RawModel headModel = loader.loadBoxToVAO(playerModel.playerHead.begin, playerModel.playerHead.end, textureCoords);
//        ModelTexture headTexture = new ModelTexture(loader.loadTexture("missing"));
//        TexturedModel texturedHead =  new TexturedModel(headModel, headTexture);
//        //GameObject headEntity = new GameObject(texturedHead, new Vector3f(0, 0, -25), 90, 0, 0, 1);


//        for (Vector3f[] box : playerModel.getBoxVertices()) {
//            TexturedModel boxTexturedModel = new TexturedModel(loader.loadBoxToVAO(box[0], box[1], textureCoords), texture);
//            PlayerGameObject boxEntity = new PlayerGameObject(boxTexturedModel, new Vector3f(0, 0, -25), 0, 0, 0, 1);
//            playerModelBoxes.add(boxEntity);
//        }

        // Game Loop
        while(!Display.isCloseRequested()) {
            //entity.increaseRotation(0, 1, 0);
            //entity.increasePosition(0, 0, -0.05f);

            if(Mouse.isButtonDown(0)) {
                playerGameObject.increaseRotation(Mouse.getDY() * -0.3f, Mouse.getDX() * 0.3f, 0);
            }

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
