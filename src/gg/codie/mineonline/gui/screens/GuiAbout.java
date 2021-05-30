package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.rendering.Font;

import java.awt.*;
import java.net.URI;

public class GuiAbout extends AbstractGuiScreen
{
    private AbstractGuiScreen parent;

    public GuiAbout(AbstractGuiScreen parent)
    {
        this.parent = parent;
    }

    public void initGui()
    {
        controlList.clear();
        byte byte0 = -16;
        controlList.add(new GuiButton(1, getWidth() / 2 - 100, getHeight() / 4 + 120 + byte0, "Done", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parent);
                else
                    MenuManager.setMenuScreen(parent);
            }
        }));

        controlList.add(new GuiButton(4, getWidth() / 2 - 100, getHeight() / 4 + 24 + byte0, "Discord", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI("http://discord.codie.gg"));
                    } catch (Exception ex) {

                    }
                }
            }
        }));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        controlList.clear();
        initGui();

        drawDefaultBackground();

        Font.minecraftFont.drawCenteredStringWithShadow("About", getWidth() / 2, 20, 0xffffff);
        super.drawScreen(mouseX, mouseY);
    }
}
