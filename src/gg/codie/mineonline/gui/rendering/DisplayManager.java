package gg.codie.mineonline.gui.rendering;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

public class DisplayManager {

    private static final int WIDTH = 854;
    private static final int HEIGHT = 480;
    private static final int FPS = 120;

    public static void createDisplay() {

        ContextAttribs attribs = new ContextAttribs(3,2).withForwardCompatible(true).withProfileCore(true);

        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(32, 0, 24, 0,  0), attribs);
            Display.setTitle("Skin Renderer Test");
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, WIDTH, HEIGHT);

    }

    public static void updateDisplay() {

        Display.sync(FPS);
        Display.update();

    }

    public static void closeDisplay() {

        Display.destroy();

    }

}
