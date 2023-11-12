package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.Globals;
import gg.codie.mineonline.Session;
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
        controlList.add(new GuiButton(1, getWidth() / 2 - 100, getHeight() / 6 + 168, "Done", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parent);
                else
                    MenuManager.setMenuScreen(parent);
            }
        }));

        controlList.add(new GuiButton(4, getWidth() / 2 - 100, getHeight() / 6 + 120 + 12, "Omniarchive Discord", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://discord.gg/h45wxnE"));
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
        Font.minecraftFont.drawCenteredString("MineOnline " + Globals.BRANCH + " "  + Globals.LAUNCHER_VERSION + (Globals.DEV ? " (dev)" : ""), getWidth() / 2, (getHeight() / 4 - 60) + 60, 0xa0a0a0);
        Font.minecraftFont.drawCenteredString("running on " + System.getProperty("os.name") + ", Java " + System.getProperty("java.version") + ".", getWidth() / 2, (getHeight() / 4 - 60) + 72, 0xa0a0a0);

        Font.minecraftFont.drawCenteredString("Logged in as " + Session.session.getUsername() + ".", getWidth() / 2, (getHeight() / 4 - 60) + 96, 0xa0a0a0);
        Font.minecraftFont.drawCenteredString("Versions provided by Omniarchive", getWidth() / 2, (getHeight() / 4 - 60) + 120, 0xa0a0a0);

        super.drawScreen(mouseX, mouseY);
    }
}
