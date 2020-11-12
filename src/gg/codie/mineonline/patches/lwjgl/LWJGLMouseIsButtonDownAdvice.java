package gg.codie.mineonline.patches.lwjgl;

import gg.codie.mineonline.gui.input.MouseHandler;
import net.bytebuddy.asm.Advice;

public class LWJGLMouseIsButtonDownAdvice {
    public static boolean lock;

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.Argument(0) int button) {
        // Prevent weird double clicking in classic.
        try {
            if (!(boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLDisplayUpdateAdvice").getField("inUpdateHook").get(null)) {
                return !(boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.gui.input.MouseHandler").getMethod("isButtonDown", int.class).invoke(null, button)
                        || !(boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.gui.input.MouseHandler").getMethod("didClickCooldown", int.class, long.class).invoke(null, button, 250L);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lock;
    }
}
