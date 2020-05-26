package gg.codie.mineonline.gui.rendering;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

public class DisplayManager {

    private static final int WIDTH = 854;
    private static final int HEIGHT = 480;
    private static final int FPS = 120;

    public static void createDisplay() {
        createDisplay(WIDTH, HEIGHT);
    }

    public static void createDisplay(int width, int height) {

        ContextAttribs attribs = new ContextAttribs(3,2).withProfileCompatibility(true);

        try {
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.create(new PixelFormat(32, 0, 24, 0,  0), attribs);
            Display.setTitle("Player Renderer");
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, width, height);

    }

    public static void updateDisplay() {

        Display.sync(FPS);
        Display.update();

    }

    public static void closeDisplay() {

        Display.destroy();

    }

}
