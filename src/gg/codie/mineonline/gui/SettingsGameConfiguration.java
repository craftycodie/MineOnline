package gg.codie.mineonline.gui;

import gg.codie.mineonline.*;
import gg.codie.utils.FileUtils;
import gg.codie.utils.JSONUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SettingsGameConfiguration implements IContainerForm {
    private JPanel contentPanel;
    private JButton newButton;
    private JButton renameButton;
    private JButton deleteButton;
    private JComboBox comboBox1;
    private JTextField jarPathTextField;
    private JTextField mainClassTextField;
    private JTextField appletClassTextField;
    private JButton browseButton;
    private JFileChooser fileChooser = new JFileChooser();

    public JPanel getContent() {
        return contentPanel;
    }

    @Override
    public JPanel getRenderPanel() {
        return null;
    }

    private List<MinecraftInstall> installs;

    public SettingsGameConfiguration() {
        contentPanel.setPreferredSize(new Dimension(845 - 147, 476));
        Properties.loadProperties();
        installs = JSONUtils.getMinecraftInstalls(Properties.properties.getJSONArray("minecraftInstalls"));

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

        for(MinecraftInstall install : installs) {
            comboBox1.addItem(install.getName());
        }

        if(installs.size() > 0) {
            updateSelection(0);
        }

        if(installs.size() < 1) {
            newInstall();
        }

        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newInstall();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = comboBox1.getSelectedIndex();
                //comboBox1.setSelectedIndex(Math.min(comboBox1.getSelectedIndex() - 1, 0));
                if(selectedIndex == installs.size() - 1)
                {
                    comboBox1.setSelectedIndex(installs.size() - 2);
                }
                installs.remove(selectedIndex);
                comboBox1.removeItemAt(selectedIndex);
                Properties.properties.put("minecraftInstalls", JSONUtils.setMineraftInstalls(installs));
                Properties.saveProperties();
                deleteButton.setEnabled(installs.size() > 1);
            }
        });

        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(null,
                        "Enter the configuration name.",
                        "Rename Configuration",
                        JOptionPane.QUESTION_MESSAGE);

                if(!name.isEmpty()){
                    int selectedIndex = comboBox1.getSelectedIndex();
                    comboBox1.removeItemAt(selectedIndex);
                    comboBox1.insertItemAt(name, selectedIndex);
                    comboBox1.setSelectedIndex(selectedIndex);
                    MinecraftInstall minecraftInstall = installs.get(selectedIndex);
                    minecraftInstall.setName(name);
                    installs.set(selectedIndex, minecraftInstall);
                    Properties.properties.put("minecraftInstalls", JSONUtils.setMineraftInstalls(installs));
                    Properties.saveProperties();
                }
            }
        });

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal =  fileChooser.showOpenDialog(FormManager.singleton);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    jarPathTextField.setText(file.getAbsolutePath());
                }
            }
        });

        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSelection(comboBox1.getSelectedIndex());
            }
        });

        jarPathTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void findMainClasses() {
                try {
                    int selectedIndex = comboBox1.getSelectedIndex();
                    MinecraftInstall minecraftInstall = installs.get(selectedIndex);
                    minecraftInstall.setJarPath(jarPathTextField.getText());
                    installs.set(selectedIndex, minecraftInstall);
                    Properties.properties.put("minecraftInstalls", JSONUtils.setMineraftInstalls(installs));
                    Properties.saveProperties();

                    appletClassTextField.setText("");
                    mainClassTextField.setText("");

                    if(jarPathTextField.getText().isEmpty())
                        return;

                    Properties.properties.put("jarFilePath", jarPathTextField.getText());
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
                            minecraftInstall.setAppletClass(classCanonicalName);
                        } else if(className.equals("MiencraftLauncher")) { ;
                            minecraftInstall.setMainClass(classCanonicalName);
                        } else if(className.equals("Minecraft")) {
                            minecraftInstall.setMainClass(classCanonicalName);
                        }
                    }
                    minecraftInstall.setJarPath(jarPathTextField.getText());
                    installs.set(selectedIndex, minecraftInstall);
                    Properties.properties.put("minecraftInstalls", JSONUtils.setMineraftInstalls(installs));
                    Properties.saveProperties();
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
    }

    public void newInstall() {
        installs.add(new MinecraftInstall("new configutation", "", "", ""));
        Properties.properties.put("minecraftInstalls", JSONUtils.setMineraftInstalls(installs));
        Properties.saveProperties();
        comboBox1.addItem("new configuration");
        updateSelection(installs.size() - 1);
        deleteButton.setEnabled(installs.size() > 1);
        comboBox1.setSelectedIndex(installs.size() - 1);
    }

    public void updateSelection(int selectedIndex) {
        MinecraftInstall minecraftInstall = installs.get(selectedIndex);
        jarPathTextField.setText(minecraftInstall.getJarPath());
        mainClassTextField.setText(minecraftInstall.getMainClass());
        appletClassTextField.setText(minecraftInstall.getAppletClass());
        deleteButton.setEnabled(installs.size() > 1);
    }
}
