package gg.codie.mineonline.gui.rendering.animation;

import gg.codie.mineonline.gui.rendering.PlayerGameObject;
import org.lwjgl.util.vector.Vector3f;

public class IdlePlayerAnimation implements IPlayerAnimation {

    boolean swingForward;

    @Override
    public void reset(PlayerGameObject player) {
        player.playerHead.setLocalRotation(new Vector3f());
        player.playerHeadwear.setLocalRotation(new Vector3f());
        player.playerLeftArm.setLocalRotation(new Vector3f(0, 0, -3));
        player.playerRightArm.setLocalRotation(new Vector3f(0, 0, 3));
        player.playerRightLeg.setLocalRotation(new Vector3f());
        player.playerLeftLeg.setLocalRotation(new Vector3f());
        player.playerCloak.setLocalRotation(new Vector3f(5, 0, 0));
        player.playerBody.setLocalRotation(new Vector3f());
    }

    @Override
    public void animate(PlayerGameObject player) {
        if(swingForward) {
            player.playerRightArm.increaseRotation(new Vector3f(0.01f, 0, 0.01f));
            player.playerLeftArm.increaseRotation(new Vector3f(-0.01f, 0, -0.01f));

            if (player.playerRightArm.getLocalRotation().x >= 3) {
                swingForward = false;
            }
        } else {
            player.playerRightArm.increaseRotation(new Vector3f(-0.01f, 0, -0.01f));
            player.playerLeftArm.increaseRotation(new Vector3f(0.01f, 0, 0.01f));

            if (player.playerRightArm.getLocalRotation().x <= -3) {
                swingForward = true;
            }
        }

    }

}
