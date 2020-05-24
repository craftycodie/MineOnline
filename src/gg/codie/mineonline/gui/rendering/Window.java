package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.rendering.animation.IPlayerAnimation;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.mineonline.gui.rendering.animation.WalkPlayerAnimation;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.GridLayout;


import javax.swing.*;


public class Window {
    private JFrame frame;
    private Canvas glCanvas = new Canvas();
    private final JPanel panel1 = new JPanel();
    private final JPanel panel2 = new JPanel();
    private final JTextPane textPane = new JTextPane();
    private final JButton button = new JButton();

    static StaticShader shader;
    static Renderer renderer;
    static Loader loader;
    static GameObject playerPivot;
    static PlayerGameObject playerGameObject;
    static Session session;
    static Camera camera;
    static IPlayerAnimation playerAnimation;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Window window = new Window();
                    window.frame.setVisible(true);
                    DisplayManager.createDisplay(854 / 2, 480);

                    shader = new StaticShader();
                    renderer = new Renderer(shader);

                    loader = new Loader();


                    playerPivot = new GameObject("player_origin", new Vector3f(0, 0, -35), new Vector3f(0, 30, 0), new Vector3f(1, 1, 1));

                    playerGameObject = new PlayerGameObject("player", loader, shader, new Vector3f(0, -16, 0), new Vector3f(), new Vector3f(1, 1, 1));

                    session = new Session("codie");


                    playerPivot.addChild(playerGameObject);

                    camera = new Camera();

                    playerAnimation = new IdlePlayerAnimation();
                    playerAnimation.reset(playerGameObject);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        EventQueue.invokeLater(new Runnable() {
            public void run() {
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

                //DisplayManager.updateDisplay();
                Display.update();

                try {
                    Thread.sleep(12);
                } catch (Exception e) {

                }
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
        //frame.setBounds(0, 0, 854, 480);
        frame.setSize(854, 480);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout(0, 2, 0, 0));
        panel1.setLayout(null);
        textPane.setBounds(10, 5, 124, 20);
        button.setBounds(134, 5, 100, 20);
        button.setText("Load Skin");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                session.logout();
                new Session(textPane.getText());
            }
        });


        panel1.add(textPane);
        panel1.add(button);
        frame.getContentPane().add(panel2);
        frame.getContentPane().add(panel1);
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