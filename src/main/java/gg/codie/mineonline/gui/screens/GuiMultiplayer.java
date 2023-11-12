package gg.codie.mineonline.gui.screens;

import gg.codie.common.utils.SHA1Utils;
import gg.codie.minecraft.api.SessionServer;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.ClassicServerAuthService;
import gg.codie.mineonline.api.SavedMinecraftServer;
import gg.codie.mineonline.api.SavedServerRepository;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.server.ThreadPollServers;
import com.johnymuffin.BetaEvolutionsUtils;
import com.johnymuffin.LegacyTrackerServer;
import com.johnymuffin.LegacyTrackerServerRepository;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Font;
import gg.codie.mineonline.gui.rendering.Renderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

public class GuiMultiplayer extends AbstractGuiScreen
{
    public GuiMultiplayer(AbstractGuiScreen guiscreen)
    {
        selectedIndex = -1;
        tooltip = null;
        parentScreen = guiscreen;

        savedServerRepository.onGotServers(gotServersListener);
        savedServerRepository.loadServers();

        listedServerRepository.onGotServers(gotListdServersListener);
        listedServerRepository.loadServers();

        controlList.clear();
        guiSlotServer = new GuiSlotServer(this);
    }

    static SavedServerRepository.GotServersListener gotServersListener = new SavedServerRepository.GotServersListener() {
        @Override
        public void GotServers(LinkedList<SavedMinecraftServer> servers) {
            for(SavedMinecraftServer server : servers) {
                if(!ThreadPollServers.serverLatencies.containsKey(server.address))
                    ThreadPollServers.pollServer(server.address);
            }
        }
    };

    static LegacyTrackerServerRepository.GotServersListener gotListdServersListener = new LegacyTrackerServerRepository.GotServersListener() {
        @Override
        public void GotServers(LinkedList<LegacyTrackerServer> servers) {
            for(LegacyTrackerServer server : servers) {
                if(!ThreadPollServers.serverLatencies.containsKey(server.ip + ":" + server.port))
                    ThreadPollServers.pollServer(server.ip + ":" + server.port);
            }
        }
    };

    protected void keyTyped(char c, int i)
    {
        guiSlotServer.keyTyped(c, i);

        if(c == '\r')
        {
            joinServer(getSelectedIndex());
        }
        else if (i == Keyboard.KEY_ESCAPE) {
            if (LegacyGameManager.isInGame())
                LegacyGameManager.setGUIScreen(parentScreen);
            else
                MenuManager.setMenuScreen(parentScreen);
        }
    }

    public void initGui()
    {
        controlList.clear();

        controlList.add(connectButton = new GuiButton(1, getWidth() / 2 - 154, getHeight() - 48, 100, 20, "Join Server", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                joinServer(getSelectedIndex());
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
        controlList.add(new GuiButton(3, getWidth() / 2 + 54, getHeight() - 48, 100, 20, "Add Server", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(new GuiEditServer(thisScreen));
                else
                    MenuManager.setMenuScreen(new GuiEditServer(thisScreen));
            }
        }));

        controlList.add(new GuiButton(3, getWidth() / 2 + 79, getHeight() - 24, 75, 20, "Cancel", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parentScreen);
                else
                    MenuManager.setMenuScreen(parentScreen);
            }
        }));

        controlList.add(new GuiButton(3, getWidth() / 2 + 3, getHeight() - 24, 70, 20, "Refresh", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                ThreadPollServers.serverLatencies.clear();
                for(SavedMinecraftServer server : SavedServerRepository.getSingleton().getServers()) {
                    ThreadPollServers.pollServer(server.address);
                }

                listedServerRepository.loadServers();
            }
        }));

        controlList.add(deleteButton = new GuiButton(3, getWidth() / 2 - 75, getHeight() - 24, 70, 20, "Delete", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                SavedServerRepository.getSingleton().deleteServer(selectedIndex);
                selectedIndex = -1;
            }
        }));


        controlList.add(editButton = new GuiButton(3, getWidth() / 2 - 154, getHeight() - 24, 70, 20, "Edit", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(new GuiEditServer(thisScreen, savedServerRepository.getServers().get(selectedIndex), selectedIndex));
                else
                    MenuManager.setMenuScreen(new GuiEditServer(thisScreen, savedServerRepository.getServers().get(selectedIndex), selectedIndex));
            }
        }));

        connectButton.enabled = selectedIndex >= 0;
        editButton.enabled = selectedIndex >= 0 && selectedIndex <= getSavedServers().size();
        deleteButton.enabled = selectedIndex >= 0 && selectedIndex <= getSavedServers().size();
    }

    public void onGuiClosed()
    {
        savedServerRepository.offGotServers(gotServersListener);
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
        Font.minecraftFont.drawCenteredStringWithShadow("Play Multiplayer", getWidth() / 2, 20, 0xffffff);
        super.drawScreen(mouseX, mouseY);
        if(tooltip != null)
        {
            Renderer.singleton.renderTooltip(tooltip, mouseX, mouseY);
        }
    }

    public void joinServer(int i)
    {
        if (i < getSavedServers().size()) {
            SavedMinecraftServer savedMinecraftServer = getSavedServers().get(i);
            joinServer(savedMinecraftServer.address.split(":")[0], savedMinecraftServer.address.contains(":") ? savedMinecraftServer.address.split(":")[1] : "25565", savedMinecraftServer.clientMD5);
        } else {
            LegacyTrackerServer server = getListedServers().get(i - getSavedServers().size() - 1);
            MinecraftVersion serverVersion = null;
            List<MinecraftVersion> versions = MinecraftVersionRepository.getSingleton().getVersionsByBaseVersion(server.baseVersion);
            if (versions.size() > 0)
                joinServer(server.ip, "" + server.port, versions.get(0).md5);
            else
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "Failed to join server: find a compatible version.");
                    }
                });
        }
    }

    private void joinServer(String ip, String port, String clientMD5)
    {
        MinecraftVersion clientVersion = MinecraftVersionRepository.getSingleton().getVersionByMD5(clientMD5);
        String jarPath = null;

        for (String path : MinecraftVersionRepository.getSingleton().getInstalledJars().keySet()) {
            if (MinecraftVersionRepository.getSingleton().getInstalledJars().get(path) != null && MinecraftVersionRepository.getSingleton().getInstalledJars().get(path).md5.equals(clientMD5)) {
                jarPath = path;
                break;
            }
        }

        if (jarPath == null) {
            if (clientVersion.downloadURL != null)
                try {
                    jarPath = clientVersion.download();
                } catch (Exception ex) {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            JOptionPane.showMessageDialog(null, "Failed to launch Minecraft.");
                        }
                    });
            }
            else {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "Couldn't find a compatible version.");
                    }
                });
            }
        }

        try {
            String mppass = null;

            MinecraftVersion minecraftVersion = MinecraftVersionRepository.getSingleton().getVersion(jarPath);
            if (minecraftVersion != null && minecraftVersion.cantVerifyName) {
                InetAddress inetAddress = InetAddress.getByName(ip);
                ip = inetAddress.getHostAddress();

                SessionServer.joinGame(Session.session.getAccessToken(), Session.session.getUuid(), SHA1Utils.sha1(ip + ":" + port));
            } else {
                mppass = classicAuthService.getMPPass(ip, port, Session.session.getAccessToken(), Session.session.getUuid(), Session.session.getUsername());
            }

            MinecraftVersion.launchMinecraft(jarPath, ip, port, mppass);

            if (LegacyGameManager.isInGame()) {
                BetaEvolutionsUtils betaEvolutions = new BetaEvolutionsUtils(true);
                BetaEvolutionsUtils.VerificationResults verificationResults = betaEvolutions.authenticateUser(Session.session.getUsername(), Session.session.getAccessToken());
                System.out.println("[Beta Evolutions] Authenticated with " + verificationResults.getSuccessful() + "/" + verificationResults.getTotal() + " BetaEVO nodes.");
                LegacyGameManager.closeGame();
            } else {
                Display.destroy();
                DisplayManager.getFrame().setVisible(false);
                BetaEvolutionsUtils betaEvolutions = new BetaEvolutionsUtils(true);
                BetaEvolutionsUtils.VerificationResults verificationResults = betaEvolutions.authenticateUser(Session.session.getUsername(), Session.session.getAccessToken());
                System.out.println("[Beta Evolutions] Authenticated with " + verificationResults.getSuccessful() + "/" + verificationResults.getTotal() + " BetaEVO nodes.");
                DisplayManager.getFrame().dispose();
                System.exit(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // ignore for now
        }
    }

    public List<SavedMinecraftServer> getSavedServers()
    {
        return savedServerRepository.getServers() != null ? savedServerRepository.getServers() : new LinkedList<>();
    }

    public List<LegacyTrackerServer> getListedServers()
    {
        return listedServerRepository.getServers() != null ? listedServerRepository.getServers() : new LinkedList<>();
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
    private GuiButton editButton;
    private GuiButton deleteButton;

    private String tooltip;
    public SavedServerRepository savedServerRepository = SavedServerRepository.getSingleton();
    public LegacyTrackerServerRepository listedServerRepository = new LegacyTrackerServerRepository();
    private ClassicServerAuthService classicAuthService = new ClassicServerAuthService();
}
