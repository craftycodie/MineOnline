package gg.codie.mineonline.gui.rendering.animation;

import gg.codie.mineonline.gui.rendering.PlayerGameObject;
import org.lwjgl.util.vector.Vector3f;

public class WalkPlayerAnimation implements IPlayerAnimation {

    boolean swingForward;

    @Override
    public void reset(PlayerGameObject player) {
        player.playerHead.setLocalRotation(new Vector3f());
        player.playerHeadwear.setLocalRotation(new Vector3f());
        player.playerLeftArm.setLocalRotation(new Vector3f());
        player.playerRightArm.setLocalRotation(new Vector3f());
        player.playerRightLeg.setLocalRotation(new Vector3f());
        player.playerLeftLeg.setLocalRotation(new Vector3f());
        player.playerCloak.setLocalRotation(new Vector3f(30, 0, 0));
        player.playerBody.setLocalRotation(new Vector3f());
    }

    @Override
    public void animate(PlayerGameObject player) {
        if(swingForward) {
            player.playerRightArm.increaseRotation(new Vector3f(2, 0, 0));
            player.playerLeftArm.increaseRotation(new Vector3f(-2, 0, 0));

            player.playerLeftLeg.increaseRotation(new Vector3f(3, 0, 0));
            player.playerRightLeg.increaseRotation(new Vector3f(-3, 0, 0));

            if (player.playerLeftLeg.getLocalRotation().x >= 70) {
                swingForward = false;
            }
        } else {
            player.playerRightArm.increaseRotation(new Vector3f(-2, 0, 0));
            player.playerLeftArm.increaseRotation(new Vector3f(2, 0, 0));

            player.playerLeftLeg.increaseRotation(new Vector3f(-3, 0, 0));
            player.playerRightLeg.increaseRotation(new Vector3f(3, 0, 0));

            if (player.playerLeftLeg.getLocalRotation().x <= -70) {
                swingForward = true;
            }
        }

//        player.playerCloak.increaseRotation(0, 0, 2);

        //player.playerHead.increaseRotation(0, 2, 0);
    }

}
