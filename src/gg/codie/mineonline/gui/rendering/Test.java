package gg.codie.mineonline.gui.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluOrtho2D;

import gg.codie.mineonline.MineOnlineLauncherFrame;
import gg.codie.mineonline.Properties;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test {



    public static void main(String[] args) throws LWJGLException {

        Display.setDisplayMode(new DisplayMode(480, 600));
        Display.setTitle("Texture Demo");
        Display.create();

        // init OpenGL
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        glFrustum(-0.01, 0.01, -0.01 / aspectRatio, 0.01 / aspectRatio, 0.01, 100000);
        glMatrixMode(GL_MODELVIEW);



        //Draw shit

        glBegin(GL_POINTS);


        glEnd();


        glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        // General
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        // Light
        // various light configs


        Properties.loadProperties();

        JFrame frame = new SkinFormTest();
        frame.setVisible(true);

    }
}