package gg.codie.mineonline.gui.textures;

public enum  EGUITexture {
    GUI("/gui/gui.png", true),
    MINEONLINE_GUI_ICONS("/mineonline/gui/icons.png", true),
    MINEONLINE_LOGO("/mineonline/gui/logo.png", true),
    LOADING("/mineonline/gui/loading.png", true),
    PACK("/mineonline/gui/pack.png", false),
    FONT("/font/default.png", true),
    UNKNOWN_PACK("/gui/unknown_pack.png", true),
    BACKGROUND("/gui/background.png", true),

    PANORAMA0("/title/bg/panorama0.png", true),
    PANORAMA1("/title/bg/panorama1.png", true),
    PANORAMA2("/title/bg/panorama2.png", true),
    PANORAMA3("/title/bg/panorama3.png", true),
    PANORAMA4("/title/bg/panorama4.png", true),
    PANORAMA5("/title/bg/panorama5.png", true);

    public final String textureName;
    public final boolean useTexturePack;
    EGUITexture(String textureName, boolean useTexturePack) {
        this.textureName = textureName;
        this.useTexturePack = useTexturePack;
    }
}
