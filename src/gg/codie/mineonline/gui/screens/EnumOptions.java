package gg.codie.mineonline.gui.screens;


public enum EnumOptions
{
    GUI_SCALE("options.guiScale", false, false),
    GAMMA( "options.gamma", true, false),
    FOV("options.fov", true, false),
    HIDE_VERSION_STRINGS("options.fov", false, true),
    FULLSCREEN("options.fullscreen", false, true);


    public static EnumOptions getEnumOptions(int i)
    {
        EnumOptions aenumoptions[] = values();
        int j = aenumoptions.length;
        for(int k = 0; k < j; k++)
        {
            EnumOptions enumoptions = aenumoptions[k];
            if(enumoptions.ordinal() == i)
            {
                return enumoptions;
            }
        }

        return null;
    }

    EnumOptions(String optionName, boolean isFloat, boolean isBool)
    {
        enumString = optionName;
        enumFloat = isFloat;
        enumBoolean = isBool;
    }

    public boolean getEnumFloat()
    {
        return enumFloat;
    }

    private final boolean enumFloat;
    private final boolean enumBoolean;
    private final String enumString;
}
