package gg.codie.mineonline.gui.components;

public class GuiSmallButton extends GuiButton
{
    public GuiSmallButton(int i, int xPos, int yPos, String s)
    {
        this(i, xPos, yPos, s, null);
    }

    public GuiSmallButton(int i, int xPos, int yPos, int l, int i1, String s)
    {
        super(i, xPos, yPos, l, i1, s, null);
    }

    public GuiSmallButton(int i, int xPos, int yPos, String s, GuiButtonListener buttonListener)
    {
        super(i, xPos, yPos, 150, 20, s, buttonListener);
    }
}
