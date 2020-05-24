package gg.codie.mineonline.gui.rendering;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class DebugCamera extends Camera {

    @Override
    public void move() {
//        if(Mouse.isButtonDown(0)) {
//            yaw += Mouse.getDX() * 0.3f;
//        }


        if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
            position.z -= 0.2f;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
            position.x += 0.2f;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
            position.x -= 0.2f;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
            position.z += 0.2f;
        }
    }
}
