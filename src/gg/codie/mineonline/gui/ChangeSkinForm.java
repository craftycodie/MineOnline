package gg.codie.mineonline.gui;

import gg.codie.mineonline.*;
import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.mineonline.gui.rendering.PlayerGameObject;
import gg.codie.mineonline.gui.rendering.TextureHelper;
import gg.codie.mineonline.gui.rendering.animation.WalkPlayerAnimation;
import gg.codie.utils.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ChangeSkinForm implements IContainerForm {
    private JButton backButton;
    private JPanel contentPanell;
    private JPanel skinPanel;
    private JLabel logolabel;
    private JTextField skinTextField;
    private JTextField cloakTextField;
    private JButton submitButton;
    private JButton removeCloakButton;
    private JButton browseSkinButton;
    private JButton browseCloakButton;
    private JFileChooser fileChooser = new JFileChooser();

    private boolean unsavedChanges = false;

    @Override
    public JPanel getContent() {
        return contentPanell;
    }

    @Override
    public JPanel getRenderPanel() {
        return skinPanel;
    }

    ChangeSkinForm() {
        if(PlayerGameObject.thePlayer != null) {
            PlayerGameObject.thePlayer.setPlayerAnimation(new WalkPlayerAnimation());
        }

        ImageIcon icon = new ImageIcon(ChangeSkinForm.class.getResource("/img/mineonlinelogo.png"));
        logolabel.setIcon(icon);

        contentPanell.setPreferredSize(new Dimension(845, 476));

        browseSkinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal =  fileChooser.showOpenDialog(FormManager.singleton);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    skinTextField.setText(file.getAbsolutePath());
                }
            }
        });

        browseCloakButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal =  fileChooser.showOpenDialog(FormManager.singleton);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    cloakTextField.setText(file.getAbsolutePath());
                }
            }
        });

        skinTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void findSkin() {
                if(skinTextField.getText().isEmpty())
                    return;

                unsavedChanges = true;

                File skinTexture = new File(skinTextField.getText());
                if (skinTexture.exists() && PlayerGameObject.thePlayer != null) {
                    try {
                        PlayerGameObject.thePlayer.setSkin(Paths.get(skinTexture.getPath()).toUri().toURL());
                    } catch (MalformedURLException mx) {

                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                findSkin();
            }

            public void removeUpdate(DocumentEvent e) {
                findSkin();
            }
            public void insertUpdate(DocumentEvent e) {
                findSkin();
            }

        });

        cloakTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void findCloak() {
                if(cloakTextField.getText().isEmpty())
                    return;

                File cloakTexture = new File(cloakTextField.getText());
                if (cloakTexture.exists() && PlayerGameObject.thePlayer != null) {
                    try {
                        PlayerGameObject.thePlayer.setCloak(Paths.get(cloakTexture.getPath()).toUri().toURL());
                    } catch (MalformedURLException mx) {

                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                findCloak();
            }

            public void removeUpdate(DocumentEvent e) {
                findCloak();
            }
            public void insertUpdate(DocumentEvent e) {
                findCloak();
            }

        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean failed = false;
                if(!skinTextField.getText().isEmpty()) {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(new File(skinTextField.getText()));
                        bufferedImage = TextureHelper.cropImage(bufferedImage, 0, 0, 64, 32);
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "png", os);
                        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                        MinecraftAPI.uploadSkin(Session.session.getUsername(), Session.session.getSessionToken(), is);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Failed to upload skin.");
                        failed = true;
                    }
                }
                if(!cloakTextField.getText().isEmpty()) {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(new File(cloakTextField.getText()));
                        bufferedImage = TextureHelper.cropImage(bufferedImage, 0, 0, 64, 32);
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write(bufferedImage, "png", os);
                        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                        MinecraftAPI.uploadCloak(Session.session.getUsername(), Session.session.getSessionToken(), is);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Failed to upload skin.");
                        failed = true;
                    }
                }
                if(!failed) {
                    unsavedChanges = false;
                }
            }
        });

        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                return FileUtils.getExtension(f) != null && FileUtils.getExtension(f).equals("png");
            }

            @Override
            public String getDescription() {
                return "PNG image file (.png)";
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(unsavedChanges) {
                    Session.session.cacheSkin();
                }

                FormManager.switchScreen(new MainForm());
            }
        });

        removeCloakButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(MinecraftAPI.removecloak(Session.session.getSessionToken())) {
                    if(PlayerGameObject.thePlayer != null) {
                        PlayerGameObject.thePlayer.setCloak(LauncherFiles.TEMPLATE_CLOAK_PATH);
                    }
                }
            }
        });
    }
}
