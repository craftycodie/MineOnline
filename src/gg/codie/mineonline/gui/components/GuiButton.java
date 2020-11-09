package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.textures.EGUITexture;
import org.lwjgl.opengl.GL11;

public class GuiButton extends GuiComponent
{

    public GuiButton(int i, int xPos, int yPos, String s)
    {
        this(i, xPos, yPos, 200, 20, s);
    }

    public GuiButton(int i, int xPos, int yPos, int l, int i1, String s)
    {
        width = 200;
        height = 20;
        enabled = true;
        enabled2 = true;
        id = i;
        xPosition = xPos;
        yPosition = yPos;
        width = l;
        height = i1;
        displayString = s;
    }

    protected int getHoverState(boolean flag)
    {
        byte byte0 = 1;
        if(!enabled)
        {
            byte0 = 0;
        } else
        if(flag)
        {
            byte0 = 2;
        }
        return byte0;
    }

    public void drawButton(int i, int j)
    {
        if(!enabled2)
        {
            return;
        }
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, Loader.singleton.getGuiTexture(EGUITexture.GUI));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        boolean flag = i >= xPosition && j >= yPosition && i < xPosition + width && j < yPosition + height;
        int k = getHoverState(flag);
        drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
        drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + k * 20, width / 2, height);
        mouseDragged(i, j);
        if(!enabled)
        {
            drawCenteredString(displayString, xPosition + width / 2, yPosition + (height - 8) / 2, 0xffa0a0a0);
        } else
        if(flag)
        {
            drawCenteredString(displayString, xPosition + width / 2, yPosition + (height - 8) / 2, 0xffffa0);
        } else
        {
            drawCenteredString(displayString, xPosition + width / 2, yPosition + (height - 8) / 2, 0xe0e0e0);
        }
    }

    protected void mouseDragged(int i, int j)
    {
    }

    public void mouseReleased(int i, int j)
    {
    }

    public boolean mousePressed(int i, int j)
    {
        return enabled && i >= xPosition && j >= yPosition && i < xPosition + width && j < yPosition + height;
    }

    protected int width;
    protected int height;
    public int xPosition;
    public int yPosition;
    public String displayString;
    public int id;
    public boolean enabled;
    public boolean enabled2;
}
