// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package gg.codie.mineonline.gui.screens;

// Referenced classes of package net.minecraft.src:
//            GuiScreen, GuiButton, StatCollector, GuiOptions, 
//            StatList, StatFileWriter, World, GuiMainMenu, 
//            GuiAchievements, GuiStats, MathHelper

import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class GuiIngameMenu extends GuiScreen
{

    public GuiIngameMenu()
    {
        updateCounter2 = 0;
        updateCounter = 0;
    }

    public void initGui()
    {
        updateCounter2 = 0;
        controlList.clear();
        byte byte0 = -16;
        controlList.add(new GuiButton(1, getWidth() / 2 - 100, getHeight() / 4 + 120 + byte0, "Mods and Texture Packs"));
//        if(mc.isMultiplayerWorld())
//        {
//            ((GuiButton)controlList.get(0)).displayString = "Join Server";
//        }
        controlList.add(new GuiButton(4, getWidth() / 2 - 100, getHeight() / 4 + 24 + byte0, "Back to game"));
        controlList.add(new GuiButton(0, getWidth() / 2 - 100, getHeight() / 4 + 96 + byte0, "Options..."));
        controlList.add(new GuiButton(5, getWidth() / 2 - 100, getHeight() / 4 + 48 + byte0, "Join Server"));

    }

    protected void actionPerformed(GuiButton guibutton)
    {
        if(guibutton.id == 0)
        {
            MenuManager.setGUIScreen(new GuiIngameOptions(this));
            //mc.displayGuiScreen(new GuiIngameOptions(this));
        }
        if(guibutton.id == 5)
        {
            MenuManager.setGUIScreen(new GuiMultiplayer(this));
            //mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if(guibutton.id == 4)
        {
            MenuManager.setGUIScreen(null);
            Mouse.setGrabbed(true);

            //GLU.gluPerspective(LWJGLPerspectiveAdvice.originalFOV, (float) Display.getWidth() / (float)Display.getHeight(), 0.05F, LWJGLPerspectiveAdvice.zFar);


            //mc.displayGuiScreen(null);
//            mc.setIngameFocus();
        }
        if(guibutton.id == 1)
        {
            MenuManager.setGUIScreen(new GuiTexturePacks(this));
            //mc.displayGuiScreen(new GuiTexturePacks(this));
        }
    }

    public void updateScreen()
    {
        super.updateScreen();
        updateCounter++;
    }

    @Override
    public void drawScreen(int i, int j)
    {
        controlList.clear();
        initGui();

        drawDefaultBackground();
        //boolean flag = !mc.theWorld.func_650_a(updateCounter2++);
//        if(flag || updateCounter < 20)
//        {
//            float f1 = ((float)(updateCounter % 10) + f) / 10F;
//            f1 = (float)Math.sin(f1 * 3.141593F * 2.0F) * 0.2F + 0.8F;
//            int k = (int)(255F * f1);
//            drawString("Saving level..", 8, height - 16, k << 16 | k << 8 | k);
//        }
        drawCenteredString("MineOnline menu", getWidth() / 2, 40, 0xffffff);
        super.drawScreen(i, j);
    }

    private int updateCounter2;
    private int updateCounter;
}
