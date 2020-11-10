package gg.codie.mineonline.gui.rendering.textures;

public enum  EGUITexture {
    OLD_GUI("/img/gui.png"),
    GUI("/gui/gui.png"),
    GUI_ICONS("/gui/icons.png"),
    FONT("/font/default.png"),
    UNKNOWN_PACK("/gui/unknown_pack.png"),
    DEFAULT_PACK("/gui/default_pack.png"),
    BACKGROUND("/gui/background.png");

    public final String textureName;
    EGUITexture(String textureName) {
        this.textureName = textureName;
    }
}
