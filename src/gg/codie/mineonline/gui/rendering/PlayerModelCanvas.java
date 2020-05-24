package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.rendering.animation.IPlayerAnimation;
import gg.codie.mineonline.gui.rendering.animation.WalkPlayerAnimation;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluOrtho2D;

public class PlayerModelCanvas extends AWTGLCanvas {

    static StaticShader shader;
    static Renderer renderer;
    static Loader loader;
    static GameObject playerPivot;
    static PlayerGameObject playerGameObject;
    static Session session;
    static Camera camera;
    static IPlayerAnimation playerAnimation;

    public PlayerModelCanvas() throws LWJGLException {
        super(new PixelFormat(8, 8, 0, 0));

//        Window window = new Window();
//        window..setVisible(true);
        //DisplayManager.createDisplay();

        shader = new StaticShader();
        renderer = new Renderer(shader);

        loader = new Loader();


        playerPivot = new GameObject("player_origin", new Vector3f(-20, 0, -65), new Vector3f(0, 30, 0), new Vector3f(1, 1, 1));

        playerGameObject = new PlayerGameObject("player", loader, shader, new Vector3f(0, -16, 0), new Vector3f(), new Vector3f(1, 1, 1));

        session = new Session("codie");


        playerPivot.addChild(playerGameObject);

        camera = new Camera();

        playerAnimation = new WalkPlayerAnimation();
        playerAnimation.reset(playerGameObject);

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