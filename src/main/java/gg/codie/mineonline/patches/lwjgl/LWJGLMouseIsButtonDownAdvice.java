package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLMouseIsButtonDownAdvice {
    public static boolean lock;

    @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
    static boolean intercept(@Advice.Argument(0) int button) {
        // Prevent weird double clicking in classic.
        try {
            // If the game is currently in Minecraft code.
            if (!(boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLDisplayUpdateAdvice").getField("inUpdateHook").get(null)) {
                // If the MineOnline menu is open, ignore inputs.
                if ((boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.client.LegacyGameManager").getMethod("mineonlineMenuOpen").invoke(null))
                    return true;
                // Wait 1/4 s in-between breaking/placing.
                /* This is weird, so I'll explain-
                    If the mouse is down, they player is breaking, or placing a block.
                    For some reason, placing/breaking sometimes repeats the first time.
                    The cooldown when holding click is 250ms.
                    If player is NOT clicking, or the cooldown has not ended, and this is not their first click, skip.
                 */
                if ((boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.InputPatch").getField("enableClassicFixes").get(null))
                    return !(boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.gui.input.MouseHandler").getMethod("isButtonDown", int.class).invoke(null, button)
                        || (
                                !(boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.gui.input.MouseHandler").getMethod("didClickCooldown", int.class, long.class).invoke(null, button, 250L)
                                //&& !(boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.gui.input.MouseHandler").getMethod("didClick", int.class).invoke(null, button)
                        );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return lock;
    }
}
