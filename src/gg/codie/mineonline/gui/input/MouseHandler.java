package gg.codie.mineonline.gui.input;

import org.lwjgl.input.Mouse;

public class MouseHandler {

    @Deprecated
    public static boolean isMouseLeftDown() {
        return mouseLeftWasDown;
    }

    @Deprecated
    public static boolean didLeftClick() {
        return didLeftClick;
    }

    public static boolean isButtonDown(int button) {
        switch (button) {
            case 1:
                return mouseRightWasDown;
            case 0:
            default:
                return mouseLeftWasDown;
        }
    }

    public static boolean didClick(int button) {
        switch (button) {
            case 1:
                return didRightClick;
            case 0:
            default:
                return didLeftClick;
        }
    }

    public static boolean didClickCooldown(int button, long cooldownMS) {
        switch (button) {
            case 1:
                return System.currentTimeMillis() > lastRightClickMillis + cooldownMS;
            case 0:
            default:
                return System.currentTimeMillis() > lastLeftClickMillis + cooldownMS;
        }
    }

    static boolean mouseLeftWasDown = false;
    static boolean didLeftClick = false;
    static long lastLeftClickMillis = 0L;

    static boolean mouseRightWasDown = false;
    static boolean didRightClick = false;
    static long lastRightClickMillis = 0L;

    public static void update() {

        if(!Mouse.isButtonDown(0) && mouseLeftWasDown) {
            mouseLeftWasDown = false;
        }

        if (mouseLeftWasDown || !Mouse.isButtonDown(0)) {
            didLeftClick = false;
        }
        else if(Mouse.isButtonDown(0) && !mouseLeftWasDown) {
            mouseLeftWasDown = true;
            didLeftClick = true;
            lastLeftClickMillis = System.currentTimeMillis();
        }



        if(!Mouse.isButtonDown(1) && mouseRightWasDown) {
            mouseRightWasDown = false;
        }

        if (mouseRightWasDown || !Mouse.isButtonDown(1)) {
            didRightClick = false;
        }
        else if(Mouse.isButtonDown(1) && !mouseRightWasDown) {
            mouseRightWasDown = true;
            didRightClick = true;
            lastRightClickMillis = System.currentTimeMillis();
        }

    }

}
