package com.ahnewark.mineonline.gui.screens;

import com.ahnewark.common.utils.OSUtils;
import com.ahnewark.mineonline.Settings;
import com.ahnewark.mineonline.client.LegacyGameManager;
import com.ahnewark.mineonline.gui.MenuManager;
import com.ahnewark.mineonline.gui.components.GuiButton;
import com.ahnewark.mineonline.gui.components.GuiSmallButton;
import com.ahnewark.mineonline.gui.rendering.Font;
import org.lwjgl.input.Keyboard;

public class GuiControls extends AbstractGuiScreen
{

    public GuiControls(AbstractGuiScreen guiscreen)
    {
        screenTitle = "Controls";
        buttonId = -1;
        parentScreen = guiscreen;
    }

    public void initGui()
    {
        int i = getWidth() / 2 - 155;

        int buttonOffset = 0;

        if (!OSUtils.isM1System()) {
            controlList.add(zoomButton = new GuiSmallButton(0, i + (buttonOffset % 2) * 160, getHeight() / 6 + 24 * (buttonOffset >> 1), 70, 20, (buttonId == 0 ? "> " : "") + Keyboard.getKeyName(Settings.singleton.getZoomKeyCode()) + (buttonId == 0 ? " <" : ""), new GuiButton.GuiButtonListener() {
                @Override
                public void OnButtonPress() {
                    buttonId = 0;
                }
            }));
            buttonOffset++;
        }
        controlList.add(menuButton = new GuiSmallButton(1, i + (buttonOffset % 2) * 160, getHeight() / 6 + 24 * (buttonOffset >> 1), 70, 20, (buttonId == 1 ? "> " : "") + Keyboard.getKeyName(Settings.singleton.getMineonlineMenuKeyCode()) + (buttonId == 1 ? " <" : ""), new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                buttonId = 1;
            }
        }));
        buttonOffset++;

        if (!LegacyGameManager.isInGame() || (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().usePlayerList)) {
            controlList.add(playerListButton = new GuiSmallButton(1, i + (buttonOffset % 2) * 160, getHeight() / 6 + 24 * (buttonOffset >> 1), 70, 20, (buttonId == 2 ? "> " : "") + Keyboard.getKeyName(Settings.singleton.getPlayerListKey()) + (buttonId == 2 ? " <" : ""), new GuiButton.GuiButtonListener() {
                @Override
                public void OnButtonPress() {
                    buttonId = 2;
                }
            }));
            buttonOffset++;
        }

        controlList.add(new GuiButton(200, getWidth() / 2 - 100, getHeight() / 6 + 168, "Done", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parentScreen);
                else
                    MenuManager.setMenuScreen(parentScreen);
            }
        }));
        screenTitle = "Controls";
    }

    protected void keyTyped(char c, int i)
    {
        if (i == Keyboard.KEY_ESCAPE)
            i = 0;

        if(buttonId >= 0)
        {
            switch(buttonId) {
                case 0:
                    Settings.singleton.setZoomKeyCode(i);
                    zoomButton.displayString = Keyboard.getKeyName(Settings.singleton.getZoomKeyCode());
                    break;
                case 1:
                    Settings.singleton.setMineonlineMenuKeyCode(i);
                    menuButton.displayString = Keyboard.getKeyName(Settings.singleton.getMineonlineMenuKeyCode());
                    break;
                case 2:
                    Settings.singleton.setPlayerListKey(i);
                    playerListButton.displayString = Keyboard.getKeyName(Settings.singleton.getPlayerListKey());
                    break;
            }
            Settings.singleton.saveSettings();
            buttonId = -1;
        } else
        {
            super.keyTyped(c, i);
        }
    }

    public void drawScreen(int mouseX, int mouseY)
    {
        controlList.clear();
        initGui();

        drawDefaultBackground();
        Font.minecraftFont.drawCenteredStringWithShadow(screenTitle, getWidth() / 2, 20, 0xffffff);
        int k = getWidth() / 2 - 155;

        int buttonOffset = 0;

        if (!OSUtils.isM1System()) {
            Font.minecraftFont.drawString("Zoom", k + (buttonOffset % 2) * 160 + 70 + 6, getHeight() / 6 + 24 * (buttonOffset >> 1) + 7, -1);
            buttonOffset++;
        }
        Font.minecraftFont.drawString("MineOnline Menu", k + (buttonOffset % 2) * 160 + 70 + 6, getHeight() / 6 + 24 * (buttonOffset >> 1) + 7, -1);
        buttonOffset++;

        if (!LegacyGameManager.isInGame() || (LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().usePlayerList)) {
            Font.minecraftFont.drawString("Player List", k + (buttonOffset % 2) * 160 + 70 + 6, getHeight() / 6 + 24 * (buttonOffset >> 1) + 7, -1);
        }

        super.drawScreen(mouseX, mouseY);
    }

    private AbstractGuiScreen parentScreen;
    private String screenTitle;
    private int buttonId;
    private GuiButton zoomButton;
    private GuiButton menuButton;
    private GuiButton playerListButton;
}
