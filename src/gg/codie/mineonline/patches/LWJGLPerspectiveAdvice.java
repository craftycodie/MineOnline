package gg.codie.mineonline.patches;

import net.bytebuddy.asm.Advice;

public class LWJGLPerspectiveAdvice {
    public static float customFOV = 70;

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0, readOnly = false) float fov) {
        try {
            float customFOV = (float)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.LWJGLPerspectiveAdvice").getField("customFOV").get(null);
            float fovDiff = fov - 70;
            fov = customFOV + fovDiff;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
