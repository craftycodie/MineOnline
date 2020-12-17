package gg.codie.mineonline.gui.textures;

public abstract class TexturePackBase
{
    public abstract void cleanup();

    public abstract void bindThumbnailTexture();

    public String texturePackFileName;
    public String firstDescriptionLine;
    public String secondDescriptionLine;
    public String key;
}
