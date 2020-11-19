package gg.codie.mineonline.gui.screens;

import gg.codie.common.utils.OSUtils;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.client.MinecraftTexturePackRepository;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiSmallButton;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.File;

public class GuiTexturePacks extends AbstractGuiScreen
{
    public GuiTexturePacks(AbstractGuiScreen guiscreen)
    {
        field_6454_o = -1;
        parent = guiscreen;
        MinecraftTexturePackRepository.singleton.loadTexturePacks();
        Settings.singleton.loadSettings();
        MinecraftTexturePackRepository.singleton.getTexturePacks();
        guiTexturePackSlot = new GuiTexturePackSlot(this);
    }

    public void initGui()
    {
        controlList.clear();

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
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parent);
                else
                    MenuManager.setMenuScreen(parent);
            }
        }));
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
        initGui();

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
        guiTexturePackSlot.update();
        field_6454_o--;
    }

    protected AbstractGuiScreen parent;
    private int field_6454_o;
    private GuiTexturePackSlot guiTexturePackSlot;
}
