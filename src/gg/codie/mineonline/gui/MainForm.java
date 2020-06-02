package gg.codie.mineonline.gui;

import gg.codie.mineonline.*;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.utils.JSONUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class MainForm implements IContainerForm {
    private JPanel skinPanel;
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

    List<MinecraftInstall> installs;

    public JPanel getContent() {
        return contentPanel;
    }

    public JPanel getRenderPanel() {
        return skinPanel;
    }

    public MainForm() {
        if (Session.session == null) {
            //FormManager.switchScreen(new LoginForm());
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
                    ArrayList<String> args = new ArrayList();

                    if(installs.size() < 1) {
                        JOptionPane.showMessageDialog(null, "You have no Minecraft configurations.\nPlease add one using the Settings screen.");
                        return;
                    }
                  
                    MinecraftInstall install = installs.get(comboBox1.getSelectedIndex());

                    if(install.jarPath.isEmpty() && !(new File(install.jarPath).exists())) {
                        JOptionPane.showMessageDialog(null, "This configuration has no jar file,\nor it's jar file could not be found.\nPlease check your settings.");
                        return;
                    }

                    Properties.properties.put("lastPlayedIndex", comboBox1.getSelectedIndex());
                    Properties.saveProperties();

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
                    FormManager.singleton.dispose();
                    System.exit(0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to launch game.");
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Session.session.logout();
                //FormManager.switchScreen(new LoginForm());
            }
        });

        changeSkinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FormManager.switchScreen(new ChangeSkinForm());
            }
        });

        joinServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FormManager.switchScreen(new JoinServerForm());
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
