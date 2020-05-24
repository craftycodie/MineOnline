package gg.codie.mineonline.gui;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.MineOnlineLauncher;
import gg.codie.mineonline.Properties;
import gg.codie.mineonline.Session;
import org.lwjgl.LWJGLException;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Random;

public class LoginForm  implements IContainerForm {
    private JLabel logolabel;
    private JPanel contentPanel;
    private JPanel loginContainer;
    private JTextField usernameTextField;
    private JPasswordField passwordField1;
    private JCheckBox rememberMeCheckBox;
    private JButton loginButton;
    private JButton playOfflineButton;
    private JLabel errorMessage;

    public JPanel getContent() {
        return contentPanel;
    }

    public JPanel getRenderPanel() {
        return null;
    }

    public LoginForm() {
        ImageIcon icon = new ImageIcon("res/mineonlinelogo.png");
        logolabel.setIcon(icon);

        playOfflineButton.setVisible(false);

        contentPanel.setPreferredSize(new Dimension(845, 476));

        Properties.loadProperties();

        readLastLogin();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(usernameTextField.getText().isEmpty()) {
                    errorMessage.setText("Enter your username.");
                    return;
                }

                if(passwordField1.getPassword().length < 1) {
                    errorMessage.setText("Enter your password.");
                    return;
                }

                String failed = null;
                errorMessage.setText("");
                try {
                    String sessionToken = MineOnlineLauncher.login(usernameTextField.getText(), new String(passwordField1.getPassword()));
                    if (sessionToken != null) {
                        new Session(usernameTextField.getText(), sessionToken);
                        writeLastLogin();
                        FormManager.switchScreen(new MainForm());
                    } else {
                        failed = "Failed to login.";
                    }
                }
                catch (Exception ex) {
                    failed = ex.getMessage();
                }
                if(failed != null && new File(LauncherFiles.LAST_LOGIN_PATH).exists()) {
                    playOfflineButton.setVisible(true);
                    errorMessage.setText(failed);
                }
            }
        });

        playOfflineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(usernameTextField.getText().isEmpty()) {
                    errorMessage.setText("Enter your username.");
                    return;
                }
                new Session(usernameTextField.getText());
                FormManager.switchScreen(new MainForm());
            }
        });
    }

    private void readLastLogin() {
    try {
        DataInputStream dis;
        File lastLogin = new File(LauncherFiles.LAST_LOGIN_PATH);

        Cipher cipher = getCipher(2, "passwordfile");
        if (cipher != null) {
            dis = new DataInputStream(new CipherInputStream(new FileInputStream(lastLogin), cipher));
        } else {
             dis = new DataInputStream(new FileInputStream(lastLogin));
        }
        usernameTextField.setText(dis.readUTF());
        passwordField1.setText(dis.readUTF());
        rememberMeCheckBox.setSelected((new String(passwordField1.getPassword()).length() > 0));
        dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   private void writeLastLogin() {
     try {
       DataOutputStream dos;
       File lastLogin = new File(LauncherFiles.LAST_LOGIN_PATH);

       Cipher cipher = getCipher(1, "passwordfile");
       if (cipher != null) {
         dos = new DataOutputStream(new CipherOutputStream(new FileOutputStream(lastLogin), cipher));
       } else {
         dos = new DataOutputStream(new FileOutputStream(lastLogin));
       }
       dos.writeUTF(this.usernameTextField.getText());
       dos.writeUTF(this.rememberMeCheckBox.isSelected() ? new String(this.passwordField1.getPassword()) : "");
       dos.close();
     } catch (Exception e) {
        e.printStackTrace();
     }
   }

   private Cipher getCipher(int mode, String password) throws Exception {
     Random random = new Random(43287234L);
     byte[] salt = new byte[8];
     random.nextBytes(salt);
     PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

     SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
     Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
     cipher.init(mode, pbeKey, pbeParamSpec);
     return cipher;
   }

}
