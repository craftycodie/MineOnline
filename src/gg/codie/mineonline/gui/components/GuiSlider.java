package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.gui.screens.EnumOptions;
import org.lwjgl.opengl.GL11;

public class GuiSlider extends GuiButton
{

    public GuiSlider(int i, int j, int k, EnumOptions enumoptions, String s, float f)
    {
        super(i, j, k, 150, 20, s);
        sliderValue = 1.0F;
        dragging = false;
        idFloat = null;
        idFloat = enumoptions;
        sliderValue = f;
    }

    protected int getHoverState(boolean flag)
    {
        return 0;
    }

    protected void mouseDragged(int i, int j)
    {
//        if(!enabled2)
//        {
//            return;
//        }
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
            // TODO: Save Change
//            minecraft.gameSettings.setOptionFloatValue(idFloat, sliderValue);
            displayString = "" + sliderValue;
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
            // TODO: Save Change
            //minecraft.gameSettings.setOptionFloatValue(idFloat, sliderValue);
            displayString = "" + sliderValue;
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
    private EnumOptions idFloat;
}
