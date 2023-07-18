package gg.codie.mineonline.gui.screens;

import gg.codie.common.utils.OSUtils;
import gg.codie.mineonline.Globals;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.MicrosoftLoginController;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.toast.DeviceCodeClipboardToast;
import gg.codie.mineonline.gui.rendering.Font;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.sound.ClickSound;
import gg.codie.mineonline.gui.textures.EGUITexture;
import gg.codie.mineonline.utils.LastLogin;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URI;

public class GuiLogin extends AbstractGuiScreen
{
    private String errorText;
    private boolean offline;
    String loginCode;
    String verificationUrl;

    public GuiLogin(boolean offline)
    {
        this.offline = offline;

        if (MicrosoftLoginController.getLoginCode() == null)
            MicrosoftLoginController.loadDeviceCode();

        loginCode = MicrosoftLoginController.getLoginCode();
        verificationUrl = MicrosoftLoginController.getVerificationUrl();
        errorText = MicrosoftLoginController.getError();

        initGui();
    }

    protected void keyTyped(char c, int i)
    {

    }

    private void legacyLogin() {
        MenuManager.setMenuScreen(new GuiLoginLegacy(offline));
    }

    protected void mouseClicked(int x, int y, int button)
    {
        super.mouseClicked(x, y, button);
        if (MenuManager.isUpdateAvailable() && y > getHeight() - 20 && y < getHeight() - 10 && x < Font.minecraftFont.width("Update Available!")) {
            ClickSound.play();
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/craftycodie/MineOnline/releases/latest"));
            } catch (Exception ex) {

            }
        }

        if (y > getHeight() / 4 + 48 + 96 && y < getHeight() / 4 + 48 + 106 && x > ((getWidth() / 2) + 102) - (Font.minecraftFont.width("Need Account?")) && x < (getWidth() / 2 ) + 102) {
            ClickSound.play();
            try {
                Desktop.getDesktop().browse(new URI("https://www.minecraft.net/store/minecraft-java-edition"));
            } catch (Exception ex) {

            }
        }

        if (y > getHeight() / 4 + 48 + 96 && y < getHeight() / 4 + 48 + 106 && x > ((getWidth() / 2) - 100) && x < ((getWidth() / 2) - 102) + (Font.minecraftFont.width("Mojang Login (Legacy)"))) {
            ClickSound.play();
            try {
                legacyLogin();
            } catch (Exception ex) {

            }
        }

        if (y > getHeight() - 10 && y < getHeight() && x > getWidth() - Font.minecraftFont.width("Made by @codiegotlost <3")) {
            ClickSound.play();
            try {
                Desktop.getDesktop().browse(new URI("https://twitter.com/codiegotlost"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (errorText == null && y > getHeight() / 4 + 48 + 52 - 22 && y < getHeight() / 4 + 48 + 52 - 22 + 10 && x > (getWidth() / 2) - (Font.minecraftFont.width(loginCode) / 2) && x < (getWidth() / 2) + (Font.minecraftFont.width(loginCode) / 2)) {
            loginHandler.OnButtonPress();
        }
    }

    GuiButton.GuiButtonListener loginHandler =  new GuiButton.GuiButtonListener() {
        @Override
        public void OnButtonPress() {
            ClickSound.play();
            try {
                Desktop.getDesktop().browse(new URI(verificationUrl));
                Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(loginCode);
                c.setContents( selection, null );
                DeviceCodeClipboardToast.show();
            } catch (Exception ex) {

            }
        }
    };

    public void showOfflineButton() {
        controlList.remove(loginButton);
        controlList.add(loginButton = new GuiButton(0, getWidth() / 2, getHeight() / 4 + 48 + 72, 100, 20, "Login", loginHandler));
        controlList.add(playOfflineButton = new GuiButton(0, getWidth() / 2 - 102, getHeight() / 4 + 48 + 72, 100, 20, "Play Offline", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                try {
                    LastLogin lastLogin = LastLogin.readLastLogin();
                    new Session(lastLogin.username, lastLogin.accessToken, lastLogin.clientToken, lastLogin.uuid, true);
                    MenuManager.setMenuScreen(new GuiMainMenu());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }));
    }

    public void showRetryButton() {
        controlList.add(retryButton = new GuiButton(0, getWidth() / 2 - 50, getHeight() / 4 + 84 - 16, 100, 20, "Retry", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                try {
                    MicrosoftLoginController.loadDeviceCode();
                    controlList.remove(retryButton);
                    retryButton = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }));
    }

    public void initGui()
    {
        int i = getHeight() / 4 + 48;
        controlList.add(loginButton = new GuiButton(0, getWidth() / 2 - 100, i + 72, "Login", loginHandler));
    }

    public void resize() {
        if (playOfflineButton != null) {
            loginButton.resize(getWidth() / 2 + 2, getHeight() / 4 + 48 + 72);
            playOfflineButton.resize(getWidth() / 2 - 102, getHeight() / 4 + 48 + 72);
        } else {
            loginButton.resize(getWidth() / 2 - 100, getHeight() / 4 + 48 + 72);
        }
    }

    public void selectNextField() {

    }

    public void drawScreen(int mouseX, int mouseY)
    {
        loginCode = MicrosoftLoginController.getLoginCode();
        verificationUrl = MicrosoftLoginController.getVerificationUrl();
        errorText = MicrosoftLoginController.getError();

        loginButton.enabled = errorText == null;

        if (offline && playOfflineButton == null)
            showOfflineButton();

        if (errorText != null && retryButton == null)
            showRetryButton();

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
        else if (OSUtils.isM1JVM())
            Font.minecraftFont.drawString("x86 JVM Recommended!", 2, getHeight() - 20, 0xffff00);
        Font.minecraftFont.drawString("MineOnline " + (Globals.DEV ? "Dev " : "") + Globals.LAUNCHER_VERSION + (!Globals.BRANCH.equalsIgnoreCase("main") ? " (" + Globals.BRANCH + ")" : ""), 2, getHeight() - 10, 0xffffff);
        String s = "Made by @codiegotlost <3";
        Font.minecraftFont.drawString(s, getWidth() - Font.minecraftFont.width(s) - 2, getHeight() - 10, 0xffffff);
        Font.minecraftFont.drawString("Mojang Login (Legacy)", ((getWidth() / 2) - 100), getHeight() / 4 + 48 + 96, 0x5555FF);
        Font.minecraftFont.drawString("Need Account?", ((getWidth() / 2) + 102) - Font.minecraftFont.width("Need Account?"), getHeight() / 4 + 48 + 96, 0x5555FF);
        Font.minecraftFont.drawCenteredString("Microsoft Login", getWidth() / 2, getHeight() / 4 + 48 - 16, 0xffffff);

        if (errorText == null) {
            int line1Width = Font.minecraftFont.width("When you click to login button, the login code will be copied to your clipboard,");
            Font.minecraftFont.drawString("When you click to login button, the login code will be copied to your clipboard,", getWidth() / 2 - (line1Width / 2), getHeight() / 4 + 64 - 16, 0xffffff);
            Font.minecraftFont.drawString("and you will be taken to the login page.", getWidth() / 2 - (line1Width / 2), getHeight() / 4 + 74 - 16, 0xffffff);
            Font.minecraftFont.drawString("Or, you can visit " + verificationUrl + " to login on any device.", getWidth() / 2 - (line1Width / 2), getHeight() / 4 + 94 - 16, 0xffffff);
            Font.minecraftFont.drawCenteredString(loginCode, getWidth() / 2, getHeight() / 4 + 48 + 52 + 20 - 22, 0x5555FF);
        }

        Font.minecraftFont.drawString(errorText, (getWidth() / 2) - Font.minecraftFont.width(errorText) / 2, getHeight() / 4 + 64 - 16, 0xFF5555);

        super.drawScreen(mouseX, mouseY);
    }

    private GuiButton retryButton;

    private GuiButton loginButton;
    private GuiButton playOfflineButton;
}
