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
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;

public class GuiTexturePacks extends GuiScreen
{
    public GuiTexturePacks(GuiScreen guiscreen)
    {
        field_6454_o = -1;
        parent = guiscreen;
        MinecraftTexturePackRepository.singleton.loadTexturePacks();
        Settings.singleton.loadSettings();
        initGui();
    }

    public void initGui()
    {
        controlList.add(new GuiSmallButton(5, getWidth() / 2 - 154, getHeight() - 48, "Open texture pack folder", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (OSUtils.isWindows())
                    Sys.openURL((new StringBuilder()).append("file://").append(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH).toString());
                else
                    try {
                        Desktop.getDesktop().open(new File(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH));
                    } catch (Exception ex) { }
            }
        }));
        controlList.add(new GuiSmallButton(6, getWidth() / 2 + 4, getHeight() - 48, "Done", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                MenuManager.setGUIScreen(parent);
                if (parent == null) {
                    Mouse.setGrabbed(true);
                }
            }
        }));
        MinecraftTexturePackRepository.singleton.getTexturePacks();
        guiTexturePackSlot = new GuiTexturePackSlot(this);
        guiTexturePackSlot.registerScrollButtons(controlList, 7, 8);
    }

//    protected void actionPerformed(GuiButton guibutton)
//    {
//            guiTexturePackSlot.actionPerformed(guibutton);
//
//    }

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
