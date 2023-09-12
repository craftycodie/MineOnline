package com.ahnewark.mineonline.gui.screens;

import com.ahnewark.common.utils.FolderChangeListener;
import com.ahnewark.common.utils.OSUtils;
import com.ahnewark.mineonline.LauncherFiles;
import com.ahnewark.mineonline.Settings;
import com.ahnewark.mineonline.client.LegacyGameManager;
import com.ahnewark.mineonline.client.MinecraftTexturePackRepository;
import com.ahnewark.mineonline.gui.MenuManager;
import com.ahnewark.mineonline.gui.components.GuiButton;
import com.ahnewark.mineonline.gui.components.GuiSmallButton;
import com.ahnewark.mineonline.gui.rendering.Font;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.File;

public class GuiTexturePacks extends AbstractGuiScreen
{
    boolean texturePacksChanged;
    FolderChangeListener texturePackFolderChangeListener = new FolderChangeListener(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH, () -> { texturePacksChanged = true; });

    public GuiTexturePacks(AbstractGuiScreen guiscreen)
    {
        parent = guiscreen;
        MinecraftTexturePackRepository.singleton.loadTexturePacks();
        Settings.singleton.loadSettings();
        MinecraftTexturePackRepository.singleton.getTexturePacks();
        guiTexturePackSlot = new GuiTexturePackSlot(this);

        new Thread(texturePackFolderChangeListener).start();
    }

    @Override
    public void onGuiClosed() {
        texturePackFolderChangeListener.stop();
    }

    public void initGui()
    {
        if (texturePacksChanged) {
            MinecraftTexturePackRepository.singleton.loadTexturePacks();
            texturePacksChanged = false;
        }

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
                done();
            }
        }));
    }

    private void done() {
        if (LegacyGameManager.isInGame())
            LegacyGameManager.setGUIScreen(parent);
        else
            MenuManager.setMenuScreen(parent);
    }

    protected void keyTyped(char c, int i)
    {
        guiTexturePackSlot.keyTyped(c, i);

        if(c == '\r' || i == Keyboard.KEY_ESCAPE)
        {
            done();
        }
    }

    public void drawScreen(int mouseX, int mouseY)
    {
        initGui();

        guiTexturePackSlot.drawScreen(mouseX, mouseY);
        Font.minecraftFont.drawCenteredStringWithShadow("Select Texture Pack", getWidth() / 2, 16, 0xffffff);
        Font.minecraftFont.drawCenteredStringWithShadow("(Place texture pack files here)", getWidth() / 2 - 77, getHeight() - 26, 0x808080);
        super.drawScreen(mouseX, mouseY);
    }

    public void updateScreen()
    {
        super.updateScreen();
        guiTexturePackSlot.update();
    }

    private AbstractGuiScreen parent;
    private GuiTexturePackSlot guiTexturePackSlot;
}
