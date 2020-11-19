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
        controlList.add(connectButton = new GuiButton(1, getWidth() / 2 - 154, getHeight() - 48, 100, 20, "Back", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parentScreen);
                else
                    MenuManager.setMenuScreen(parentScreen);
            }
        }));
        AbstractGuiScreen thisScreen = this;
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
        connectButton.enabled = selectedIndex >= 0 && selectedIndex < guiSlotServer.getSize();
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

    public void joinServer(int i)
    {
        joinServer(serverRepository.getServers().get(i));
    }

    private void joinServer(MineOnlineServer server)
    {
        try {
            LinkedList<String> launchArgs = new LinkedList();
            launchArgs.add(JREUtils.getRunningJavaExecutable());
            launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
            launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");
            launchArgs.add("-cp");
            launchArgs.add(LibraryManager.getClasspath(true, new String[]{new File(MenuManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(), LauncherFiles.DISCORD_RPC_JAR}));
            launchArgs.add(MenuManager.class.getCanonicalName());
            launchArgs.add("-quicklaunch");
            launchArgs.add("-joinserver");
            launchArgs.add(server.ip + ":" + server.port);
            launchArgs.add("-skipupdates");

            java.util.Properties props = System.getProperties();
            ProcessBuilder processBuilder = new ProcessBuilder(launchArgs);

            Map<String, String> env = processBuilder.environment();
            for (String prop : props.stringPropertyNames()) {
                env.put(prop, props.getProperty(prop));
            }
            processBuilder.directory(new File(System.getProperty("user.dir")));

            Process launcherProcess = processBuilder.inheritIO().start();

            LegacyGameManager.closeGame();
        } catch (Exception ex) {
            ex.printStackTrace();
            // ignore for now
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

    public List<MineOnlineServer> getServers()
    {
        return serverRepository.getServers() != null ? serverRepository.getServers() : new LinkedList<>();
    }

    public int select(int i)
    {
        return selectedIndex = i;
    }

    public int getSelectedIndex()
    {
        return selectedIndex;
    }

    public GuiButton getConnectButton()
    {
        return connectButton;
    }

    public String setTooltip(String s)
    {
        return tooltip = s;
    }

    private AbstractGuiScreen parentScreen;
    private GuiSlotVersion guiSlotServer;
    private int selectedIndex;
    private GuiButton connectButton;
    private String tooltip;
    private MineOnlineServerRepository serverRepository = new MineOnlineServerRepository();
}
