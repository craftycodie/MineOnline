package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.textures.EGUITexture;
import gg.codie.mineonline.gui.sound.ClickSound;
import org.lwjgl.opengl.GL11;

public class GuiButton extends GuiComponent
{
    public interface GuiButtonListener {
        void OnButtonPress();
    }

    public void doClick() {
        ClickSound.play();
        if(buttonListener != null)
            buttonListener.OnButtonPress();
    }

    public GuiButton(int i, int xPos, int yPos, String s, GuiButtonListener buttonListener)
    {
        this(i, xPos, yPos, 200, 20, s, buttonListener);
    }

    public GuiButton(int i, int xPos, int yPos, int width, int height, String s, GuiButtonListener buttonListener)
    {
        this.width = 200;
        this.height = 20;
        enabled = true;
        enabled2 = true;
        id = i;
        xPosition = xPos;
        yPosition = yPos;
        this.width = width;
        this.height = height;
        displayString = s;
        this.buttonListener = buttonListener;
    }

    @Override
    public void resize(int x, int y) {
        xPosition = x;
        yPosition = y;
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
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.GUI));
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
    private GuiButtonListener buttonListener;
}
