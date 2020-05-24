package gg.codie.mineonline.gui;

import gg.codie.mineonline.MineOnlineLauncher;
import gg.codie.mineonline.Properties;
import gg.codie.mineonline.Session;
import org.lwjgl.LWJGLException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm  implements IContainerForm {
    private JLabel logolabel;
    private JPanel contentPanel;
    private JPanel loginContainer;
    private JTextField usernameTextField;
    private JPasswordField passwordField1;
    private JCheckBox rememberMeCheckBox;
    private JButton loginButton;

    public JPanel getContent() {
        return contentPanel;
    }

    public JPanel getRenderPanel() {
        return null;
    }

    public LoginForm() {
        ImageIcon icon = new ImageIcon("res/mineonlinelogo.png");
        logolabel.setIcon(icon);

        contentPanel.setPreferredSize(new Dimension(845, 476));

        Properties.loadProperties();

        usernameTextField.setText(Properties.properties.getProperty("username"));

        if (Properties.properties.getProperty("rememberMe") != null && Properties.properties.getProperty("rememberMe").toLowerCase().equals("true")) {
            rememberMeCheckBox.setSelected(true);
        }

        rememberMeCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Properties.properties.setProperty("rememberMe", Boolean.toString(rememberMeCheckBox.isSelected()));
                Properties.saveProperties();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sessionToken = MineOnlineLauncher.login(usernameTextField.getText(), new String(passwordField1.getPassword()));
                if (sessionToken != null) {
                    new Session(usernameTextField.getText(), sessionToken);
                    FormManager.switchScreen(new MainForm());
                }
            }
        });
    }

}
