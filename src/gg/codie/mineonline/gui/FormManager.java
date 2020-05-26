package gg.codie.mineonline.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.Properties;
import gg.codie.mineonline.Proxy;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class FormManager {
    static JFrame singleton;
    private static Canvas glCanvas = new Canvas();

    static StaticShader shader;
    static gg.codie.mineonline.gui.rendering.Renderer renderer;
    static Loader loader;
    static GameObject playerPivot;
    static PlayerGameObject playerGameObject;
    static Camera camera;

    public static void main(String[] args) throws Exception {
        LibraryManager.extractLibraries();
        LibraryManager.updateClasspath();
        LibraryManager.updateNativesPath();

        Properties.loadProperties();

        Proxy.launchProxy();

        JFrame frame = new JFrame();
        frame.setVisible(true);

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                DisplayManager.closeDisplay();
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

        singleton = frame;

        frame.setSize(new Dimension(845, 476));
        frame.setLocationRelativeTo(null);

        switchScreen(new LoginForm());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().setPreferredSize(new Dimension(845, 476));
        frame.pack();

        frame.setLocationRelativeTo(null);

        frame.setResizable(false);
        frame.setVisible(true);

        glCanvas.setIgnoreRepaint(true);

        try {
            Display.setParent(glCanvas);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    public static void switchScreen(IContainerForm containerForm) {
        singleton.getContentPane().setVisible(false);
        singleton.setContentPane(containerForm.getContent());
        singleton.getContentPane().setVisible(true);

        JPanel renderPanel = containerForm.getRenderPanel();

        if(renderPanel != null) {
            renderPanel.add(glCanvas, new GridConstraints());
            glCanvas.setSize(renderPanel.getSize());

            if(!gamePrepared) {
                gamePrepare.run();
            } else {
                try {
                    Display.setParent(glCanvas);
                } catch (Exception e) {}
                DisplayManager.createDisplay(glCanvas.getSize().width, glCanvas.getSize().height);
            }
            EventQueue.invokeLater(gameMainLoop);
        }
    }


    static boolean gamePrepared;
    public static Runnable gamePrepare = new Runnable() {
        public void run() {
            try {
                Display.setParent(glCanvas);
            } catch (Exception e) {}
            DisplayManager.createDisplay(glCanvas.getSize().width, glCanvas.getSize().height);

            gamePrepared = true;

            shader = new StaticShader();
            renderer = new Renderer(shader);

            loader = new Loader();

            playerPivot = new GameObject("player_origin", new Vector3f(0, 0, -40), new Vector3f(0, 25, 0), new Vector3f(1, 1, 1));

            playerGameObject = new PlayerGameObject("player", loader, shader, new Vector3f(0, -16, 0), new Vector3f(), new Vector3f(1, 1, 1));

            playerPivot.addChild(playerGameObject);

            try {
                playerGameObject.setSkin(Paths.get(LauncherFiles.CACHED_SKIN_PATH).toUri().toURL());
                playerGameObject.setCloak(Paths.get(LauncherFiles.CACHED_CLOAK_PATH).toUri().toURL());
            } catch (MalformedURLException mx) {

            }

            camera = new Camera();
        }
    };



    public static Runnable gameMainLoop = new Runnable() {
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
    };

}
