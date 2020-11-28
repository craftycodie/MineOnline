package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.gui.rendering.FontRenderer;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.sound.ClickSound;
import gg.codie.mineonline.gui.textures.EGUITexture;
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

    public GuiButton(int id, int xPos, int yPos, String text, GuiButtonListener buttonListener)
    {
        this(id, xPos, yPos, 200, 20, text, buttonListener);
    }

    public GuiButton(int id, int xPos, int yPos, int width, int height, String text, GuiButtonListener buttonListener)
    {
        this.width = 200;
        this.height = 20;
        enabled = true;
        this.id = id;
        xPosition = xPos;
        yPosition = yPos;
        this.width = width;
        this.height = height;
        displayString = text;
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
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.GUI));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        boolean hover = i >= xPosition && j >= yPosition && i < xPosition + width && j < yPosition + height;
        int k = getHoverState(hover);
        Renderer.singleton.drawSprite(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
        Renderer.singleton.drawSprite(xPosition + width / 2, yPosition, 200 - width / 2, 46 + k * 20, width / 2, height);
        mouseDragged(i, j);
        if(!enabled)
        {
            FontRenderer.minecraftFontRenderer.drawCenteredString(displayString, xPosition + width / 2, yPosition + (height - 8) / 2, 0xffa0a0a0);
        } else
        if(hover)
        {
            FontRenderer.minecraftFontRenderer.drawCenteredString(displayString, xPosition + width / 2, yPosition + (height - 8) / 2, 0xffffa0);
            if (tooltip != null) {
                Renderer.singleton.renderTooltip(tooltip, i, j);
            }
        } else
        {
            FontRenderer.minecraftFontRenderer.drawCenteredString(displayString, xPosition + width / 2, yPosition + (height - 8) / 2, 0xe0e0e0);
        }
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    protected void mouseDragged(int i, int j)
    {
    }

    public void mouseReleased(int x, int y)
    {
    }

    public boolean mousePressed(int x, int y)
    {
        return enabled && x >= xPosition && y >= yPosition && x < xPosition + width && y < yPosition + height;
    }

    protected int width;
    protected int height;
    public int xPosition;
    public int yPosition;
    public String displayString;
    public int id;
    public boolean enabled;
    private GuiButtonListener buttonListener;
    private String tooltip;
}
