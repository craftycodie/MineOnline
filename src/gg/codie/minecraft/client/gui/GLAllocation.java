package gg.codie.minecraft.client.gui;

import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

// TODO: Refactor Out
@Deprecated
public class GLAllocation
{

    public GLAllocation()
    {
    }

    public static synchronized int generateDisplayLists(int i)
    {
        int j = GL11.glGenLists(i);
        displayLists.add(Integer.valueOf(j));
        displayLists.add(Integer.valueOf(i));
        return j;
    }

    public static synchronized ByteBuffer createDirectByteBuffer(int i)
    {
        ByteBuffer bytebuffer = ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
        return bytebuffer;
    }

    public static IntBuffer createDirectIntBuffer(int i)
    {
        return createDirectByteBuffer(i << 2).asIntBuffer();
    }

    private static List displayLists = new ArrayList();
}
