package gg.codie.mineonline.gui.screens;

import gg.codie.common.utils.OSUtils;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.MinecraftTexturePackRepository;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiSmallButton;
import gg.codie.mineonline.gui.rendering.FontRenderer;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.textures.EGUITexture;
import gg.codie.mineonline.patches.HashMapPutAdvice;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;

public class GuiTexturePacks extends GuiScreen
{
    Class minecraftClass;

    public GuiTexturePacks(GuiScreen guiscreen, Class minecraftClass)
    {
        field_6454_o = -1;
        parent = guiscreen;
        this.minecraftClass = minecraftClass;
        MinecraftTexturePackRepository.singleton.loadTexturePacks();
        Settings.singleton.loadSettings();
        initGui();
    }

    public void initGui()
    {
        controlList.add(new GuiSmallButton(5, getWidth() / 2 - 154, getHeight() - 48, "Open texture pack folder"));
        controlList.add(new GuiSmallButton(6, getWidth() / 2 + 4, getHeight() - 48, "Done"));
        MinecraftTexturePackRepository.singleton.getTexturePacks();
        guiTexturePackSlot = new GuiTexturePackSlot(this);
        guiTexturePackSlot.registerScrollButtons(controlList, 7, 8);
    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if(!guibutton.enabled)
        {
            return;
        }
        if(guibutton.id == 5)
        {
            if (OSUtils.isWindows())
                Sys.openURL((new StringBuilder()).append("file://").append(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH).toString());
            else
                try {
                    Desktop.getDesktop().open(new File(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH));
                } catch (Exception ex) { }
        } else
        if(guibutton.id == 6)
        {
            //Loader.singleton.unloadTexture(EGUITexture.DEFAULT_PACK.textureName);
            Loader.singleton.unloadTexture(EGUITexture.BACKGROUND);
            Loader.singleton.unloadTexture(EGUITexture.GUI);
            Loader.singleton.unloadTexture(EGUITexture.GUI_ICONS);
            Loader.singleton.unloadTexture(EGUITexture.UNKNOWN_PACK);
            Loader.singleton.unloadTexture(EGUITexture.FONT);
            Loader.reloadMinecraftTextures(minecraftClass);
            FontRenderer.reloadFont();

            MenuManager.setGUIScreen(parent);
        } else
        {
            guiTexturePackSlot.actionPerformed(guibutton);
        }
    }

    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
    }

    protected void mouseMovedOrUp(int i, int j, int k)
    {
        super.mouseMovedOrUp(i, j, k);
    }

    public void drawScreen(int i, int j)
    {
        guiTexturePackSlot.drawScreen(i, j);
        if(field_6454_o <= 0)
        {
            MinecraftTexturePackRepository.singleton.getTexturePacks();
            field_6454_o += 20;
        }
        drawCenteredString("Select Texture Pack", getWidth() / 2, 16, 0xffffff);
        drawCenteredString("(Place texture pack files here)", getWidth() / 2 - 77, getHeight() - 26, 0x808080);
        super.drawScreen(i, j);
    }

    public void updateScreen()
    {
        super.updateScreen();
        field_6454_o--;
    }

    protected GuiScreen parent;
    private int field_6454_o;
    private GuiTexturePackSlot guiTexturePackSlot;
}
