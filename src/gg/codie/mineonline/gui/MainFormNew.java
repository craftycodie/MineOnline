package gg.codie.mineonline.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Properties;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.animation.IPlayerAnimation;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainFormNew extends JFrame {
    private JPanel skinPanel;
    private Canvas glCanvas = new Canvas();
    private JPanel contentPanel;
    private JButton joinServerButton;
    private JButton changeSkinButton;
    private JButton settingsButton;
    private JButton logoutButton;
    private JComboBox comboBox1;
    private JButton playButton;
    private JLabel logolabel;
    private JLabel playerName;

    StaticShader shader;
    gg.codie.mineonline.gui.rendering.Renderer renderer;
    Loader loader;
    GameObject playerPivot;
    PlayerGameObject playerGameObject;
    Session session;
    Camera camera;
    IPlayerAnimation playerAnimation;

    boolean closing;

    public static void main(String[] args) throws LWJGLException {
        Properties.loadProperties();

        JFrame frame = new MainFormNew();
        frame.setVisible(true);
    }

    public Runnable gamePrepare = new Runnable() {
        public void run() {
            setVisible(true);
            DisplayManager.createDisplay(glCanvas.getSize().width, glCanvas.getSize().height);

            shader = new StaticShader();
            renderer = new Renderer(shader);

            loader = new Loader();

            playerPivot = new GameObject("player_origin", new Vector3f(0, -2    , -40), new Vector3f(0, 30, 0), new Vector3f(1, 1, 1));

            playerGameObject = new PlayerGameObject("player", loader, shader, new Vector3f(0, -16, 0), new Vector3f(), new Vector3f(1, 1, 1));

            playerPivot.addChild(playerGameObject);

            playerGameObject.setSkin(LauncherFiles.CACHED_SKIN_PATH);
            playerGameObject.setCloak(LauncherFiles.CACHED_CLOAK_PATH);

            camera = new Camera();

            playerAnimation = new IdlePlayerAnimation();
            playerAnimation.reset(playerGameObject);
        }
    };



    public Runnable gameMainLoop = new Runnable() {
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

            if(!closing) {
                EventQueue.invokeLater(this);
            }
        }
    };

    public MainFormNew() throws LWJGLException {

        super("MineOnline Launcher");

        session = new Session("codie");

        ImageIcon icon = new ImageIcon("res/mineonlinelogo.png");
        logolabel.setIcon(icon);
//        imagePanel1.setPath("res/mineonlinelogo.png");
//        imagePanel1.setSize(new Dimension(400, 50));

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                closing = true;
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(contentPanel);

//        pack(); // Need this, otherwise insets() show as 0.
        contentPanel.setPreferredSize(new Dimension(845, 476));
        pack();

        playerName.setText(session.getUsername());

        //setSize(854 + getInsets().left , 480 + getInsets().top);
        setResizable(false);
        setVisible(true);

        //PlayerModelCanvas canvas = new PlayerModelCanvas();

        //skinPanel.add(canvas, new GridConstraints());

        EventQueue.invokeLater(gamePrepare);
        EventQueue.invokeLater(gameMainLoop);

        glCanvas.setIgnoreRepaint(true);

        skinPanel.add(glCanvas, new GridConstraints());
        glCanvas.setSize(skinPanel.getSize());

        try {
            Display.setParent(glCanvas);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        //JFrame frame = new JFrame("AWTGLCanvas - multisampling");
        //frame.setPreferredSize(new Dimension(640, 480));
        //frame.add(skinPanel, new GridConstraints());
        //frame.pack();

        //frame.setVisible(true);
    }
}
