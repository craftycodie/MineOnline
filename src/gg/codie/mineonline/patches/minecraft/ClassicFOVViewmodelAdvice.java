package gg.codie.mineonline.patches.minecraft;

import gg.codie.mineonline.client.LegacyGameManager;
import net.bytebuddy.asm.Advice;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ClassicFOVViewmodelAdvice {

    // In classic, this intercept is reached twice per frame.
    // We need to do stuff on the second frame, so the first boolean is used for that.
    // In infdev, it's called multiple times from multiple places.
    // We need to do stuff when it's called from the viewmodel function, so we check the stack.

    public static int callCount = 0;

    @Advice.OnMethodExit
    static void intercept() {
        try {
            String viewModelFunction = LegacyGameManager.getVersion().viewModelFunction;

            if (viewModelFunction != null) {
                if (!Thread.currentThread().getStackTrace()[2].getMethodName().equals(viewModelFunction))
                    return;
            } else {
                int callCount = (int) ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.ClassicFOVViewmodelAdvice").getField("callCount").get(null);
                System.out.println(callCount);
                if (callCount < LegacyGameManager.getVersion().hurtEffectCallsPerFrame - 1) {
                    ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.ClassicFOVViewmodelAdvice").getField("callCount").set(null, callCount + 1);
                    return;
                } else {
                    ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.ClassicFOVViewmodelAdvice").getField("callCount").set(null, 0);
                }
            }

            float originalFOV = (float)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLGLUPerspectiveAdvice").getField("originalFOV").get(null);
            float zFar = (float)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLGLUPerspectiveAdvice").getField("zFar").get(null);
            boolean leftHanded = (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.FOVViewmodelAdvice").getField("leftHanded").get(null);
            boolean hideViewModel = (boolean)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.minecraft.FOVViewmodelAdvice").getField("hideViewModel").get(null);

            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();

            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLGLUPerspectiveAdvice").getField("ignore").set(null, true);

            if (leftHanded)
                GL11.glScalef(-1, 1, 1);
            if (hideViewModel)
                GL11.glScalef(0,0,0);

            GLU.gluPerspective(originalFOV, (float) Display.getWidth() / Display.getHeight(), 0.05f, zFar);

            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLGLUPerspectiveAdvice").getField("ignore").set(null, false);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
