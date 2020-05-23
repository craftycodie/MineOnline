package gg.codie.mineonline.gui.rendering.animation;

import gg.codie.mineonline.gui.rendering.PlayerGameObject;
import gg.codie.mineonline.gui.rendering.animation.IPlayerAnimation;
import org.lwjgl.util.vector.Vector3f;

public class EmptyPlayerAnimation implements IPlayerAnimation {

    @Override
    public void reset(PlayerGameObject player) {
        player.playerHead.setLocalRotation(new Vector3f());
        player.playerHeadwear.setLocalRotation(new Vector3f());
        player.playerLeftArm.setLocalRotation(new Vector3f());
        player.playerRightArm.setLocalRotation(new Vector3f());
        player.playerRightLeg.setLocalRotation(new Vector3f());
        player.playerLeftLeg.setLocalRotation(new Vector3f());
        player.playerCloak.setLocalRotation(new Vector3f());
        player.playerBody.setLocalRotation(new Vector3f());
    }

    @Override
    public void animate(PlayerGameObject player) {

    }
}
