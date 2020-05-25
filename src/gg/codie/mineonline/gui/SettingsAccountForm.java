package gg.codie.mineonline.gui;

import gg.codie.mineonline.api.MineOnlineAccount;
import gg.codie.mineonline.MineOnlineLauncher;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.MinecraftAPI;

import javax.swing.*;
import java.awt.*;

public class SettingsAccountForm implements IContainerForm {
    private JPanel contentPanel;
    private JLabel emailLabel;
    private JLabel usernameLabel;
    private JLabel memberSinceLabel;

    public JPanel getContent() {
        return contentPanel;
    }

    @Override
    public JPanel getRenderPanel() {
        return null;
    }

    public SettingsAccountForm() {
        contentPanel.setPreferredSize(new Dimension(845 - 147, 476));

        try {
            MineOnlineAccount account = MinecraftAPI.account(Session.session.getUsername(), Session.session.getSessionToken());

            usernameLabel.setText(usernameLabel.getText() + account.user);
            emailLabel.setText(emailLabel.getText() + account.email);
            memberSinceLabel.setText(memberSinceLabel.getText() + account.createdAt.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
