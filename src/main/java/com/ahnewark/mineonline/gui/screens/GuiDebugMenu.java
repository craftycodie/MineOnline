package com.ahnewark.mineonline.gui.screens;

import com.ahnewark.mineonline.Settings;
import com.ahnewark.mineonline.client.LegacyGameManager;
import com.ahnewark.mineonline.gui.components.GuiButton;
import com.ahnewark.mineonline.gui.rendering.Font;

public class GuiDebugMenu extends AbstractGuiScreen
{
    public void initGui()
    {
        controlList.clear();
        byte byte0 = -16;

        controlList.add(new GuiButton(4, getWidth() / 2 - 100, getHeight() / 4 + 24 + byte0, "Back to game", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                LegacyGameManager.setGUIScreen(null);
            }
        }));

        AbstractGuiScreen thisScreen = this;

        controlList.add(new GuiButton(5, getWidth() / 2 - 100, getHeight() / 4 + 48 + byte0, 98, 20, "Texture Packs", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                LegacyGameManager.setGUIScreen(new GuiTexturePacks(thisScreen));
            }
        }));
        controlList.add(new GuiButton(6, getWidth() / 2 + 2, getHeight() / 4 + 48 + byte0, 98, 20, "Reset Toast", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                Settings.singleton.setMenuToast(true);
                Settings.singleton.setScreenshotToast(true);
                Settings.singleton.setZoomToast(true);
                Settings.singleton.setPlayerListToast(true);
                Settings.singleton.saveSettings();
            }
        }));
    }

    public void updateScreen()
    {
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        controlList.clear();
        initGui();

        drawDefaultBackground();
        Font.minecraftFont.drawCenteredStringWithShadow("MineOnline Debug menu", getWidth() / 2, 40, 0xffffff);
        super.drawScreen(mouseX, mouseY);
    }
}
