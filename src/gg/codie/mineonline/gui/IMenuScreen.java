package gg.codie.mineonline.gui;

import gg.codie.mineonline.gui.rendering.Renderer;

public interface IMenuScreen {
    void update();
    void render(Renderer renderer);
    boolean showPlayer();
    void resize();
}
