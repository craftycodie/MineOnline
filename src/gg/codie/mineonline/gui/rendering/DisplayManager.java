package gg.codie.mineonline.gui.rendering;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class DisplayManager {
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

    public static void checkGLError(String location)
    {
        int i = GL11.glGetError();
        if(i != 0)
        {
            String errorString = GLU.gluErrorString(i);
            System.out.println("########## GL ERROR ##########");
            System.out.println((new StringBuilder()).append("@ ").append(location).toString());
            System.out.println((new StringBuilder()).append(i).append(": ").append(errorString).toString());
        }
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

        try {
            Display.setParent(canvas);
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, width, height);

        frame.setVisible(true);

        checkGLError("display create");
    }

    public static void updateDisplay() {
        Display.sync(FPS);
        Display.update();

        checkGLError("display update");
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
