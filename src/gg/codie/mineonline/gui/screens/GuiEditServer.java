package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.api.MineOnlineServerRepository;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiTextField;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Font;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, GuiTextField, StringTranslate, GuiButton, 
//            GameSettings, GuiConnecting

public class GuiEditServer extends AbstractGuiScreen
{
    private final GuiEditServer thisScreen = this;
    private MineOnlineServer server;
    private int index;

    public GuiEditServer(AbstractGuiScreen guiscreen)
    {
        server = new MineOnlineServer();
        index = -1;
        server.name = "Minecraft Server";
        parentScreen = guiscreen;
        initGui();
    }

    public GuiEditServer(AbstractGuiScreen guiscreen, MineOnlineServer server, int index)
    {
        this.server = server;
        this.index = index;
        parentScreen = guiscreen;
        initGui();
    }

    GuiVersions.IVersionSelectListener selectListener = new GuiVersions.IVersionSelectListener() {
        @Override
        public void onSelect(String path) {
            MinecraftVersion version = MinecraftVersionRepository.getSingleton().getVersion(path);
            server.clientMD5 = version.md5;
            ((GuiButton)controlList.get(2)).displayString = "Version: " + version.name;

            ((GuiButton)controlList.get(0)).enabled = addressTextField.getText().length() > 0 && nameTextField.getText().length() > 0 && server.clientMD5 != null;


            if (LegacyGameManager.isInGame())
                LegacyGameManager.setGUIScreen(thisScreen);
            else
                MenuManager.setMenuScreen(thisScreen);
        }
    };

    GuiButton.GuiButtonListener doneButtonHandler = new GuiButton.GuiButtonListener() {
        @Override
        public void OnButtonPress() {
            if (index < 0) {
                MineOnlineServerRepository.getSingleton().addServer(server);
            } else {
                MineOnlineServerRepository.getSingleton().editServer(server, index);
            }
            if (LegacyGameManager.isInGame())
                LegacyGameManager.setGUIScreen(parentScreen);
            else
                MenuManager.setMenuScreen(parentScreen);
        }
    };

    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        controlList.clear();


        controlList.add(new GuiButton(0, getWidth() / 2 - 100, getHeight() / 4 + 96 + 20, "Done", doneButtonHandler));
        controlList.add(new GuiButton(1, getWidth() / 2 - 100, (getHeight() / 4 - 10) + 50 + 18 + 8, "Cancel", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parentScreen);
                else
                    MenuManager.setMenuScreen(parentScreen);
            }
        }));

        controlList.add(new GuiButton(2, getWidth() / 2 - 100, getHeight() / 4 + 96 + 12, server.clientMD5 != null ? "Version: " + MinecraftVersionRepository.getSingleton().getVersionByMD5(server.clientMD5).name : "Select Version", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                GuiVersions guiVersions = new GuiVersions(thisScreen, null, selectListener, new GuiSlotVersion.ISelectableVersionCompare() {
                    @Override
                    public boolean isDefault(GuiSlotVersion.SelectableVersion selectableVersion) {
                        return MinecraftVersionRepository.getSingleton().getLastSelectedJarPath() != null && MinecraftVersionRepository.getSingleton().getLastSelectedJarPath().equals(selectableVersion.path);
                    }
                },
                false, false);

                guiVersions.setSelectButtonText("Select");

                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(guiVersions);
                else
                    MenuManager.setMenuScreen(guiVersions);
            }
        }));

        ((GuiButton)controlList.get(0)).enabled = false;
        addressTextField = new GuiTextField(this, getWidth() / 2 - 100, (getHeight() / 4 - 10) + 50 + 18, 200, 20, server.address);
        addressTextField.isFocused = true;
        addressTextField.setMaxStringLength(128);

        nameTextField = new GuiTextField(this, getWidth() / 2 - 100, (getHeight() / 4 - 10) + 50 + 22, 200, 20, server.name);
        nameTextField.isFocused = false;
        nameTextField.setMaxStringLength(128);
    }

    public void resize() {
        controlList.get(0).resize(getWidth() / 2 - 100, getHeight() / 4 + 96 + 18);
        controlList.get(1).resize(getWidth() / 2 - 100, getHeight() / 4 + 120 + 18);
        controlList.get(2).resize(getWidth() / 2 - 100, getHeight() / 4 + 72);
        addressTextField.resize(getWidth() / 2 - 100, (getHeight() / 4 - 10) + 56);
        nameTextField.resize(getWidth() / 2 - 100, (getHeight() / 4 - 10) + 16);
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void keyTyped(char c, int i)
    {
        addressTextField.textboxKeyTyped(c, i);
        if(c == '\r' && ((GuiButton) controlList.get(0)).enabled)
        {
            doneButtonHandler.OnButtonPress();
        }

        nameTextField.textboxKeyTyped(c, i);
        if(c == '\r' && ((GuiButton) controlList.get(0)).enabled)
        {
            doneButtonHandler.OnButtonPress();
        }

        server.address = addressTextField.getText();
        server.name = nameTextField.getText();

        ((GuiButton)controlList.get(0)).enabled = addressTextField.getText().length() > 0 && nameTextField.getText().length() > 0 && server.clientMD5 != null;
    }

    protected void mouseClicked(int x, int y, int button)
    {
        super.mouseClicked(x, y, button);
        addressTextField.mouseClicked(x, y, button);
        nameTextField.mouseClicked(x, y, button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        resize();

        drawDefaultBackground();
        Font.minecraftFont.drawCenteredStringWithShadow("Edit Server Info", getWidth() / 2, (getHeight() / 4 - 60) + 20, 0xffffff);
        Font.minecraftFont.drawString("Server Name", getWidth() / 2 - 100, (getHeight() / 4 - 10) + 4, 0xa0a0a0);
        Font.minecraftFont.drawString("Server Address", getWidth() / 2 - 100, (getHeight() / 4 - 10) + 44, 0xa0a0a0);

        addressTextField.drawTextBox();
        nameTextField.drawTextBox();
        super.drawScreen(mouseX, mouseY);
    }

    private AbstractGuiScreen parentScreen;
    private GuiTextField addressTextField;
    private GuiTextField nameTextField;
}
