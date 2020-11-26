package gg.codie.mineonline.gui.components;

public class GuiSmallButton extends GuiButton
{
    public GuiSmallButton(int id, int xPos, int yPos, int width, int height, String text, GuiButton.GuiButtonListener buttonListener)
    {
        super(id, xPos, yPos, width, height, text, buttonListener);
    }

    public GuiSmallButton(int id, int xPos, int yPos, String s, GuiButtonListener buttonListener)
    {
        super(id, xPos, yPos, 150, 20, s, buttonListener);
    }
}
