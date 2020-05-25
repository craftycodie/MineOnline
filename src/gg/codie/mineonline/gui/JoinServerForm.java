package gg.codie.mineonline.gui;

import gg.codie.mineonline.*;
import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.mineonline.gui.rendering.PlayerGameObject;
import gg.codie.mineonline.gui.rendering.animation.ClassicWalkPlayerAnimation;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.mineonline.gui.rendering.animation.WalkPlayerAnimation;
import gg.codie.utils.JSONUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JoinServerForm implements IContainerForm {
    private JPanel contentPanell;
    private JPanel skinPanel;
    private JTextField serverIPTextField;
    private JTextField serverPortTextField;
    private JButton cancelButton;
    private JLabel logolabel;
    private JComboBox comboBox1;
    private JButton playButton;

    List<MinecraftInstall> installs;

    @Override
    public JPanel getContent() {
        return contentPanell;
    }

    @Override
    public JPanel getRenderPanel() {
        return skinPanel;
    }

    JoinServerForm() {
        if(PlayerGameObject.thePlayer != null) {
            PlayerGameObject.thePlayer.setPlayerAnimation(new IdlePlayerAnimation());
        }

        ImageIcon icon = new ImageIcon(ChangeSkinForm.class.getResource("/img/mineonlinelogo.png"));
        logolabel.setIcon(icon);

        contentPanell.setPreferredSize(new Dimension(845, 476));

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FormManager.switchScreen(new MainForm());
            }
        });

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

        if (Properties.properties.has("lastServerIP")) {
            serverIPTextField.setText(Properties.properties.getString("lastServerIP"));
        }

        if (Properties.properties.has("lastServerPort")) {
            serverPortTextField.setText(Properties.properties.getString("lastServerPort"));
        }

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<String> args = new ArrayList();

                    String port = serverPortTextField.getText();
                    if(port.isEmpty()) {
                        port = "25565";
                    }

                    String mppass = MinecraftAPI.getMpPass(Session.session.getSessionToken(), serverIPTextField.getText(), port);

                    if(serverIPTextField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter a server IP address or domain.");
                        return;
                    }

                    if(installs.size() < 1) {
                        JOptionPane.showMessageDialog(null, "You have no Minecraft configurations.\nPlease add one using the Settings screen.");
                        return;
                    }

                    Properties.properties.put("lastServerIP", serverIPTextField.getText());
                    Properties.properties.put("lastServerPort", port);
                    Properties.saveProperties();

                    MinecraftInstall install = installs.get(comboBox1.getSelectedIndex());

                    if(install.jarPath.isEmpty() && !(new File(install.jarPath).exists())) {
                        JOptionPane.showMessageDialog(null, "This configuration has no jar file,\nor it's jar file could not be found.\nPlease check your settings.");
                        return;
                    }

                    Properties.properties.put("lastPlayedIndex", comboBox1.getSelectedIndex());
                    Properties.saveProperties();

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

                    args.add("-server");
                    args.add(serverIPTextField.getText());
                    args.add(port);

                    if(mppass != null)
                    {
                        args.add(mppass);
                    }

                    MineOnlineLauncher.launch(installs.get(comboBox1.getSelectedIndex()).getJarPath(), ELaunchType.Applet, install.getAppletClass(),
                            args.toArray(new String[0]),
                            Proxy.getProxyPort());

                    FormManager.singleton.setVisible(false);
                    while(MineOnlineLauncher.gameProcess.isAlive()) {

                    }
                    FormManager.singleton.dispose();
                    System.exit(0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to launch game.");
                }
            }
        });

    }
}
