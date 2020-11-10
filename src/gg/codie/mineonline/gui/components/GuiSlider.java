package gg.codie.mineonline.gui.components;

import org.lwjgl.opengl.GL11;

public class GuiSlider extends GuiButton
{
    public interface SliderListener {
        String onValueChange(float sliderValue);
    }

    public GuiSlider(int i, int j, int k, String s, float f, SliderListener sliderListener)
    {
        super(i, j, k, 150, 20, s, null);
        sliderValue = 1.0F;
        dragging = false;
        this.sliderListener = sliderListener;
        sliderValue = f;
    }

    protected int getHoverState(boolean flag)
    {
        return 0;
    }

    protected void mouseDragged(int i, int j)
    {
        if(!enabled2)
        {
            return;
        }
        if(dragging)
        {
            sliderValue = (float)(i - (xPosition + 4)) / (float)(width - 8);
            if(sliderValue < 0.0F)
            {
                sliderValue = 0.0F;
            }
            if(sliderValue > 1.0F)
            {
                sliderValue = 1.0F;
            }
            displayString = sliderListener.onValueChange(sliderValue);
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(xPosition + (int)(sliderValue * (float)(width - 8)), yPosition, 0, 66, 4, 20);
        drawTexturedModalRect(xPosition + (int)(sliderValue * (float)(width - 8)) + 4, yPosition, 196, 66, 4, 20);
    }

    public boolean mousePressed(int i, int j)
    {
        if(super.mousePressed(i, j))
        {
            sliderValue = (float)(i - (xPosition + 4)) / (float)(width - 8);
            if(sliderValue < 0.0F)
            {
                sliderValue = 0.0F;
            }
            if(sliderValue > 1.0F)
            {
                sliderValue = 1.0F;
            }
            displayString = "" + sliderListener.onValueChange(sliderValue);
            dragging = true;
            return true;
        } else
        {
            return false;
        }
    }

    public void mouseReleased(int i, int j)
    {
        dragging = false;
    }

    public float sliderValue;
    public boolean dragging;
    private SliderListener sliderListener;
}
