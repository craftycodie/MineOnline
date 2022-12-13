package gg.codie.mineonline.gui;

import gg.codie.mineonline.Settings;

public class GUIScale
{
    private static GUIScale singleton;

    private static final int MIN_WIDTH = 320;
    private static final int MIN_HEIGHT = 240;

    public GUIScale(int displayWidth, int displayHeight)
    {
        scaledWidth = displayWidth;
        scaledHeight = displayHeight;
        scaleFactor = 1;
        int guiScale = Settings.singleton.getGUIScale().getIntValue();
        if(guiScale == 0) // if the scale is set to auto, set the limit really high.
        {
            guiScale = 1000;
        }
        for(; scaleFactor < guiScale && scaledWidth / (scaleFactor + 1) >= MIN_WIDTH && scaledHeight / (scaleFactor + 1) >= MIN_HEIGHT; scaleFactor++) { }
        scaledWidth = scaledWidth / (double)scaleFactor;
        scaledHeight = scaledHeight / (double)scaleFactor;
        singleton = this;
    }

    public double getScaledWidth()
    {
        return scaledWidth;
    }

    public double getScaledHeight()
    {
        return scaledHeight;
    }

    public double scaledWidth;
    public double scaledHeight;
    public int scaleFactor;

    public static int lastScaledWidth() {
        return (int)Math.ceil(singleton.getScaledWidth());
    }

    public static int lastScaleFactor() {
        return singleton.scaleFactor;
    }

    public static int lastScaledHeight() {
        return (int)Math.ceil(singleton.getScaledHeight());
    }
}
