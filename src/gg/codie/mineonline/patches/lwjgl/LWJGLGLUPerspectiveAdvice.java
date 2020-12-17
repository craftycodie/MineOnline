package gg.codie.mineonline.patches.lwjgl;

import net.bytebuddy.asm.Advice;

public class LWJGLGLUPerspectiveAdvice {
    public static float customFOV = 70;
    public static float originalFOV = 70;
    public static float zFar = 0;
    public static boolean zoom;
    public static boolean ignore;

    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0, readOnly = false) float fov, @Advice.Argument(value = 2, readOnly = false) float zNear, @Advice.Argument(3) float zFar) {
        try {
            if ((boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLGLUPerspectiveAdvice").getField("ignore").get(null))
                return;

            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLGLUPerspectiveAdvice").getField("originalFOV").set(null, fov);
            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLGLUPerspectiveAdvice").getField("zFar").set(null, zFar);

            float customFOV = (float)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLGLUPerspectiveAdvice").getField("customFOV").get(null);
            boolean zoom = (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLGLUPerspectiveAdvice").getField("zoom").get(null);

            if (zoom)
                customFOV = 20;

            if (customFOV == 70)
                return;

            float fovDiff = fov - 70;
            fov = customFOV + fovDiff;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
