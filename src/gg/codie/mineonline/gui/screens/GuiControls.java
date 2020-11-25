package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiSmallButton;
import gg.codie.mineonline.gui.rendering.FontRenderer;
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

        controlList.add(zoomButton = new GuiSmallButton(0, i + (0 % 2) * 160, getHeight() / 6 + 24 * (0 >> 1), 70, 20, (buttonId == 0 ? "> " : "") + Keyboard.getKeyName(Settings.singleton.getZoomKeyCode()) + (buttonId == 0 ? " <" : ""), new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                buttonId = 0;
            }
        }));
        controlList.add(menuButton = new GuiSmallButton(1, i + (1 % 2) * 160, getHeight() / 6 + 24 * (1 >> 1), 70, 20, (buttonId == 1 ? "> " : "") + Keyboard.getKeyName(Settings.singleton.getMineonlineMenuKeyCode()) + (buttonId == 1 ? " <" : ""), new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                buttonId = 1;
            }
        }));

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
        FontRenderer.minecraftFontRenderer.drawCenteredString(screenTitle, getWidth() / 2, 20, 0xffffff);
        int k = getWidth() / 2 - 155;
        FontRenderer.minecraftFontRenderer.drawString("Zoom", k + (0 % 2) * 160 + 70 + 6, getHeight() / 6 + 24 * (0 >> 1) + 7, -1);
        FontRenderer.minecraftFontRenderer.drawString("MineOnline Menu", k + (1 % 2) * 160 + 70 + 6, getHeight() / 6 + 24 * (1 >> 1) + 7, -1);

        super.drawScreen(mouseX, mouseY);
    }

    private AbstractGuiScreen parentScreen;
    private String screenTitle;
    private int buttonId;
    private GuiButton zoomButton;
    private GuiButton menuButton;
}
