package gg.codie.mineonline.gui.rendering.animation;

import gg.codie.mineonline.gui.rendering.PlayerGameObject;
import org.lwjgl.util.vector.Vector3f;

public class TestPlayerAnimation implements IPlayerAnimation {

    @Override
    public void reset(PlayerGameObject player) {
        player.playerHead.setLocalRotation(new Vector3f());
        player.playerHeadwear.setLocalRotation(new Vector3f());
        player.playerLeftArm.setLocalRotation(new Vector3f());
        player.playerRightArm.setLocalRotation(new Vector3f(0, 0, 90));
        player.playerRightLeg.setLocalRotation(new Vector3f());
        player.playerLeftLeg.setLocalRotation(new Vector3f());
        player.playerCloak.setLocalRotation(new Vector3f());
        player.playerBody.setLocalRotation(new Vector3f());
    }

    @Override
    public void animate(PlayerGameObject player) {
//        player.playerRightArm.increaseRotation(1, 0, 0);
//        player.playerLeftArm.increaseRotation(-1, 0, 0);
//
//        player.playerLeftLeg.increaseRotation(1, 0, 0);
//        player.playerRightLeg.increaseRotation(-1, 0, 0);
//
//        player.playerCloak.increaseRotation(0, 0, 2);
//
//        player.playerHead.increaseRotation(0, 2, 0);
    }
}
