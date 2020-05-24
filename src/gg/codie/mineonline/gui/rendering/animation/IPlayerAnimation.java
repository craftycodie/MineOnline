package gg.codie.mineonline.gui.rendering.animation;

import gg.codie.mineonline.gui.rendering.PlayerGameObject;

public interface IPlayerAnimation {

    void reset(PlayerGameObject player);

    void animate(PlayerGameObject player);

}
