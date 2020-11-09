package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.gui.screens.EnumOptions;

public class GuiSmallButton extends GuiButton
{

    public GuiSmallButton(int i, int xPos, int yPos, String s)
    {
        this(i, xPos, yPos, null, s);
    }

    public GuiSmallButton(int i, int xPos, int yPos, int l, int i1, String s)
    {
        super(i, xPos, yPos, l, i1, s);
        enumOptions = null;
    }

    public GuiSmallButton(int i, int xPos, int yPos, EnumOptions enumoptions, String s)
    {
        super(i, xPos, yPos, 150, 20, s);
        enumOptions = enumoptions;
    }

    private final EnumOptions enumOptions;
}
