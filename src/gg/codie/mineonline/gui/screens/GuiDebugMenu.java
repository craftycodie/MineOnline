package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.rendering.Font;

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
