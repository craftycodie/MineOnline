package gg.codie.mineonline.gui.rendering;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.GridLayout;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;



public class Window {
    private JFrame frame;
    private Canvas glCanvas = new Canvas();
    private final JPanel panel1 = new JPanel();
    private final JPanel panel2 = new JPanel();
    private final JTextPane textPane = new JTextPane();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Window window = new Window();
                    window.frame.setVisible(true);
                    Display.create();


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        EventQueue.invokeLater(new Runnable() {
            public void run() {

                glMatrixMode(GL_PROJECTION);
                glLoadIdentity();
                glOrtho(0, 640, 480, 0, 1,  -1);
                glMatrixMode(GL_MODELVIEW);

                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                GL11.glLoadIdentity();

                GL11.glTranslatef(0f,0.0f,-7f);
                GL11.glRotatef(45f,0.0f,1.0f,0.0f);
                GL11.glColor3f(0.5f,0.5f,1.0f);

                glBegin(GL_QUADS);
                //GL11.GL_QUADS);
                GL11.glColor3f(1.0f,1.0f,0.0f);
                GL11.glVertex3f( 1.0f, 1.0f,-1.0f);
                GL11.glVertex3f(-1.0f, 1.0f,-1.0f);
                GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
                GL11.glVertex3f( 1.0f, 1.0f, 1.0f);
                GL11.glColor3f(1.0f,0.5f,0.0f);
                GL11.glVertex3f( 1.0f,-1.0f, 1.0f);
                GL11.glVertex3f(-1.0f,-1.0f, 1.0f);
                GL11.glVertex3f(-1.0f,-1.0f,-1.0f);
                GL11.glVertex3f( 1.0f,-1.0f,-1.0f);
                GL11.glColor3f(1.0f,0.0f,0.0f);
                GL11.glVertex3f( 1.0f, 1.0f, 1.0f);
                GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
                GL11.glVertex3f(-1.0f,-1.0f, 1.0f);
                GL11.glVertex3f( 1.0f,-1.0f, 1.0f);
                GL11.glColor3f(1.0f,1.0f,0.0f);
                GL11.glVertex3f( 1.0f,-1.0f,-1.0f);
                GL11.glVertex3f(-1.0f,-1.0f,-1.0f);
                GL11.glVertex3f(-1.0f, 1.0f,-1.0f);
                GL11.glVertex3f( 1.0f, 1.0f,-1.0f);
                GL11.glColor3f(0.0f,0.0f,1.0f);
                GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
                GL11.glVertex3f(-1.0f, 1.0f,-1.0f);
                GL11.glVertex3f(-1.0f,-1.0f,-1.0f);
                GL11.glVertex3f(-1.0f,-1.0f, 1.0f);
                GL11.glColor3f(1.0f,0.0f,1.0f);
                GL11.glVertex3f( 1.0f, 1.0f,-1.0f);
                GL11.glVertex3f( 1.0f, 1.0f, 1.0f);
                GL11.glVertex3f( 1.0f,-1.0f, 1.0f);
                GL11.glVertex3f( 1.0f,-1.0f,-1.0f);
                glEnd();

//
//                glBegin(GL_POINTS);
//
//                PlayerGameObject playerModel = new PlayerGameObject(0, 0);
//                playerModel.render();
//
//                glEnd();


                Display.update();

                EventQueue.invokeLater(this);
            }
        });
    }

    /**
     * Create the application.
     */
    public Window() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.addWindowListener(new FrameWindowListener());
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout(0, 2, 0, 0));
        frame.getContentPane().add(panel1);
        panel1.setLayout(null);
        textPane.setBounds(10, 5, 124, 20);

        panel1.add(textPane);
        frame.getContentPane().add(panel2);
        panel2.setLayout(new BorderLayout(0, 0));

        glCanvas.setIgnoreRepaint(true);

        panel2.add(glCanvas);
        try {
            Display.setParent(glCanvas);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    private class FrameWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            Display.destroy();
        }
    }
}