package gg.codie.mineonline.gui.rendering;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.PixelFormat;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluOrtho2D;

public class PlayerModelCanvas extends AWTGLCanvas {

    public PlayerModelCanvas() throws LWJGLException {
        super(new PixelFormat(8, 8, 0, 4));
    }

    @Override
    public void paintGL() {
        try {
            glViewport(0, 0, getWidth(), getHeight());
            glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            gluOrtho2D(0.0f, (float) getWidth(), 0.0f, (float) getHeight());
            glMatrixMode(GL_MODELVIEW);
            glPushMatrix();
            glColor3f(1f, 1f, 0f);
            glTranslatef(getWidth() / 2.0f, getHeight() / 2.0f, 0.0f);
            glRotatef(51f, 0f, 0f, 1.0f);
            glRectf(-50.0f, -50.0f, 50.0f, 50.0f);
            glPopMatrix();
            swapBuffers();

//            PlayerGameObject playerModel = new PlayerGameObject(0, 0);
//            playerModel.render();

        } catch (LWJGLException ex) {
            ex.printStackTrace();
        }
    }
}