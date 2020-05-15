package gg.codie.mc;

import gg.codie.utils.FileUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MineOnlineLauncherFrame extends JFrame {
    private JButton launchGameButton;
    private JButton launchAppletButton;
    private JTextField usernameTextField;
    private JButton loginButton;
    private JTextField mppassTextField;
    private JButton getMppassButton;
    private JTextField jarPathTextField;
    private JButton browseButton;
    private JPanel formPanel;
    private JLabel username;
    private JCheckBox useLocalProxyCheckBox;
    private JTextField apiDomainTextField;
    private JTextField serverPortTextField;
    private JTextField sessionIdTextField;
    private JTextField serverIPTextField;
    private JPasswordField passwordField;
    private JCheckBox hasPaidCheckBox;
    private JTextField appletClassNameTextField;
    private JTextField gameClassNameTextField;
    private JCheckBox connectToServerCheckBox;
    private JButton openJoinURLButton;
    private JLabel needAccountLabel;
    private JTextField baseURLTextField;
    private JFileChooser fileChooser = new JFileChooser();

    int proxyPort;

    public MineOnlineLauncherFrame(){
        super("MineOnline Launcher (Prototype)");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(formPanel);
        setSize(600, 575);
        setResizable(false);

        if(useLocalProxyCheckBox.isSelected()) {
            startProxy();
        }

        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                return FileUtils.getExtension(f) != null && FileUtils.getExtension(f).equals("jar");
            }

            @Override
            public String getDescription() {
                return "Minecraft game file (.jar)";
            }
        });

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal =  fileChooser.showOpenDialog(MineOnlineLauncherFrame.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    jarPathTextField.setText(file.getAbsolutePath());
                }
            }
        });

        jarPathTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void findMainClasses() {
                try {
                    appletClassNameTextField.setText("");
                    gameClassNameTextField.setText("");

                    if(jarPathTextField.getText().isEmpty())
                        return;

                    Properties.properties.setProperty("jarFilePath", jarPathTextField.getText());
                    Properties.saveProperties();

                    JarFile jarFile = new JarFile(jarPathTextField.getText());
                    Enumeration allEntries = jarFile.entries();
                    while (allEntries.hasMoreElements()) {
                        JarEntry entry = (JarEntry) allEntries.nextElement();
                        String classCanonicalName = entry.getName();

                        if(!classCanonicalName.contains(".class"))
                            continue;

                        classCanonicalName = classCanonicalName.replace("/", ".");
                        classCanonicalName = classCanonicalName.replace(".class", "");

                        String className = classCanonicalName;
                        if(classCanonicalName.lastIndexOf(".") > -1) {
                            className = classCanonicalName.substring(classCanonicalName.lastIndexOf(".") + 1);
                        }

                        if(className.equals("MinecraftApplet")) {
                            appletClassNameTextField.setText(classCanonicalName);
                        } else if(className.equals("MiencraftLauncher")) {
                            gameClassNameTextField.setText(classCanonicalName);
                        } else if(className.equals("Minecraft")) {
                            gameClassNameTextField.setText(classCanonicalName);
                        }
                    }
                } catch (IOException ex) {

                }
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                findMainClasses();
            }

            public void removeUpdate(DocumentEvent e) {
                findMainClasses();
            }
            public void insertUpdate(DocumentEvent e) {
                findMainClasses();
            }

        });

        launchAppletButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<String> args = new ArrayList();

                    String premiumArgument = "-demo";
                    if(hasPaidCheckBox.isSelected()) {
                        premiumArgument = "-paid";
                    }
                    args.add(premiumArgument);

                    if(!usernameTextField.getText().isEmpty())
                    {
                        args.add("-login");
                        args.add(usernameTextField.getText());

                        if(!sessionIdTextField.getText().isEmpty())
                        {
                            args.add(sessionIdTextField.getText());
                        }
                    }

                    if(connectToServerCheckBox.isSelected() && !serverIPTextField.getText().isEmpty())
                    {
                        args.add("-server");
                        args.add(serverIPTextField.getText());
                        if(!serverPortTextField.getText().isEmpty())
                        {
                            args.add(serverPortTextField.getText());

                            if(!mppassTextField.getText().isEmpty())
                            {
                                args.add(mppassTextField.getText());
                            }
                        }
                    }

                    MineOnlineLauncher.launch(jarPathTextField.getText(), ELaunchType.Applet, appletClassNameTextField.getText(),
                            args.toArray(new String[0]),
                            proxyPort);

                    setVisible(false);
                    while(MineOnlineLauncher.gameProcess.isAlive()) {

                    }
                    System.exit(0);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Failed to launch applet.");
                }
            }
        });

        launchGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<String> args = new ArrayList();

                    if(!usernameTextField.getText().isEmpty())
                    {
                        args.add(usernameTextField.getText());

                        if(!sessionIdTextField.getText().isEmpty())
                        {
                            args.add(sessionIdTextField.getText());
                        }
                    }

                    MineOnlineLauncher.launch(jarPathTextField.getText(), ELaunchType.Game, gameClassNameTextField.getText(),
                            args.toArray(new String[0]),
                            proxyPort);

                    setVisible(false);
                    while(MineOnlineLauncher.gameProcess.isAlive()) {

                    }
                    System.exit(0);
                    setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Failed to launch game.");
                }
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!apiDomainTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty() && passwordField.getPassword().length > 0) {
                    String sessionId = MineOnlineLauncher.login(new String(passwordField.getPassword()));
                    sessionIdTextField.setText(sessionId);
                } else {
                    // show alert
                }
            }
        });

        getMppassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!apiDomainTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty() && !serverIPTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty() && !sessionIdTextField.getText().isEmpty()) {
                    String port = serverPortTextField.getText();
                    if(port.isEmpty())
                        port = "25565";
                    try {
                        String mpPass = MineOnlineLauncher.getMpPass(sessionIdTextField.getText(), serverIPTextField.getText(), port);
                        mppassTextField.setText(mpPass);
                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(null, "Failed to authenticate.\nThis API might not support MineOnline.");
                    }
                } else {
                    // show alert
                }
            }
        });

        useLocalProxyCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(useLocalProxyCheckBox.isSelected()) {
                    Properties.properties.setProperty("useLocalProxy", "true");
                } else {
                    Properties.properties.setProperty("useLocalProxy", "false");
                }
                useLocalProxyUpdated();
                Properties.saveProperties();
            }
        });

        connectToServerCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(connectToServerCheckBox.isSelected()) {
                    Properties.properties.setProperty("joinServer", "true");
                } else {
                    Properties.properties.setProperty("joinServer", "false");
                }
                joinServerUpdated();
                Properties.saveProperties();
            }
        });

        hasPaidCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(hasPaidCheckBox.isSelected()) {
                    Properties.properties.setProperty("isPremium", "true");
                } else {
                    Properties.properties.setProperty("isPremium", "false");
                }
                Properties.saveProperties();
            }
        });


        apiDomainTextField.getDocument().addDocumentListener(new DocumentListener() {
            private void onChange() {
                if(useLocalProxyCheckBox.isSelected())
                    startProxy();
                Properties.properties.setProperty("apiDomainName", apiDomainTextField.getText());
                Properties.saveProperties();
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                onChange();
            }

            public void removeUpdate(DocumentEvent e) {
                onChange();
            }
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }
        });

        usernameTextField.getDocument().addDocumentListener(new DocumentListener() {
            private void onChange() {
                Properties.properties.setProperty("username", usernameTextField.getText());
                Properties.saveProperties();
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                onChange();
            }

            public void removeUpdate(DocumentEvent e) {
                onChange();
            }
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }
        });


        serverIPTextField.getDocument().addDocumentListener(new DocumentListener() {
            private void onChange() {
                Properties.properties.setProperty("serverIP", serverIPTextField.getText());
                Properties.saveProperties();
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                onChange();
            }

            public void removeUpdate(DocumentEvent e) {
                onChange();
            }
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }
        });

        serverPortTextField.getDocument().addDocumentListener(new DocumentListener() {
            private void onChange() {
                Properties.properties.setProperty("serverPort", serverPortTextField.getText());
                Properties.saveProperties();
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                onChange();
            }

            public void removeUpdate(DocumentEvent e) {
                onChange();
            }
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }
        });

        baseURLTextField.getDocument().addDocumentListener(new DocumentListener() {
            private void onChange() {
                Properties.properties.setProperty("baseUrl", baseURLTextField.getText());
                Properties.saveProperties();
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                onChange();
            }

            public void removeUpdate(DocumentEvent e) {
                onChange();
            }
            public void insertUpdate(DocumentEvent e) {
                onChange();
            }
        });

        DocumentListener loginButtonEnableListener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent evt) {
                if(passwordField.getPassword().length > 0 && !usernameTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty()) || !useLocalProxyCheckBox.isSelected()) {
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                }
            }

            public void removeUpdate(DocumentEvent e) {
                if(passwordField.getPassword().length > 0 && !usernameTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty()) || !useLocalProxyCheckBox.isSelected()) {
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                }
            }
            public void insertUpdate(DocumentEvent e) {
                if(passwordField.getPassword().length > 0 && !usernameTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty()) || !useLocalProxyCheckBox.isSelected()) {
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                }
            }
        };

        passwordField.getDocument().addDocumentListener(loginButtonEnableListener);
        apiDomainTextField.getDocument().addDocumentListener(loginButtonEnableListener);
        usernameTextField.getDocument().addDocumentListener(loginButtonEnableListener);

        appletClassNameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent evt) {
                if(!jarPathTextField.getText().isEmpty() && !appletClassNameTextField.getText().isEmpty())
                    launchAppletButton.setEnabled(true);
                else
                    launchAppletButton.setEnabled(false);
            }

            public void removeUpdate(DocumentEvent e) {
                if(!jarPathTextField.getText().isEmpty() && !appletClassNameTextField.getText().isEmpty())
                    launchAppletButton.setEnabled(true);
                else
                    launchAppletButton.setEnabled(false);
            }
            public void insertUpdate(DocumentEvent e) {
                if(!jarPathTextField.getText().isEmpty() && !appletClassNameTextField.getText().isEmpty())
                    launchAppletButton.setEnabled(true);
                else
                    launchAppletButton.setEnabled(false);
            }
        });

        gameClassNameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent evt) {
                if(!jarPathTextField.getText().isEmpty() && !gameClassNameTextField.getText().isEmpty())
                    launchGameButton.setEnabled(true);
                else
                    launchGameButton.setEnabled(false);
            }

            public void removeUpdate(DocumentEvent e) {
                if(!jarPathTextField.getText().isEmpty() && !gameClassNameTextField.getText().isEmpty())
                    launchGameButton.setEnabled(true);
                else
                    launchGameButton.setEnabled(false);
            }
            public void insertUpdate(DocumentEvent e) {
                if(!jarPathTextField.getText().isEmpty() && !gameClassNameTextField.getText().isEmpty())
                    launchGameButton.setEnabled(true);
                else
                    launchGameButton.setEnabled(false);
            }
        });

        DocumentListener mpPassButtonDocumentListner = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent evt) {
                if(!serverIPTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty() && !sessionIdTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty()) || !useLocalProxyCheckBox.isSelected())
                    getMppassButton.setEnabled(true);
                else
                    getMppassButton.setEnabled(false);
            }

            public void removeUpdate(DocumentEvent e) {
                if(!serverIPTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty() && !sessionIdTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty()) || !useLocalProxyCheckBox.isSelected())
                    getMppassButton.setEnabled(true);
                else
                    getMppassButton.setEnabled(false);
            }
            public void insertUpdate(DocumentEvent e) {
                if(!serverIPTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty() && !sessionIdTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty()) || !useLocalProxyCheckBox.isSelected())
                    getMppassButton.setEnabled(true);
                else
                    getMppassButton.setEnabled(false);
            }
        };

        openJoinURLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String response = JOptionPane.showInputDialog(null,
                        "Paste server join URL. \neg: http://www.minecraft.net/play.jsp?server=2119e641afa41e6a6364221c1566c560",
                        "Join Server",
                        JOptionPane.QUESTION_MESSAGE);

                response = response.substring(response.indexOf("server="));
                if(response.contains("&"))
                    response = response.substring(0, response.indexOf('&'));
                response = response.replace("server=", "");

                try {
                    String server = MineOnlineLauncher.getServer(response);
                    serverIPTextField.setText(server.split(":")[0]);
                    serverPortTextField.setText(server.split(":")[1]);
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(null, "Failed to locate server.\nThis API might not support MineOnline.");
                }
            }
        });

        sessionIdTextField.getDocument().addDocumentListener(mpPassButtonDocumentListner);
        serverIPTextField.getDocument().addDocumentListener(mpPassButtonDocumentListner);
        apiDomainTextField.getDocument().addDocumentListener(mpPassButtonDocumentListner);

        needAccountLabel.setForeground(Color.BLUE.darker());
        needAccountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        needAccountLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://" + apiDomainTextField.getText() + "/register.jsp"));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // the mouse has entered the label
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // the mouse has exited the label
            }
        });


        usernameTextField.setText(Properties.properties.getProperty("username"));
        hasPaidCheckBox.setSelected(Boolean.parseBoolean(Properties.properties.getProperty("isPremium")));
        jarPathTextField.setText(Properties.properties.getProperty("jarFilePath"));
        apiDomainTextField.setText(Properties.properties.getProperty("apiDomainName"));
        useLocalProxyCheckBox.setSelected(Boolean.parseBoolean(Properties.properties.getProperty("useLocalProxy")));
        useLocalProxyUpdated();
        serverIPTextField.setText(Properties.properties.getProperty("serverIP"));
        serverPortTextField.setText(Properties.properties.getProperty("serverPort"));
        connectToServerCheckBox.setSelected(Boolean.parseBoolean(Properties.properties.getProperty("joinServer")));
        baseURLTextField.setText(Properties.properties.getProperty("baseUrl"));
        joinServerUpdated();
    }

    private void joinServerUpdated() {
        serverIPTextField.setEnabled(connectToServerCheckBox.isSelected());
        serverIPTextField.setEditable(connectToServerCheckBox.isSelected());
        serverPortTextField.setEnabled(connectToServerCheckBox.isSelected());
        serverPortTextField.setEditable(connectToServerCheckBox.isSelected());
        mppassTextField.setEnabled(connectToServerCheckBox.isSelected());
        mppassTextField.setEditable(connectToServerCheckBox.isSelected());
        openJoinURLButton.setEnabled(connectToServerCheckBox.isSelected());
        getMppassButton.setEnabled(connectToServerCheckBox.isSelected() && !serverIPTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty() && !sessionIdTextField.getText().isEmpty() && (!apiDomainTextField.getText().isEmpty()) || !useLocalProxyCheckBox.isSelected());
    }

    private void useLocalProxyUpdated() {
        if(useLocalProxyCheckBox.isSelected())
            startProxy();
        else
            killProxy();
        apiDomainTextField.setEnabled(useLocalProxyCheckBox.isSelected());
        apiDomainTextField.setEditable(useLocalProxyCheckBox.isSelected());
    }

    private void startProxy() {
        try {
            killProxy();

            proxyPort = Proxy.launchProxy();
            System.getProperties().put("http.proxyHost", "0.0.0.0");
            System.getProperties().put("http.proxyPort", proxyPort);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Failed to start proxy.");
            useLocalProxyCheckBox.setSelected(false);
        }
    }

    private void killProxy() {
        if(Proxy.proxyProcess != null && Proxy.proxyProcess.isAlive()) {
            Proxy.proxyProcess.destroy();
        }
    }

    public static void main(String[] args) {
        Properties.loadProperties();

        JFrame frame = new MineOnlineLauncherFrame();
        frame.setVisible(true);
    }
}
