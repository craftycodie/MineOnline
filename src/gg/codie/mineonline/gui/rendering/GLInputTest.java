package gg.codie.mineonline.gui.rendering;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("serial")
public class GLInputTest extends JFrame implements MouseListener, MouseMotionListener, KeyListener {

    private GLWindow canvas;

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

        public GLWindow() throws LWJGLException {
            super();
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
            try {
                makeCurrent();
//                GL11.glViewport(0, 0, getWidth(), getHeight());
//                GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
//
//                // clear the screen
//                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

                // center square according to screen size
                GL11.glPushMatrix();
                GL11.glTranslatef(100/ 2, 200 / 2, 0.0f);

                // rotate square according to angle
                GL11.glRotatef(0, 0, 0, 1.0f);

                // render the square
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2i(-50, -50);
                GL11.glVertex2i(50, -50);
                GL11.glVertex2i(50, 50);
                GL11.glVertex2i(-50, 50);
                GL11.glEnd();

                GL11.glPopMatrix();

                swapBuffers();
            } catch (LWJGLException e) {
                throw new RuntimeException(e);
            }
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