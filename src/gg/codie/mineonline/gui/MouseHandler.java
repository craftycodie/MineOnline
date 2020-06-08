package gg.codie.mineonline.gui;

import org.lwjgl.input.Mouse;

public class MouseHandler {

    public static boolean isMouseDown() {
        return mouseWasDown;
    }

    public static boolean didClick() {
        return didClick;
    }

    static boolean mouseWasDown = false;
    static boolean didClick = false;
    public static void update() {

        if(!Mouse.isButtonDown(0) && mouseWasDown) {
            mouseWasDown = false;
        }

        if (mouseWasDown || !Mouse.isButtonDown(0)) {
            didClick = false;
            return;
        }

        if(Mouse.isButtonDown(0) && !mouseWasDown) {
            mouseWasDown = true;
            didClick = true;
        }

    }

}
