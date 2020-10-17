package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLPerspectiveAdvice {
    public static float customFOV = 70;
    public static float originalFOV = 70;
    public static float zFar = 0;
    public static boolean ignore = false;

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0, readOnly = false) float fov, @Advice.Argument(3) float zFar) {
        try {
            if ((boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice").getField("ignore").get(null))
                return;

            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice").getField("originalFOV").set(null, fov);

            float customFOV = (float)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice").getField("customFOV").get(null);
            float fovDiff = fov - 70;
            fov = customFOV + fovDiff;

            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice").getField("zFar").set(null, fov);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
