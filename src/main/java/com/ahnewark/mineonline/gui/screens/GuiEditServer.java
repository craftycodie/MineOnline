package com.ahnewark.mineonline.gui.screens;

import com.ahnewark.mineonline.MinecraftVersion;
import com.ahnewark.mineonline.MinecraftVersionRepository;
import com.ahnewark.mineonline.api.SavedMinecraftServer;
import com.ahnewark.mineonline.api.SavedServerRepository;
import com.ahnewark.mineonline.client.LegacyGameManager;
import com.ahnewark.mineonline.gui.MenuManager;
import com.ahnewark.mineonline.server.ThreadPollServers;
import com.ahnewark.mineonline.gui.components.GuiButton;
import com.ahnewark.mineonline.gui.components.GuiTextField;
import com.ahnewark.mineonline.gui.rendering.Font;
import org.lwjgl.input.Keyboard;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, GuiTextField, StringTranslate, GuiButton, 
//            GameSettings, GuiConnecting

public class GuiEditServer extends AbstractGuiScreen
{
    private final GuiEditServer thisScreen = this;
    private SavedMinecraftServer server;
    private int index;

    public GuiEditServer(AbstractGuiScreen guiscreen)
    {
        server = new SavedMinecraftServer();
        index = -1;
        server.name = "Minecraft Server";
        parentScreen = guiscreen;
        initGui();
    }

    public GuiEditServer(AbstractGuiScreen guiscreen, SavedMinecraftServer server, int index)
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
                
            Keyboard.enableRepeatEvents(true);
        }
    };

    GuiButton.GuiButtonListener doneButtonHandler = new GuiButton.GuiButtonListener() {
        @Override
        public void OnButtonPress() {
            if (index < 0) {
                SavedServerRepository.getSingleton().addServer(server);
            } else {
                SavedServerRepository.getSingleton().editServer(server, index);
            }
            ThreadPollServers.pollServer(server.address);
            if (LegacyGameManager.isInGame())
                LegacyGameManager.setGUIScreen(parentScreen);
            else
                MenuManager.setMenuScreen(parentScreen);
        }
    };

    public void selectNextField() {
        if (nameTextField.isFocused) {
            nameTextField.setFocused(false);
            addressTextField.setFocused(true);
        }
    }

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

        addressTextField = new GuiTextField(this, getWidth() / 2 - 100, (getHeight() / 4 - 10) + 50 + 18, 200, 20, server.address);
        addressTextField.isFocused = true;
        addressTextField.setMaxStringLength(128);
        addressTextField.disableSpaces();

        nameTextField = new GuiTextField(this, getWidth() / 2 - 100, (getHeight() / 4 - 10) + 50 + 22, 200, 20, server.name);
        nameTextField.isFocused = false;
        nameTextField.setMaxStringLength(128);

        ((GuiButton)controlList.get(0)).enabled = addressTextField.getText().length() > 0 && nameTextField.getText().length() > 0 && server.clientMD5 != null;
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
        if(c == '\r' && addressTextField.isFocused && ((GuiButton) controlList.get(0)).enabled) {
            doneButtonHandler.OnButtonPress();
            return;
        }

        nameTextField.textboxKeyTyped(c, i);
        addressTextField.textboxKeyTyped(c, i);

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
