package gg.codie.mineonline.gui.screens;

import com.johnymuffin.BetaEvolutionsUtils;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.api.ClassicServerAuthService;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiTextField;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Font;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.io.IOException;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, GuiTextField, StringTranslate, GuiButton, 
//            GameSettings, GuiConnecting

public class GuiDirectConnect extends AbstractGuiScreen
{
    private final GuiDirectConnect thisScreen = this;

    public GuiDirectConnect(AbstractGuiScreen guiscreen, String serverString)
    {
        parentScreen = guiscreen;
        initGui(serverString);
    }

    public GuiDirectConnect(AbstractGuiScreen guiscreen)
    {
        this(guiscreen, null);
    }

    GuiVersions.IVersionSelectListener selectListener = new GuiVersions.IVersionSelectListener() {
        @Override
        public void onSelect(String path) {
            joinServer(path);
        }
    };

    GuiButton.GuiButtonListener connectButtonHandler = new GuiButton.GuiButtonListener() {
        @Override
        public void OnButtonPress() {
            GuiSlotVersion.ISelectableVersionCompare compare = new GuiSlotVersion.ISelectableVersionCompare() {
                @Override
                public boolean isDefault(GuiSlotVersion.SelectableVersion selectableVersion) {
                    return MinecraftVersionRepository.getSingleton().getLastSelectedJarPath() != null && MinecraftVersionRepository.getSingleton().getLastSelectedJarPath().equals(selectableVersion.path);
                }
            };

            if (LegacyGameManager.isInGame())
                LegacyGameManager.setGUIScreen(new GuiVersions(thisScreen, null, selectListener, compare, false, false));
            else
                MenuManager.setMenuScreen(new GuiVersions(thisScreen, null, selectListener, compare, false, false));
        }
    };

    public void initGui(String serverString)
    {
        Keyboard.enableRepeatEvents(true);
        controlList.clear();

        controlList.add(new GuiButton(0, getWidth() / 2 - 100, getHeight() / 4 + 96 + 12, "Connect", connectButtonHandler));
        controlList.add(new GuiButton(1, getWidth() / 2 - 100, (getHeight() / 4 - 10) + 50 + 18, "Cancel", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parentScreen);
                else
                    MenuManager.setMenuScreen(parentScreen);
            }
        }));
        String s = Settings.singleton.getLastServer().replaceAll("_", ":");
        if (serverString != null)
            s = serverString;
        ((GuiButton)controlList.get(0)).enabled = s.length() > 0;
        textField = new GuiTextField(this, getWidth() / 2 - 100, (getHeight() / 4 - 10) + 50 + 18, 200, 20, s);
        textField.isFocused = true;
        textField.setMaxStringLength(128);
    }

    public void resize() {
        controlList.get(0).resize(getWidth() / 2 - 100, getHeight() / 4 + 96 + 12);
        controlList.get(1).resize(getWidth() / 2 - 100, getHeight() / 4 + 120 + 12);
        textField.resize(getWidth() / 2 - 100, (getHeight() / 4 - 10) + 50 + 18);
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    private void joinServer(String jarPath) {
        String s = textField.getText().trim();
        String as[] = s.split(":");
        if(s.startsWith("["))
        {
            int i = s.indexOf("]");
            if(i > 0)
            {
                String s1 = s.substring(1, i);
                String s2 = s.substring(i + 1).trim();
                if(s2.startsWith(":") && s2.length() > 0)
                {
                    s2 = s2.substring(1);
                    as = new String[2];
                    as[0] = s1;
                    as[1] = s2;
                } else
                {
                    as = new String[1];
                    as[0] = s1;
                }
            }
        }
        if(as.length > 2)
        {
            as = new String[1];
            as[0] = s;
        }

        try {
            try {
                String ip = as[0];
                String port = as.length > 1 ? as[1] : "25565";
                String mppass = classicAuthService.getMPPass(ip, port, Session.session.getAccessToken(), Session.session.getUuid(), Session.session.getUsername());
                MinecraftVersion.launchMinecraft(jarPath, as[0], (as.length <= 1 ? "25565" : as[1]), mppass);
                if (LegacyGameManager.isInGame()) {
//                    if (usingBetaEvolutions) {
                        BetaEvolutionsUtils betaEvolutions = new BetaEvolutionsUtils(true);
                        BetaEvolutionsUtils.VerificationResults verificationResults = betaEvolutions.authenticateUser(Session.session.getUsername(), Session.session.getAccessToken());
                        System.out.println("[Beta Evolutions] Authenticated with " + verificationResults.getSuccessful() + "/" + verificationResults.getTotal() + " BetaEVO nodes.");
//                    }
                    LegacyGameManager.closeGame();
                } else {
                    Display.destroy();
                    DisplayManager.getFrame().setVisible(false);
//                    if(usingBetaEvolutions) {
                        BetaEvolutionsUtils betaEvolutions = new BetaEvolutionsUtils(true);
                        BetaEvolutionsUtils.VerificationResults verificationResults = betaEvolutions.authenticateUser(Session.session.getUsername(), Session.session.getAccessToken());
                        System.out.println("[Beta Evolutions] Authenticated with " + verificationResults.getSuccessful() + "/" + verificationResults.getTotal() + " BetaEVO nodes.");
//                    }
                    DisplayManager.getFrame().dispose();
                    System.exit(0);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                // ignore for now
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // ignore for now
        }
    }

    protected void keyTyped(char c, int i)
    {
        textField.textboxKeyTyped(c, i);
        ((GuiButton)controlList.get(0)).enabled = textField.getText().length() > 0;
        if(c == '\r' && ((GuiButton) controlList.get(0)).enabled)
        {
            connectButtonHandler.OnButtonPress();
        }
    }

    protected void mouseClicked(int x, int y, int button)
    {
        super.mouseClicked(x, y, button);
        textField.mouseClicked(x, y, button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        resize();

        drawDefaultBackground();
        Font.minecraftFont.drawCenteredStringWithShadow("Play Multiplayer", getWidth() / 2, (getHeight() / 4 - 60) + 20, 0xffffff);
        Font.minecraftFont.drawString("Enter the IP of a server to connect to it:", getWidth() / 2 - 140, (getHeight() / 4 - 60) + 60, 0xa0a0a0);

//        FontRenderer.minecraftFont.drawString("Minecraft Multiplayer is currently not finished, but there", getWidth() / 2 - 140, (getHeight() / 4 - 60) + 60, 0xa0a0a0);
//        FontRenderer.minecraftFont.drawString("is some buggy early testing going on.", getWidth() / 2 - 140, (getHeight() / 4 - 60) + 60 + 9, 0xa0a0a0);
//        FontRenderer.minecraftFont.drawString("Enter the IP of a server to connect to it:", getWidth() / 2 - 140, (getHeight() / 4 - 60) + 60 + 36, 0xa0a0a0);
        textField.drawTextBox();
        super.drawScreen(mouseX, mouseY);
    }

    private AbstractGuiScreen parentScreen;
    private GuiTextField textField;
    private ClassicServerAuthService classicAuthService = new ClassicServerAuthService();
}
