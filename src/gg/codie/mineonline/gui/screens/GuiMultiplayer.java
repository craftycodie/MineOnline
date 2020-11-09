// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.Settings;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, GuiTextField, StringTranslate, GuiButton, 
//            GameSettings, GuiConnecting

public class GuiMultiplayer extends GuiScreen
{

    public GuiMultiplayer(GuiScreen guiscreen)
    {
        parentScreen = guiscreen;
    }

    public void updateScreen()
    {
        if(textField != null)
            textField.updateCursorCounter();
    }

    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        controlList.clear();
        controlList.add(new GuiButton(0, getWidth() / 2 - 100, getHeight() / 4 + 96 + 12, "Connect"));
        controlList.add(new GuiButton(1, getWidth() / 2 - 100, getHeight() / 4 + 120 + 12, "Cancel"));
        String s = Settings.singleton.getLastServer().replaceAll("_", ":");
        ((GuiButton)controlList.get(0)).enabled = s.length() > 0;
        textField = new GuiTextField(this, getWidth() / 2 - 100, (getHeight() / 4 - 10) + 50 + 18, 200, 20, s);
        textField.isFocused = true;
        textField.setMaxStringLength(128);
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if(!guibutton.enabled)
        {
            return;
        }
        if(guibutton.id == 1)
        {
            MenuManager.setGUIScreen(parentScreen);

            if (parentScreen == null)
                Mouse.setGrabbed(true);
        } else
        if(guibutton.id == 0)
        {
            String s = textField.getText().trim();
//            Settings.singleton.setLastServer(s.replaceAll(":", "_"));
//            Settings.singleton.saveSettings();
            //mc.gameSettings.lastServer = s.replaceAll(":", "_");
            //mc.gameSettings.saveOptions();
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
            //mc.displayGuiScreen(new GuiConnecting(mc, as[0], as.length <= 1 ? 25565 : parseIntWithDefault(as[1], 25565)));
        }
    }

    private int parseIntWithDefault(String s, int i)
    {
        try
        {
            return Integer.parseInt(s.trim());
        }
        catch(Exception exception)
        {
            return i;
        }
    }

    protected void keyTyped(char c, int i)
    {
        textField.textboxKeyTyped(c, i);
        if(c == '\r')
        {
            actionPerformed((GuiButton)controlList.get(0));
        }
        ((GuiButton)controlList.get(0)).enabled = textField.getText().length() > 0;
    }

    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
        textField.mouseClicked(i, j, k);
    }

    @Override
    public void drawScreen(int i, int j)
    {
        controlList.clear();
        initGui();

        drawDefaultBackground();
        drawCenteredString("Play Multiplayer", getWidth() / 2, (getHeight() / 4 - 60) + 20, 0xffffff);
        drawString("Minecraft Multiplayer is currently not finished, but there", getWidth() / 2 - 140, (getHeight() / 4 - 60) + 60 + 0, 0xa0a0a0);
        drawString("is some buggy early testing going on.", getWidth() / 2 - 140, (getHeight() / 4 - 60) + 60 + 9, 0xa0a0a0);
        drawString("Enter the IP of a server to connect to it:", getWidth() / 2 - 140, (getHeight() / 4 - 60) + 60 + 36, 0xa0a0a0);
        textField.drawTextBox();
        super.drawScreen(i, j);
    }

    private GuiScreen parentScreen;
    private GuiTextField textField;
}
