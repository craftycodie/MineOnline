package gg.codie.mineonline.gui.rendering.textures;

public enum  EGUITexture {
    OLD_GUI("/img/gui.png"),
    GUI("/gui/gui.png"),
    FONT("/font/default.png"),
    UNKNOWN_PACK("/gui/unknown_pack.png"),
    PACK("/pack.png"),
    BACKGROUND("/gui/background.png");

    public final String textureName;
    EGUITexture(String textureName) {
        this.textureName = textureName;
    }
}
