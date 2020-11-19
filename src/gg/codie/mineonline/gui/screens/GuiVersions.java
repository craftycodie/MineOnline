package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.api.MineOnlineServerRepository;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.rendering.FontRenderer;
import gg.codie.mineonline.utils.JREUtils;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GuiVersions extends AbstractGuiScreen
{
    public GuiVersions(AbstractGuiScreen guiscreen)
    {
        selectedIndex = -1;
        tooltip = null;
        parentScreen = guiscreen;

        initGui();
    }

    public void updateScreen()
    {
    }

    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        controlList.clear();
        guiSlotServer = new GuiSlotVersion(this);
        func_35337_c();
    }

    public void func_35337_c()
    {
        controlList.add(new GuiButton(1, getWidth() / 2 - 154, getHeight() - 48, 100, 20, "Back", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parentScreen);
                else
                    MenuManager.setMenuScreen(parentScreen);
            }
        }));
        controlList.add(new GuiButton(4, getWidth() / 2 - 50, getHeight() - 48, 20, 20, "c", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {

            }
        }));
        controlList.add(new GuiButton(4, getWidth() / 2 - 30, getHeight() - 48, 20, 20, "in", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {

            }
        }));
        controlList.add(new GuiButton(4, getWidth() / 2 - 10, getHeight() - 48, 20, 20, "\u00F0", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {

            }
        }));
        controlList.add(new GuiButton(4, getWidth() / 2 + 10, getHeight() - 48, 20, 20, "\u00F1", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {

            }
        }));
        controlList.add(new GuiButton(4, getWidth() / 2 + 30, getHeight() - 48, 20, 20, "+", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {

            }
        }));
        controlList.add(new GuiButton(3, getWidth() / 2 + 4 + 50, getHeight() - 48, 100, 20, "Select", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parentScreen);
                else
                    MenuManager.setMenuScreen(parentScreen);            }
        }));
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void keyTyped(char c, int i)
    {
        if(c == '\r')
        {
            actionPerformed((GuiButton)controlList.get(2));
        }
    }

    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
    }

    public void drawScreen(int i, int j)
    {
        tooltip = null;
        drawDefaultBackground();
        guiSlotServer.drawScreen(i, j);
        drawCenteredString("Select Version", getWidth() / 2, 20, 0xffffff);
        super.drawScreen(i, j);
        if(tooltip != null)
        {
            renderTooltip(tooltip, i, j);
        }
    }

    protected void renderTooltip(String s, int i, int j)
    {
        if(s == null)
        {
            return;
        } else
        {
            int k = i + 12;
            int l = j - 12;
            int i1 = FontRenderer.minecraftFontRenderer.getStringWidth(s);
            drawGradientRect(k - 3, l - 3, k + i1 + 3, l + 8 + 3, 0xc0000000, 0xc0000000);
            FontRenderer.minecraftFontRenderer.drawStringWithShadow(s, k, l, -1);
            return;
        }
    }

    public String setTooltip(String s)
    {
        return tooltip = s;
    }

    private AbstractGuiScreen parentScreen;
    private GuiSlotVersion guiSlotServer;
    private int selectedIndex;
    private String tooltip;
}
