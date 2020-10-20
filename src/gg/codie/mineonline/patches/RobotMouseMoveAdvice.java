package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

public class RobotMouseMoveAdvice {

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.Argument(0) int x, @Advice.Argument(1) int y) {
        //System.out.println("Moving mouse " + x + ", " + y);
        //Mouse.setGrabbed(cursor != null);
        return true;
    }
}
