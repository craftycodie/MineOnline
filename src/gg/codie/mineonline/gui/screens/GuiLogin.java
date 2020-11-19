package gg.codie.mineonline.gui.screens;

import gg.codie.minecraft.api.AuthServer;
import gg.codie.minecraft.api.MojangAPI;
import gg.codie.minecraft.client.gui.Tessellator;
import gg.codie.mineonline.Globals;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiPasswordField;
import gg.codie.mineonline.gui.components.GuiTextField;
import gg.codie.mineonline.gui.rendering.FontRenderer;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.textures.EGUITexture;
import gg.codie.mineonline.gui.sound.ClickSound;
import gg.codie.mineonline.utils.LastLogin;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

public class GuiLogin extends AbstractGuiScreen
{
    String errorText;
    boolean offline;

    public GuiLogin()
    {
        try
        {
            ArrayList arraylist = new ArrayList();
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader((GuiLogin.class).getResourceAsStream("/title/splashes.txt"), Charset.forName("UTF-8")));
            String s = "";
            do
            {
                String s1;
                if((s1 = bufferedreader.readLine()) == null)
                {
                    break;
                }
                s1 = s1.trim();
                if(s1.length() > 0)
                {
                    arraylist.add(s1);
                }
            } while(true);
        }
        catch(Exception exception) { }

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

        ((GuiButton)controlList.get(0)).enabled = usernameField.getText().length() > 0 && passwordField.getText().length() > 0;
    }

    protected void mouseClicked(int i, int j, int k)
    {
        usernameField.mouseClicked(i, j, k);
        passwordField.mouseClicked(i, j, k);
        super.mouseClicked(i, j, k);
        if (MenuManager.isUpdateAvailable() && j > getHeight() - 20 && j < getHeight() - 10 && i < FontRenderer.minecraftFontRenderer.getStringWidth("Update Available!")) {
            ClickSound.play();
            try {
                Desktop.getDesktop().browse(new URI(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/download"));
            } catch (Exception ex) {

            }
        }

        if (MenuManager.isUpdateAvailable() && j > getHeight() / 4 + 48 + 96 && j < getHeight() / 4 + 48 + 106 && i > (getWidth() / 2 ) - (FontRenderer.minecraftFontRenderer.getStringWidth("Need Account?")) / 2 && i < (getWidth() / 2 ) + (FontRenderer.minecraftFontRenderer.getStringWidth("Need Account?")) / 2) {
            ClickSound.play();
            try {
                Desktop.getDesktop().browse(new URI("https://www.minecraft.net/store/minecraft-java-edition"));
            } catch (Exception ex) {

            }
        }
    }

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
        controlList.add(new GuiButton(0, getWidth() / 2 - 100, i + 72, "Login", new GuiButton.GuiButtonListener() {
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
//                        MenuManager.setMenuScreen(new MainMenuScreen());
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
//                    if(playOfflineButton == null)
//                        playOfflineButton = new LargeButton("Play Offline", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) + 172), new IOnClickListener() {
//                            @Override
//                            public void onClick() {
//                                try {
//                                    if(usernameInput.getValue().contains("@")) {
//                                        EventQueue.invokeLater(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                JOptionPane.showMessageDialog(null, "Enter a username to play in offline-mode.");
//                                            }
//                                        });
//                                        return;
//                                    }
//
//                                    new Session(usernameInput.getValue());
////                                    MenuManager.setMenuScreen(new MainMenuScreen());
//                                } catch (Exception ex) {
//                                }
//                            }
//                        });
                }
            }

        }));

        ((GuiButton)controlList.get(0)).enabled = false;

        usernameField.isFocused = true;
        usernameField.setMaxStringLength(128);

        passwordField.isFocused = false;
        passwordField.setMaxStringLength(128);
    }

    public void resizeGui() {
        usernameField.resize(getWidth() / 2 - 100, getHeight() / 4 + 48);
        passwordField.resize(getWidth() / 2 - 100, getHeight() / 4 + 48 + 42);
        controlList.get(0).resize(getWidth() / 2 - 100, getHeight() / 4 + 48 + 72);
    }

    public void updateScreen()
    {
        if(usernameField != null)
            usernameField.updateCursorCounter();

        if(passwordField != null)
            passwordField.updateCursorCounter();
    }

    public void drawScreen(int i, int j)
    {
        resizeGui();

        Tessellator tessellator = Tessellator.instance;
        char c = '\u0112';
        int k = getWidth() / 2 - c / 2;
        byte byte0 = 30;
//        drawGradientRect(0, 0, getWidth(), getHeight(), 0x55ffffff, 0xffffff);
//        drawGradientRect(0, 0, getWidth(), getHeight(), 0, 0x55000000);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, Loader.singleton.getGuiTexture(EGUITexture.MINEONLINE_LOGO));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(k + 0, byte0 + 0, 0, 0, 155, 44);
        drawTexturedModalRect(k + 155, byte0 + 0, 0, 45, 155, 44);
        tessellator.setColorOpaque_I(0xffffff);
        if (MenuManager.isUpdateAvailable())
            drawString("Update Available!", 2, getHeight() - 20, 0xffff00);
        drawString("MineOnline " + (Globals.DEV ? "Dev " + Globals.LAUNCHER_VERSION + " (" + Globals.BRANCH + ")" : Globals.LAUNCHER_VERSION), 2, getHeight() - 10, 0xffffff);
        String s = "Made by @codieradical <3";
        drawString(s, getWidth() - FontRenderer.minecraftFontRenderer.getStringWidth(s) - 2, getHeight() - 10, 0xffffff);
        drawString("Need Account?", (getWidth() / 2) - FontRenderer.minecraftFontRenderer.getStringWidth("Need Account?") / 2, getHeight() / 4 + 48 + 96, 0xffffff);
        drawString("Username or E-Mail:", getWidth() / 2 - 100, getHeight() / 4 + 48 - 16, 0xffffff);
        drawString("Password:", getWidth() / 2 - 100, getHeight() / 4 + 48 + 48 - 22, 0xffffff);
        drawString(errorText, (getWidth() / 2) - FontRenderer.minecraftFontRenderer.getStringWidth(errorText) / 2, getHeight() / 4 + 48 - 32, 0xFF5555);

        usernameField.drawTextBox();
        passwordField.drawTextBox();

        super.drawScreen(i, j);
    }

    private GuiTextField usernameField;
    private GuiPasswordField passwordField;
}
