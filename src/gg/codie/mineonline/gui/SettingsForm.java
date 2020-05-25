package gg.codie.mineonline.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import gg.codie.mineonline.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsForm implements IContainerForm {
    private JPanel contentPanel;
    private JButton accountButton;
    private JButton gameConfigurationsButton;
    private JButton launchSettingsButton;
    private JButton onlineMapsButton;
    private JButton aboutButton;
    private JButton backButton;
    private JPanel bodyPanel;

    public JPanel getContent() {
        return contentPanel;
    }

    public JPanel getRenderPanel() {
        return null;
    }

    public void changeMenu(IContainerForm menu) {
        bodyPanel.setLayout(new java.awt.BorderLayout());
        if(bodyPanel.getComponents().length > 0) {
            bodyPanel.remove(0);
        }
        bodyPanel.add(menu.getContent());
        bodyPanel.validate();
        bodyPanel.repaint();
    }

    public SettingsForm() {
        contentPanel.setPreferredSize(new Dimension(845, 476));


        changeMenu(new SettingsAccountForm());

        if (!Session.session.isOnline()) {
            accountButton.setVisible(false);
            onlineMapsButton.setVisible(false);
        }

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FormManager.switchScreen(new MainForm());
            }
        });


        accountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeMenu(new SettingsAccountForm());
            }
        });

        gameConfigurationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeMenu(new SettingsGameConfiguration());
            }
        });
    }
}
