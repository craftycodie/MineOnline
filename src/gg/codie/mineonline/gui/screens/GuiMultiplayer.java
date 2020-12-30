package gg.codie.mineonline.gui.screens;

import com.johnymuffin.BetaEvolutionsUtils;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.api.MineOnlineServerRepository;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Font;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.server.ThreadPollServers;
import org.lwjgl.opengl.Display;
import gg.codie.mineonline.api.ClassicAuthService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class GuiMultiplayer extends AbstractGuiScreen
{
    public GuiMultiplayer(AbstractGuiScreen guiscreen)
    {
        selectedIndex = -1;
        tooltip = null;
        parentScreen = guiscreen;

        serverRepository.onGotServers(gotServersListener);
        serverRepository.loadServers();

        controlList.clear();
        guiSlotServer = new GuiSlotServer(this);
    }

    static MineOnlineServerRepository.GotServersListener gotServersListener = new MineOnlineServerRepository.GotServersListener() {
        @Override
        public void GotServers(LinkedList<MineOnlineServer> servers) {
            for(MineOnlineServer server : servers) {
                if(!ThreadPollServers.serverLatencies.containsKey(server.connectAddress + ":" + server.port))
                    ThreadPollServers.pollServer(server);
            }
        }
    };

    public void initGui()
    {
        controlList.clear();

        controlList.add(connectButton = new GuiButton(1, getWidth() / 2 + 54, getHeight() - 48, 100, 20, "Join Server", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                joinServer(serverRepository.getServers().get(selectedIndex));
            }
        }));
        AbstractGuiScreen thisScreen = this;
        controlList.add(new GuiButton(4, getWidth() / 2 - 50, getHeight() - 48, 100, 20, "Direct Connect", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(new GuiDirectConnect(thisScreen));
                else
                    MenuManager.setMenuScreen(new GuiDirectConnect(thisScreen));
            }
        }));
        controlList.add(new GuiButton(3, getWidth() / 2 - 154, getHeight() - 48, 100, 20, "Cancel", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parentScreen);
                else
                    MenuManager.setMenuScreen(parentScreen);
            }
        }));
        connectButton.enabled = selectedIndex >= 0 && selectedIndex < guiSlotServer.getSize();
    }

    public void onGuiClosed()
    {
        serverRepository.offGotServers(gotServersListener);
    }

    protected void keyTyped(char c, int i)
    {
        if(c == '\r')
        {
            actionPerformed((GuiButton)controlList.get(2));
        }
    }

    protected void mouseClicked(int x, int y, int button)
    {
        super.mouseClicked(x, y, button);
    }

    public void drawScreen(int mouseX, int mouseY)
    {
        initGui();

        tooltip = null;
        drawDefaultBackground();
        guiSlotServer.drawScreen(mouseX, mouseY);
        Font.minecraftFont.drawCenteredString("Play Multiplayer", getWidth() / 2, 20, 0xffffff);
        super.drawScreen(mouseX, mouseY);
        if(tooltip != null)
        {
            Renderer.singleton.renderTooltip(tooltip, mouseX, mouseY);
        }
    }

    public void joinServer(int i)
    {
        joinServer(serverRepository.getServers().get(i));
    }

    private void joinServer(MineOnlineServer server)
    {
        MinecraftVersion serverVersion = MinecraftVersionRepository.getSingleton().getVersionByMD5(server.md5);

        Predicate<GuiSlotVersion.SelectableVersion> selectableVersionPredicate = null;

        if (serverVersion != null) {
            selectableVersionPredicate = (GuiSlotVersion.SelectableVersion selectableVersion) -> {
                return selectableVersion.version != null && (serverVersion.baseVersion.equals(selectableVersion.version.baseVersion) || Arrays.stream(serverVersion.clientVersions).anyMatch(selectableVersion.version.baseVersion::equals));
            };
        }

        GuiVersions.IVersionSelectListener selectListener = new GuiVersions.IVersionSelectListener() {
            @Override
            public void onSelect(String path) {
                try {
                    String mppass = classicAuthService.getMPPass(server.ip, server.port + "", Session.session.getAccessToken(), Session.session.getUuid(), Session.session.getUsername());
                    MinecraftVersion.launchMinecraft(path, server.ip, server.port + "", mppass);

                    if (LegacyGameManager.isInGame()) {
                        if (server.usingBetaEvolutions) {
                            BetaEvolutionsUtils betaEvolutions = new BetaEvolutionsUtils(true);
                            BetaEvolutionsUtils.VerificationResults verificationResults = betaEvolutions.authenticateUser(Session.session.getUsername(), Session.session.getAccessToken());
                            System.out.println("[Beta Evolutions] Authenticated with " + verificationResults.getSuccessful() + "/" + verificationResults.getTotal() + " BetaEVO nodes.");
                        }
                        LegacyGameManager.closeGame();
                    } else {
                        Display.destroy();
                        DisplayManager.getFrame().setVisible(false);
                        if(server.usingBetaEvolutions) {
                            BetaEvolutionsUtils betaEvolutions = new BetaEvolutionsUtils(true);
                            BetaEvolutionsUtils.VerificationResults verificationResults = betaEvolutions.authenticateUser(Session.session.getUsername(), Session.session.getAccessToken());
                            System.out.println("[Beta Evolutions] Authenticated with " + verificationResults.getSuccessful() + "/" + verificationResults.getTotal() + " BetaEVO nodes.");
                        }
                        DisplayManager.getFrame().dispose();
                        System.exit(0);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // ignore for now
                }
            }
        };

        GuiSlotVersion.ISelectableVersionCompare compare = new GuiSlotVersion.ISelectableVersionCompare() {
            @Override
            public boolean isDefault(GuiSlotVersion.SelectableVersion selectableVersion) {
                if (serverVersion == null)
                    return false;
                return selectableVersion.version != null && selectableVersion.version.baseVersion.equals(serverVersion.clientVersions[0]);
            }
        };

        if (LegacyGameManager.isInGame())
            LegacyGameManager.setGUIScreen(new GuiVersions(this, selectableVersionPredicate, selectListener, compare, true, serverVersion != null && !serverVersion.legacy));
        else
            MenuManager.setMenuScreen(new GuiVersions(this, selectableVersionPredicate, selectListener, compare, true, serverVersion != null && !serverVersion.legacy));
    }

    public List<MineOnlineServer> getServers()
    {
        return serverRepository.getServers() != null ? serverRepository.getServers() : new LinkedList<>();
    }

    @Override
    public void updateScreen() {
        guiSlotServer.update();
        super.updateScreen();
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
    private GuiSlotServer guiSlotServer;
    private int selectedIndex;
    private GuiButton connectButton;
    private String tooltip;
    public MineOnlineServerRepository serverRepository = new MineOnlineServerRepository();
    private ClassicAuthService classicAuthService = new ClassicAuthService();
}
