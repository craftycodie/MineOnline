package gg.codie.mineonline.gui.rendering;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;

import java.awt.*;

public class DisplayManager {

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    private static final int WIDTH = 854;
    private static final int HEIGHT = 480;
    private static final int FPS = 120;

    public static Frame getFrame() {
        return frame;
    }

    public static Canvas getCanvas() {
        return canvas;
    }

    private static Frame frame = null;
    private static Canvas canvas = null;

    public static void init() {
        frame = new Frame("MineOnline");
        canvas = new Canvas();
        frame.setLayout(new BorderLayout());
        frame.add(canvas, "Center");
        canvas.setPreferredSize(new Dimension(854, 480));
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    public static void createDisplay() {
        createDisplay(WIDTH, HEIGHT);
    }

    public static void createDisplay(int width, int height) {

        if(Display.isCreated()) {
            System.out.println("Display already active!");
            return;
        }

        //ContextAttribs attribs = new ContextAttribs(3,2).withProfileCompatibility(true);

        try {
            Display.setParent(canvas);
            Display.setDisplayMode(new DisplayMode(854, 480));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, width, height);

        frame.setVisible(true);

    }

    public static void updateDisplay() {

        Display.sync(FPS);
        Display.update();

    }

    public static void closeDisplay() {

        Display.destroy();
        //frame.setVisible(false);

    }

}
