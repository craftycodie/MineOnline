package gg.codie.mineonline.patches.minecraft;

import net.bytebuddy.asm.Advice;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class FOVViewmodelAdvice {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(value = 0, readOnly = false) float fov) {
        try {
            float originalFOV = (float)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice").getField("originalFOV").get(null);
            float zFar = (float)ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice").getField("zFar").get(null);

            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
//            float temp = this.j.z.fov;
//            this.j.z.fov = 0.0f;
            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice").getField("ignore").set(null, true);
            GLU.gluPerspective(originalFOV, (float) Display.getWidth() / Display.getHeight(), 0.05f, zFar);
            ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice").getField("ignore").set(null, false);

//            this.j.z.fov = temp;
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
