package gg.codie.mineonline.gui.rendering;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.*;
import org.lwjgl.*;

public class SimpleOGLRenderer {

    public SimpleOGLRenderer() {
        try {
            Display.setDisplayMode(new DisplayMode(854, 480));
            Display.setTitle("Player Model Render Test");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        // OpenGL
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 854, 480, 0, 1, -1);


        // Render Loop
        while(!Display.isCloseRequested()) {
            Display.update();
            Display.sync(60);
        }

        Display.destroy();
    }

    public static void main(String[] args) {
        new PlayerRendererText();
    }

}
