package gg.codie.mineonline.gui.rendering.models;

import gg.codie.mineonline.gui.rendering.textures.ModelTexture;

public class TexturedModel {

    public void setRawModel(RawModel rawModel) {
        this.rawModel = rawModel;
    }

    public void setDontRender(boolean dontRender) {
        this.dontRender = dontRender;
    }

    public boolean getDontRender() {
        return dontRender;
    }

    private RawModel rawModel;
    private ModelTexture texture;
    private boolean dontRender;

    public TexturedModel(RawModel model, ModelTexture texture) {
        this.rawModel = model;
        this.texture = texture;
    }

    public void setTexture(ModelTexture texture) {
        this.texture = texture;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public ModelTexture getTexture() {
        return texture;
    }

}
