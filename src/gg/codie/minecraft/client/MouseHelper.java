package gg.codie.minecraft.client;

import java.awt.Component;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import gg.codie.mineonline.gui.rendering.DisplayManager;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class MouseHelper
{
    private static MouseHelper singleton = null;

    public static MouseHelper getSingleton() {
        if (MouseHelper.singleton == null) {
            singleton = new MouseHelper(DisplayManager.getCanvas());
        }

        return singleton;
    }

    private static synchronized ByteBuffer createDirectByteBuffer(int i)
    {
        ByteBuffer bytebuffer = ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
        return bytebuffer;
    }

    private static IntBuffer createDirectIntBuffer(int i)
    {
        return createDirectByteBuffer(i << 2).asIntBuffer();
    }

    public MouseHelper(Component component)
    {
        field_1115_e = 10;
        field_1117_c = component;
        IntBuffer intbuffer = createDirectIntBuffer(1);
        intbuffer.put(0);
        intbuffer.flip();
        IntBuffer intbuffer1 = createDirectIntBuffer(1024);
        try
        {
            cursor = new Cursor(32, 32, 16, 16, 1, intbuffer1, intbuffer);
        }
        catch(LWJGLException lwjglexception)
        {
            lwjglexception.printStackTrace();
        }
    }

    public void grabMouseCursor()
    {
        Mouse.setGrabbed(true);
        deltaX = 0;
        deltaY = 0;
    }

    public void ungrabMouseCursor()
    {
        Mouse.setCursorPosition(field_1117_c.getWidth() / 2, field_1117_c.getHeight() / 2);
        Mouse.setGrabbed(false);
    }

    public void mouseXYChange()
    {
        deltaX = Mouse.getDX();
        deltaY = Mouse.getDY();
    }

    private Component field_1117_c;
    private Cursor cursor;
    public int deltaX;
    public int deltaY;
    private int field_1115_e;
}
