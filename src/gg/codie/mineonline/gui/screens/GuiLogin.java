package gg.codie.mineonline.gui.screens;

import gg.codie.minecraft.api.AuthServer;
import gg.codie.minecraft.api.MojangAPI;
import gg.codie.mineonline.Globals;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiPasswordField;
import gg.codie.mineonline.gui.components.GuiTextField;
import gg.codie.mineonline.gui.rendering.Font;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.sound.ClickSound;
import gg.codie.mineonline.gui.textures.EGUITexture;
import gg.codie.mineonline.utils.LastLogin;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class GuiLogin extends AbstractGuiScreen
{
    private String errorText;
    private boolean offline;

    public GuiLogin()
    {
        initGui();
    }

    protected void keyTyped(char c, int i)
    {
        usernameField.textboxKeyTyped(c, i);
        if(c == '\r')
        {
            actionPerformed((GuiButton)controlList.get(0));
        }

        passwordField.textboxKeyTyped(c, i);
        if(c == '\r')
        {
            actionPerformed((GuiButton)controlList.get(0));
        }

        loginButton.enabled = usernameField.getText().length() > 0 && passwordField.getText().length() > 0;

        if (playOfflineButton != null)
            playOfflineButton.enabled = usernameField.getText().length() > 0;

    }

    protected void mouseClicked(int x, int y, int button)
    {
        usernameField.mouseClicked(x, y, button);
        passwordField.mouseClicked(x, y, button);
        super.mouseClicked(x, y, button);
        if (MenuManager.isUpdateAvailable() && y > getHeight() - 20 && y < getHeight() - 10 && x < Font.minecraftFont.width("Update Available!")) {
            ClickSound.play();
            try {
                if (Globals.BRANCH.equalsIgnoreCase("release"))
                    Desktop.getDesktop().browse(new URI(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/download"));
                else
                    Desktop.getDesktop().browse(new URI("https://github.com/codieradical/MineOnline/releases"));
            } catch (Exception ex) {

            }
        }

        if (y > getHeight() / 4 + 48 + 96 && y < getHeight() / 4 + 48 + 106 && x > (getWidth() / 2 ) - (Font.minecraftFont.width("Need Account?")) / 2 && x < (getWidth() / 2 ) + (Font.minecraftFont.width("Need Account?")) / 2) {
            ClickSound.play();
            try {
                Desktop.getDesktop().browse(new URI("https://www.minecraft.net/store/minecraft-java-edition"));
            } catch (Exception ex) {

            }
        }

        if (y > getHeight() - 10 && y < getHeight() && x > getWidth() - Font.minecraftFont.width("Made by @codieradical <3")) {
            ClickSound.play();
            try {
                Desktop.getDesktop().browse(new URI("https://twitter.com/codieradical"));
            } catch (Exception ex) {

            }
        }
    }

    public void showOfflineButton() {
        controlList.remove(loginButton);
        controlList.add(loginButton = new GuiButton(0, getWidth() / 2, getHeight() / 4 + 48 + 72, 100, 20, "Login", loginHandler));
        controlList.add(playOfflineButton = new GuiButton(0, getWidth() / 2 - 102, getHeight() / 4 + 48 + 72, 100, 20, "Play Offline", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                try {
                    if(usernameField.getText().contains("@")) {
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                JOptionPane.showMessageDialog(null, "Enter a username to play in offline-mode.");
                            }
                        });
                        return;
                    }

                    new Session(usernameField.getText());
                    MenuManager.setMenuScreen(new GuiMainMenu());
                } catch (Exception ex) {
                }
            }
        }));
    }

    GuiButton.GuiButtonListener loginHandler =  new GuiButton.GuiButtonListener() {
        @Override
        public void OnButtonPress() {

            try {
                String clientSecret = UUID.randomUUID().toString();
                JSONObject login = AuthServer.authenticate(usernameField.getText(), passwordField.getText(), clientSecret);

                if (login.has("error"))
                    throw new Exception(login.getString("error"));
                if (!login.has("accessToken") || !login.has("selectedProfile"))
                    throw new Exception("Failed to authenticate!");
                if (MojangAPI.minecraftProfile(login.getJSONObject("selectedProfile").getString("name")).optBoolean("demo", false))
                    throw new Exception("Please buy Minecraft to use MineOnline.");

                String sessionToken = login.getString("accessToken");
                String uuid = login.getJSONObject("selectedProfile").getString("id");

                if (sessionToken != null) {
                    new Session(login.getJSONObject("selectedProfile").getString("name"), sessionToken, clientSecret, uuid, true);
                    LastLogin.writeLastLogin(Session.session.getAccessToken(), clientSecret, usernameField.getText(), Session.session.getUsername(), Session.session.getUuid());
                    MenuManager.setMenuScreen(new GuiMainMenu());
                } else {
                    errorText = "Incorrect username or password.";
                }
            } catch (Exception ex) {
                ex.printStackTrace();

                String errorMessage = ex.getMessage();
                if (errorMessage.startsWith("Bad login")) {
                    errorMessage = "Incorrect username or password.";
                }

                if (ex instanceof IOException)
                    errorMessage = "Bad login.";

                errorText = errorMessage;

                offline = true;
            }
        }
    };

    public void initGui()
    {
        int i = getHeight() / 4 + 48;

        String username = "";
        LastLogin lastLogin = LastLogin.readLastLogin();
        if(lastLogin != null) {
            username = lastLogin.loginUsername;
        }

        usernameField = new GuiTextField(this, getWidth() / 2 - 100, i, 200, 20, username);
        passwordField = new GuiPasswordField(this, getWidth() / 2 - 100, i + 42, 200, 20, "");
        controlList.add(loginButton = new GuiButton(0, getWidth() / 2 - 100, i + 72, "Login", loginHandler));

        loginButton.enabled = false;

        usernameField.isFocused = true;
        usernameField.setMaxStringLength(128);

        passwordField.isFocused = false;
        passwordField.setMaxStringLength(128);
    }

    public void resize() {
        usernameField.resize(getWidth() / 2 - 100, getHeight() / 4 + 48);
        passwordField.resize(getWidth() / 2 - 100, getHeight() / 4 + 48 + 42);
        if (playOfflineButton != null) {
            loginButton.resize(getWidth() / 2 + 2, getHeight() / 4 + 48 + 72);
            playOfflineButton.resize(getWidth() / 2 - 102, getHeight() / 4 + 48 + 72);
        } else {
            loginButton.resize(getWidth() / 2 - 100, getHeight() / 4 + 48 + 72);
        }
    }

    public void selectNextField() {
        if (!passwordField.isFocused) {
            usernameField.setFocused(false);
            passwordField.setFocused(true);
        }
    }

    public void drawScreen(int mouseX, int mouseY)
    {
        if (offline && playOfflineButton == null)
            showOfflineButton();

        resize();

        Renderer tessellator = Renderer.singleton;
        int k = (int)(((getWidth() / 2) - (90 * 1.6)) / 1.6f);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.MINEONLINE_LOGO));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, 10241, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, 10240, GL11.GL_NEAREST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glScalef(1.6f, 1.6f, 1);
        Renderer.singleton.drawSprite(k, 22, 0, 0, 180, 180);
        GL11.glScalef(0.625f, 0.625f, 1);
        tessellator.setColorRGBA(255, 255, 255, 255);
        if (MenuManager.isUpdateAvailable())
            Font.minecraftFont.drawString("Update Available!", 2, getHeight() - 20, 0xffff00);
        Font.minecraftFont.drawString("MineOnline " + (Globals.DEV ? "Dev " : "") + Globals.LAUNCHER_VERSION + (!Globals.BRANCH.equalsIgnoreCase("release") ? " (" + Globals.BRANCH + ")" : ""), 2, getHeight() - 10, 0xffffff);
        String s = "Made by @codieradical <3";
        Font.minecraftFont.drawString(s, getWidth() - Font.minecraftFont.width(s) - 2, getHeight() - 10, 0xffffff);
        Font.minecraftFont.drawString("Need Account?", (getWidth() / 2) - Font.minecraftFont.width("Need Account?") / 2, getHeight() / 4 + 48 + 96, 0x5555FF);
        Font.minecraftFont.drawString("Username or E-Mail:", getWidth() / 2 - 100, getHeight() / 4 + 48 - 16, 0xffffff);
        Font.minecraftFont.drawString("Password:", getWidth() / 2 - 100, getHeight() / 4 + 48 + 48 - 22, 0xffffff);
        Font.minecraftFont.drawString(errorText, (getWidth() / 2) - Font.minecraftFont.width(errorText) / 2, getHeight() / 4 + 48 - 32, 0xFF5555);

        usernameField.drawTextBox();
        passwordField.drawTextBox();

        super.drawScreen(mouseX, mouseY);
    }

    private GuiTextField usernameField;
    private GuiPasswordField passwordField;
    private GuiButton loginButton;
    private GuiButton playOfflineButton;
}
