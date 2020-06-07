package gg.codie.mineonline.gui.rendering;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class DisplayManager {

    public static int getDefaultWidth() {
        return DEFAULT_WIDTH;
    }

    public static float scaledWidth(float width) {
        return width * (float)getScale();
    }

    public static int getDefaultHeight() {
        return DEFAULT_HEIGHT;
    }

    public static float scaledHeight(float height) {
        return height * (float)getScale();
    }

    public static int getXBuffer() {
        return (int)(Display.getWidth() - DisplayManager.scaledWidth(DisplayManager.getDefaultWidth())) / 2;
    }

    public static int getYBuffer() {
        return (int)(Display.getHeight() - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight())) / 2;
    }

    public static boolean isTall() {
        return (double)Display.getWidth() / Display.getHeight() > DEFAULT_ASPECT;
    }

    public static boolean isWide() {
        return (double)Display.getWidth() / Display.getHeight() <= DEFAULT_ASPECT;
    }

//    public static float getWidthScale() {
//        float multiplier = isTall()
//                ? (float)Display.getWidth() / DEFAULT_WIDTH
//                : (float)Display.getHeight() / DEFAULT_HEIGHT;
//
//        return multiplier;
//    }
//
//    public static float getHeightScale() {
//        float multiplier = isTall()
//                ? (float)Display.getWidth() / DEFAULT_WIDTH
//                : (float)Display.getHeight() / DEFAULT_HEIGHT;
//
//        return multiplier;
//    }

    public static double getScale() {
        //return 1.25;
        double xScale = (double) Display.getWidth() / DEFAULT_WIDTH;
        double yScale = (double) Display.getHeight() / DEFAULT_HEIGHT;

        if(xScale < yScale)
            return xScale;
        else
            return yScale;
    }

    private static final double DEFAULT_ASPECT = 1.77916666667;
    private static final int DEFAULT_WIDTH = 854;
    private static final int DEFAULT_HEIGHT = 480;
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
        if (frame != null) {
            System.out.println("Display already initiated.");
        }
        frame = new Frame("MineOnline");
        canvas = new Canvas();
        frame.setBackground(Color.black);
        canvas.setBackground(Color.black);
        frame.setLayout(new BorderLayout());
        frame.add(canvas, "Center");
        canvas.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(frame.getInsets().left + 10,frame.getInsets().top + 10));
        frame.addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                Dimension d=frame.getSize();
                Dimension minD=frame.getMinimumSize();
                if(d.width<minD.width)
                    d.width=minD.width;
                if(d.height<minD.height)
                    d.height=minD.height;
                frame.setSize(d);
            }
        });

        Image img = Toolkit.getDefaultToolkit().getImage(DisplayManager.class.getResource("/img/favicon.png"));
        frame.setIconImage(img);

//        frame.setSize(DisplayManager.getDefaultWidth() + frame.getInsets().left + frame.getInsets().right, DisplayManager.getDefaultHeight() + frame.getInsets().left + frame.getInsets().right);
//        frame.pack();
//        frame.setDefaultLookAndFeelDecorated(true)
    }

    public static void createDisplay() {
        createDisplay(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static void createDisplay(int width, int height) {

        if(Display.isCreated()) {
            System.out.println("Display already active!");
            return;
        }

        ContextAttribs attribs = new ContextAttribs(3,2).withProfileCompatibility(true);

        try {
            Display.setParent(canvas);
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.create(new PixelFormat(), attribs);
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
        //frame.dispose();
        //frame.setVisible(false);
    }

    public static void fullscreen(boolean on) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice dev = env.getDefaultScreenDevice();

        if(on) {
            dev.setFullScreenWindow(frame);
        } else {
            dev.setFullScreenWindow(null);
        }
    }

}
