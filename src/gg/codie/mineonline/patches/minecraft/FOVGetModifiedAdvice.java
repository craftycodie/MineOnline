package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.asm.Advice;

public class FOVGetModifiedAdvice {
    public static float customFOV = 70;

    @Advice.OnMethodExit
    static void intercept(@Advice.Return(readOnly = false) float returnValue) {
        try {
            float customFOV = (float)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.FOVGetModifiedAdvice").getField("customFOV").get(null);
            float fovDiff = returnValue - 70;
            returnValue = customFOV + fovDiff;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
