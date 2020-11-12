package gg.codie.mineonline.gui.rendering.textures;

public enum  EGUITexture {
    OLD_GUI("/img/gui.png", true),
    GUI("/gui/gui.png", true),
    GUI_ICONS("/gui/icons.png", false),
    FONT("/font/default.png", true),
    UNKNOWN_PACK("/gui/unknown_pack.png", true),
    PACK("/pack.png", false),
    BACKGROUND("/gui/background.png", true);

    public final String textureName;
    public final boolean useTexturePack;
    EGUITexture(String textureName, boolean useTexturePack) {
        this.textureName = textureName;
        this.useTexturePack = useTexturePack;
    }
}
