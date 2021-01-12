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
    public GuiLogin()
    {
        initGui();
    }

    protected void mouseClicked(int x, int y, int button)
    {
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

    public void initGui()
    {
        int i = getHeight() / 4 + 48;

        controlList.add(loginLegacy = new GuiButton(0, getWidth() / 2 - 100, i + 72, "Login via Mojang", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                MenuManager.setMenuScreen(new GuiLoginLegacy());
            }
        }));
        controlList.add(loginMicrosoft = new GuiButton(0, getWidth() / 2 - 100, i + 72, "Login via Microsoft", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                // TODO: Init Microsoft Login.
            }
        }));
    }

    public void resize() {
        loginLegacy.resize(getWidth() / 2 - 102, getHeight() / 4 + 48 + 32);
        loginMicrosoft.resize(getWidth() / 2 - 102, getHeight() / 4 + 48 + 72);
    }

    public void drawScreen(int mouseX, int mouseY)
    {
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

        super.drawScreen(mouseX, mouseY);
    }

    private GuiButton loginLegacy;
    private GuiButton loginMicrosoft;
}
