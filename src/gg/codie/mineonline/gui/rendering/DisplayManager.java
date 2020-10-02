package gg.codie.mineonline.gui.rendering;

import gg.codie.minecraft.client.Options;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DisplayManager {

    static {
        if (new File(LauncherFiles.MINECRAFT_OPTIONS_PATH).exists()) {
            try {
                Settings.loadSettings();
                guiScale = Settings.settings.optInt(Settings.GUI_SCALE, 0);
            } catch (Exception ex) {
                guiScale = 0;
            }
        }
    }

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

    public static int getDefaultScaleXBuffer() {
        return (int)(getXBuffer() * (1 / getScale()));
    }

    public static int getDefaultScaleYBuffer() {
        return (int)(getYBuffer() * (1 / getScale()));
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

    public static int getGuiScale() {
        return guiScale;
    }

    public static void setGuiScale(int guiScale) {
        DisplayManager.guiScale = guiScale;

        try {
            if (!new File(LauncherFiles.MINECRAFT_OPTIONS_PATH).exists()) {
                Files.createFile(Paths.get(LauncherFiles.MINECRAFT_OPTIONS_PATH));
            }

            Settings.settings.put(Settings.GUI_SCALE, guiScale);
            Settings.saveSettings();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static int guiScale;

    public static double getScale() {
        double xScale = (double) Display.getWidth() / DEFAULT_WIDTH;
        double yScale = (double) Display.getHeight() / DEFAULT_HEIGHT;

        double scale = yScale;

        if(xScale < yScale)
            scale = xScale;

        if(guiScale == 1 && scale > 0.5) {
            return 0.5;
        } else if(guiScale == 2 && scale > 1) {
            return 1;
        } else if (guiScale == 3 && scale > 1.5) {
            return 1.5;
        }

        // Scale is rounded down to nearest half.
        // Easiest way to do this is double, floor, half.
        scale = scale * 2;
        scale = Math.floor(scale);
        scale = scale / 2;

        // Don't allow 0 scale.
        if (scale < 0.5)
            scale = 0.5;

        return scale;
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
        canvas.setPreferredSize(new Dimension(width, height));
        frame.pack();

        if(Display.isCreated()) {
            System.out.println("Display already active!");
            return;
        }

        ContextAttribs attribs = new ContextAttribs(3,2).withProfileCore(true);

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
