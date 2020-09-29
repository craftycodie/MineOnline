package gg.codie.mineonline.gui.rendering.animation;

import gg.codie.mineonline.gui.rendering.PlayerGameObject;
import org.lwjgl.util.vector.Vector3f;

public class ClassicWalkPlayerAnimation implements IPlayerAnimation {

    boolean swingForward;
    boolean armsUp;

    int swingCount = 0;

    @Override
    public void reset(PlayerGameObject player) {
        player.playerHead.setLocalRotation(new Vector3f());
        player.playerHeadwear.setLocalRotation(new Vector3f());
        player.playerLeftArm.setLocalRotation(new Vector3f());
        player.playerRightArm.setLocalRotation(new Vector3f(0, 0, 90));
        player.playerRightLeg.setLocalRotation(new Vector3f());
        player.playerLeftLeg.setLocalRotation(new Vector3f());
        player.playerCloak.setLocalRotation(new Vector3f(30, 0, 0));
        player.playerBody.setLocalRotation(new Vector3f());
    }

    @Override
    public void animate(PlayerGameObject player) {
        if(armsUp) {
            if (player.playerLeftArm.getLocalRotation().z <=-90 && swingCount % 2 == 0) {
                armsUp = false;
            } else {
                player.playerRightArm.increaseRotation(new Vector3f(0, 0, -2));
                player.playerLeftArm.increaseRotation(new Vector3f(0, 0, -2));


                //System.out.println(player.playerRightArm.getLocalZRot());
                if (player.playerLeftArm.getLocalRotation().z <= -90) {
                    armsUp = false;
                }
            }

        } else {
            player.playerRightArm.increaseRotation(new Vector3f(0, 0, 2));
            player.playerLeftArm.increaseRotation(new Vector3f(0, 0, 2));

//            System.out.println(player.playerRightArm.getLocalRotation().z);
            if (player.playerLeftArm.getLocalRotation().z >=0) {
                armsUp = true;
            }

        }

        if(swingForward) {
            player.playerRightArm.increaseRotation(new Vector3f(3, 0, 0));
            player.playerLeftArm.increaseRotation(new Vector3f(-3, 0, 0));

            player.playerLeftLeg.increaseRotation(new Vector3f(3, 0, 0));
            player.playerRightLeg.increaseRotation(new Vector3f(-3, 0, 0));

            if (player.playerLeftLeg.getLocalRotation().x >= 80) {
                swingForward = false;
                swingCount++;
            }
        } else {
            player.playerRightArm.increaseRotation(new Vector3f(-3, 0, 0));
            player.playerLeftArm.increaseRotation(new Vector3f(3, 0, 0));

            player.playerLeftLeg.increaseRotation(new Vector3f(-3, 0, 0));
            player.playerRightLeg.increaseRotation(new Vector3f(3, 0, 0));

            if (player.playerLeftLeg.getLocalRotation().x <= -80) {
                swingForward = true;
                swingCount++;
            }
        }

//        player.playerCloak.increaseRotation(0, 0, 2);

        //player.playerHead.increaseRotation(0, 2, 0);
    }

}
