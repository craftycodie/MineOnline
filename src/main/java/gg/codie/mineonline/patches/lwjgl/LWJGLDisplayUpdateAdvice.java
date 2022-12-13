package gg.codie.mineonline.patches.lwjgl;

import gg.codie.mineonline.gui.GUIScale;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.Renderer;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class LWJGLDisplayUpdateAdvice {
    // It's useful to know whether the game is in Minecraft or MineOnline code sometimes.
    // There might be a better place to put this though.
    public static boolean inUpdateHook;

    public static ByteBuffer buffer = null;

    @Advice.OnMethodEnter
    static void intercept() throws Throwable {
        Field inUpdateHookField = ClassLoader.getSystemClassLoader().loadClass("gg.codie.mineonline.patches.lwjgl.LWJGLDisplayUpdateAdvice").getField("inUpdateHook");
        inUpdateHookField.set(null, true);
        if(LWJGLDisplayPatch.updateListener != null)
            LWJGLDisplayPatch.updateListener.onUpdateEvent();
        inUpdateHookField.set(null, false);

        drawM1Quad();
    }

    public static void drawM1Quad() {
        int width = Display.getParent().getWidth();
        int height = Display.getParent().getHeight();

        if(buffer == null || buffer.capacity() != (width * height * 3))
        {
            buffer = BufferUtils.createByteBuffer(width * height * 3);
        }
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        buffer.clear();
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);

        buffer.clear();

        for (int fuck = 0; fuck < (width * height * 3); fuck += 3) {
            byte b = buffer.get(fuck + 2);
            buffer.put(fuck + 2, buffer.get(fuck));
            buffer.put(fuck, b);
        }

        buffer.position(0);

        boolean isHudHidden = LWJGLGL11GLOrthoAdvice.hideHud;
        LWJGLGL11GLOrthoAdvice.hideHud = false;
        GL11.glOrtho(0.0D, width, height, 0.0D, 1000D, 3000D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000F);

        GL11.glColor4f(1, 1, 1, 1 );

        if (Loader.singleton == null) return;

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.loadRGBBuffer("mo:the m1 quad lol", buffer, width, height));
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glDisable(16384 /*GL_LIGHT0*/);
        GL11.glDisable(16385 /*GL_LIGHT1*/);
        GL11.glEnable(2903 /*GL_COLOR_MATERIAL*/);

        float scale = 1 / (float)GUIScale.lastScaleFactor();
        GL11.glScalef(scale, scale, 1);


        double[] z = new double[] {1000, 2000, 2900, 3000, 4000};

        Renderer.singleton.startDrawingQuads();

        for (double zOffset : z) {
            Renderer.singleton.addVertexWithUV(0, height, zOffset, 0, 0);
            Renderer.singleton.addVertexWithUV(width,   height, zOffset, 1, 0);
            Renderer.singleton.addVertexWithUV(width, 0, zOffset, 1, 1);
            Renderer.singleton.addVertexWithUV(0, 0, zOffset, 0, 1);
        }

        Renderer.singleton.draw();
        LWJGLGL11GLOrthoAdvice.hideHud = isHudHidden;
    }
}
