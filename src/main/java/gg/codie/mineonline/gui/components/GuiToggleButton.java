package gg.codie.mineonline.gui.components;

public class GuiToggleButton extends GuiButton {

    public GuiToggleButton(int id, int xPos, int yPos, int width, int height, String text, GuiButtonListener buttonListener)
    {
        super(id, xPos, yPos, width, height, text, buttonListener);
    }

    public boolean mousePressed(int x, int y)
    {
        return x >= xPosition && y >= yPosition && x < xPosition + width && y < yPosition + height;
    }

}
