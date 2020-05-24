package gg.codie.mineonline.gui.rendering;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.*;

import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.rendering.animation.IPlayerAnimation;
import gg.codie.mineonline.gui.rendering.animation.WalkPlayerAnimation;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

@SuppressWarnings("serial")
public class GLInputTest extends JFrame implements MouseListener, MouseMotionListener, KeyListener {

    private GLWindow canvas;
    private final JTextPane textPane = new JTextPane();

    public GLInputTest() throws LWJGLException {
        setTitle("Keyboard & Mouse Input Example");
        setSize(640, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        canvas = new GLWindow();
        add(canvas);

        setVisible(true);

        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);

        canvas.addKeyListener(this);
        canvas.requestFocus(); // Focus for Keyboard Events

        new Thread() {
            public void run() {
                for (;;) {
                    canvas.repaint();

                    try {
                        sleep(20);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }.start();
    }

    class GLWindow extends AWTGLCanvas {

        StaticShader shader;
        Renderer renderer;
        Loader loader;
        GameObject playerPivot;
        PlayerGameObject playerGameObject;
        Session session;
        Camera camera;
        IPlayerAnimation playerAnimation;

        public GLWindow() throws LWJGLException {
            super();
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

        protected void resizeGL(int w, int h) {
            // setup viewport, projection etc.:
//            GL11.glMatrixMode(GL11.GL_PROJECTION);
//            GL11.glLoadIdentity();
//            GL11.glOrtho(0.0, this.width(), 0.0, this.height(), -1.0, 1.0);
//            GL11.glMatrixMode(GL11.GL_MODELVIEW);
//            GL11.glLoadIdentity();
//            GL11.glViewport(0, 0, this.width(), this.height());
        }

        public void paintGL() {

                renderer.prepare();
                // Camera roll lock.
                // Broken and not necessary.

//            if(playerPivot.getLocalRotation().z > 0) {
//                playerPivot.increaseRotation(new Vector3f(0, 0, -playerPivot.getLocalRotation().z));
//            }

                if(Mouse.isButtonDown(0)) {
                    Vector3f currentRotation = playerPivot.getLocalRotation();
                    Vector3f rotation = new Vector3f();

                    // Camera pitch rotation with lock.
                    // Currently broken.

//                float dy = Mouse.getDY();

//                if(currentRotation.x + (dy * -0.3f) > 30) {
//                    rotation.x = 30 - currentRotation.x;
//                } else if(currentRotation.x + (dy * -0.3f) < -30) {
//                    rotation.x = -30 - currentRotation.x;
//                } else {
//                    rotation.x = dy * -0.3f;
//                }

                    rotation.y = (Mouse.getDX() * 0.5f);

//                System.out.println(rotation.toString());

                    playerPivot.increaseRotation(rotation);
                }

                playerGameObject.update();

                playerAnimation.animate(playerGameObject);

                camera.move();

                shader.start();
                shader.loadViewMatrix(camera);

                renderer.render(playerGameObject, shader);

                shader.stop();

                DisplayManager.updateDisplay();

        }
    }

    public static void main(String[] args) throws LWJGLException {
        new GLInputTest();
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getID()==MouseEvent.MOUSE_CLICKED) {
            switch (e.getButton()) {
                case 1:
                    System.out.println("Left Mouse Button");
                    break;
                case 2:
                    System.out.println("Middle Mouse Button");
                    break;
                case 3:
                    System.out.println("Right Mouse Button");
                    break;
            }
        }
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                System.out.println("SPACE pressed");
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                System.out.println("SPACE released");
                break;
        }
    }

    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar()=='a') {
            System.out.println("a typed");
        } else if (e.getKeyChar()=='A') {
            System.out.println("A typed");
        }
    }

}