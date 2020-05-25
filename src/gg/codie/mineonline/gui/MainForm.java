package gg.codie.mineonline.gui;

import gg.codie.mineonline.*;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.animation.IPlayerAnimation;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.mineonline.gui.rendering.shaders.StaticShader;
import gg.codie.utils.JSONUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class MainForm implements IContainerForm {
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
    private JLabel offlineModeLabel;

    StaticShader shader;
    gg.codie.mineonline.gui.rendering.Renderer renderer;
    Loader loader;
    GameObject playerPivot;
    PlayerGameObject playerGameObject;
    Camera camera;
    IPlayerAnimation playerAnimation;

    List<MinecraftInstall> installs;

    boolean closing;

    public JPanel getContent() {
        return contentPanel;
    }

    public JPanel getRenderPanel() {
        return skinPanel;
    }

    public Runnable gamePrepare = new Runnable() {
        public void run() {
            //DisplayManager.createDisplay(glCanvas.getSize().width, glCanvas.getSize().height);

            shader = new StaticShader();
            renderer = new Renderer(shader);

            loader = new Loader();

            playerPivot = new GameObject("player_origin", new Vector3f(0, -2    , -40), new Vector3f(0, 30, 0), new Vector3f(1, 1, 1));

            playerGameObject = new PlayerGameObject("player", loader, shader, new Vector3f(0, -16, 0), new Vector3f(), new Vector3f(1, 1, 1));

            playerPivot.addChild(playerGameObject);

            try {
                playerGameObject.setSkin(Paths.get(LauncherFiles.CACHED_SKIN_PATH).toUri().toURL());
                playerGameObject.setCloak(Paths.get(LauncherFiles.CACHED_CLOAK_PATH).toUri().toURL());
            } catch (MalformedURLException mx) {

            }

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

    public MainForm() {
        if (Session.session == null) {
            FormManager.switchScreen(new LoginForm());
            return;
        }

        if(PlayerGameObject.thePlayer != null) {
            PlayerGameObject.thePlayer.setPlayerAnimation(new IdlePlayerAnimation());
        }

        this.offlineModeLabel.setVisible(false);

        if (!Session.session.isOnline()) {
            this.changeSkinButton.setVisible(false);
            this.joinServerButton.setVisible(false);
            this.offlineModeLabel.setVisible(true);
        }

        ImageIcon icon = new ImageIcon(MainForm.class.getResource("/img/mineonlinelogo.png"));
        logolabel.setIcon(icon);

        contentPanel.setPreferredSize(new Dimension(845, 476));

        playerName.setText(Session.session.getUsername());

        installs = JSONUtils.getMinecraftInstalls(Properties.properties.getJSONArray("minecraftInstalls"));

        for(MinecraftInstall install : installs) {
            comboBox1.addItem(install.getName());
        }

        if (Properties.properties.has("lastPlayedIndex")) {
            int lastPlayed = Properties.properties.getInt("lastPlayedIndex");

            if (lastPlayed < installs.size() - 1) {
                comboBox1.setSelectedIndex(lastPlayed);
            }
        }

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Properties.properties.put("lastPlayedIndex", comboBox1.getSelectedIndex());
                    Properties.saveProperties();

                    ArrayList<String> args = new ArrayList();

                    MinecraftInstall install = installs.get(comboBox1.getSelectedIndex());

                    if(install.jarPath.isEmpty() && !(new File(install.jarPath).exists())) {
                        JOptionPane.showMessageDialog(null, "This configuration has no jar file,\nor it's jar file could not be found.\nPlease check your settings.");
                        return;
                    }

                    if(!install.getMainClass().isEmpty()) {
                        args.add(Session.session.getUsername());

                        if(Session.session.isOnline())
                        {
                            args.add(Session.session.getSessionToken());
                        }

                        MineOnlineLauncher.launch(installs.get(comboBox1.getSelectedIndex()).getJarPath(), ELaunchType.Game, install.getMainClass(),
                                args.toArray(new String[0]),
                                Proxy.getProxyPort());
                    } else {
                        String premiumArgument = "-demo";
                        if(Properties.properties.getBoolean("isPremium")) {
                            premiumArgument = "-paid";
                        }
                        args.add(premiumArgument);

                        args.add("-login");
                        args.add(Session.session.getUsername());

                        if(Session.session.isOnline())
                        {
                            args.add(Session.session.getSessionToken());
                        }

                        MineOnlineLauncher.launch(installs.get(comboBox1.getSelectedIndex()).getJarPath(), ELaunchType.Applet, install.getAppletClass(),
                                args.toArray(new String[0]),
                                Proxy.getProxyPort());
                    }

                    FormManager.singleton.setVisible(false);
                    while(MineOnlineLauncher.gameProcess.isAlive()) {

                    }
                    System.exit(0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to launch game.");
                }
            }
        });

        glCanvas.setIgnoreRepaint(true);


        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Session.session.logout();
                FormManager.switchScreen(new LoginForm());
            }
        });

        changeSkinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FormManager.switchScreen(new ChangeSkinForm());
            }
        });

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FormManager.switchScreen(new SettingsForm());
            }
        });

    }
}
