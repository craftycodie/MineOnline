package gg.codie.mineonline.gui.components;

public class GuiToggleButton extends GuiButton {

    public GuiToggleButton(int i, int xPos, int yPos, int width, int height, String s, GuiButtonListener buttonListener)
    {
        super(i, xPos, yPos, width, height, s, buttonListener);
    }

    public boolean mousePressed(int i, int j)
    {
        return i >= xPosition && j >= yPosition && i < xPosition + width && j < yPosition + height;
    }

}
