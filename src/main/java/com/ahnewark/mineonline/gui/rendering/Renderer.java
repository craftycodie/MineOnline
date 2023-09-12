package com.ahnewark.mineonline.gui.rendering;

import com.ahnewark.mineonline.utils.MathUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Renderer
{
    private Renderer(int i)
    {
        vertexCount = 0;
        hasColor = false;
        hasTexture = false;
        rawBufferIndex = 0;
        addedVertices = 0;
        bufferSize = i;
        byteBuffer = Loader.createDirectByteBuffer(i * 4);
        intBuffer = byteBuffer.asIntBuffer();
        floatBuffer = byteBuffer.asFloatBuffer();
        rawBuffer = new int[i];
    }
    public void renderTooltip(String tooltipText, int mouseX, int mouseY)
    {
        if(tooltipText != null)
        {
            int tooltipX = mouseX + 12;
            int tooltipY = mouseY - 12;
            int tooltipWidth = Font.minecraftFont.width(tooltipText);
            Renderer.singleton.drawGradient(tooltipX - 3, tooltipY - 3, tooltipX + tooltipWidth + 3, tooltipY + 8 + 3, 0xc0, 0, 0, 0, 0xc0, 0, 0, 0);
            Font.minecraftFont.drawStringWithShadow(tooltipText, tooltipX, tooltipY, -1);
        }
    }

    public void drawSprite(int x, int y, int atlasX, int atlasY, int width, int height)
    {
        float atlasUnit = 0.00390625F;

        startDrawingQuads();
        addVertexWithUV(x, y + height, 0, atlasX * atlasUnit, (float)(atlasY + height) * atlasUnit);
        addVertexWithUV(x + width, y + height, 0, (float)(atlasX + width) * atlasUnit, (float)(atlasY + height) * atlasUnit);
        addVertexWithUV(x + width, y, 0, (float)(atlasX + width) * atlasUnit, atlasY * atlasUnit);
        addVertexWithUV(x, y, 0, atlasX * atlasUnit, atlasY * atlasUnit);

        draw();
    }

    public void drawGradient(int x, int y, int width, int height, int a1, int r1, int g1, int b1, int a2, int r2, int g2, int b2)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Renderer tessellator = Renderer.singleton;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(r1, g1, b1, a1);
        tessellator.addVertex(width, y, 0);
        tessellator.addVertex(x, y, 0);
        tessellator.setColorRGBA(r2, g2, b2, a2);
        tessellator.addVertex(x, height, 0);
        tessellator.addVertex(width, height, 0);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public void drawRect(int x, int y, int width, int height, int color)
    {
        if(x < width)
        {
            int j1 = x;
            x = width;
            width = j1;
        }
        if(y < height)
        {
            int k1 = y;
            y = height;
            height = k1;
        }
        float f = (float)(color >> 24 & 0xff) / 255F;
        float f1 = (float)(color >> 16 & 0xff) / 255F;
        float f2 = (float)(color >> 8 & 0xff) / 255F;
        float f3 = (float)(color & 0xff) / 255F;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(f1, f2, f3, f);
        startDrawingQuads();
        addVertex(x, height, 0);
        addVertex(width, height, 0);
        addVertex(width, y, 0);
        addVertex(x, y, 0);
        draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void draw()
    {
        if(vertexCount > 0)
        {
            intBuffer.clear();
            intBuffer.put(rawBuffer, 0, rawBufferIndex);
            byteBuffer.position(0);
            byteBuffer.limit(rawBufferIndex * 4);
            if(hasTexture)
            {
                floatBuffer.position(3);
                GL11.glTexCoordPointer(2, 32, floatBuffer);
                GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            }
            if(hasColor)
            {
                byteBuffer.position(20);
                GL11.glColorPointer(4, true, 32, byteBuffer);
                GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            }
            floatBuffer.position(0);
            GL11.glVertexPointer(3, 32, floatBuffer);
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            if(drawMode == 7 && convertQuadsToTriangles)
            {
                GL11.glDrawArrays(4, 0, vertexCount);
            } else
            {
                GL11.glDrawArrays(drawMode, 0, vertexCount);
            }
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            if(hasTexture)
            {
                GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
            }
            if(hasColor)
            {
                GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
            }
        }
        reset();
    }

    private void reset()
    {
        vertexCount = 0;
        byteBuffer.clear();
        rawBufferIndex = 0;
        addedVertices = 0;
    }

    public void startDrawingQuads()
    {
        startDrawing(7);
    }

    public void startDrawing(int i)
    {
        reset();

        drawMode = i;
        hasColor = false;
        hasTexture = false;
    }

    public void setNormal(float f, float f1, float f2) {
        byte byte0 = (byte) ((int) (f * 128.0F));
        byte byte1 = (byte) ((int) (f1 * 127.0F));
        byte byte2 = (byte) ((int) (f2 * 127.0F));
        GL11.glNormal3b(byte0, byte1, byte2);
    }

    public void setTextureUV(double d, double d1)
    {
        hasTexture = true;
        textureU = d;
        textureV = d1;
    }

    public void setColorRGBA(int r, int g, int b, int a)
    {
        r = MathUtils.clamp(r, 0, 255);
        g = MathUtils.clamp(g, 0, 255);
        b = MathUtils.clamp(b, 0, 255);
        a = MathUtils.clamp(a, 0, 255);

        hasColor = true;
        if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
        {
            color = a << 24 | b << 16 | g << 8 | r;
        } else
        {
            color = r << 24 | g << 16 | b << 8 | a;
        }
    }

    public void addVertexWithUV(double x, double y, double z, double uvx, double uvy)
    {
        setTextureUV(uvx, uvy);
        addVertex(x, y, z);
    }

    public void addVertex(double d, double d1, double d2)
    {
        addedVertices++;
        if(drawMode == 7 && convertQuadsToTriangles && addedVertices % 4 == 0)
        {
            for(int i = 0; i < 2; i++)
            {
                int j = 8 * (3 - i);
                if(hasTexture)
                {
                    rawBuffer[rawBufferIndex + 3] = rawBuffer[(rawBufferIndex - j) + 3];
                    rawBuffer[rawBufferIndex + 4] = rawBuffer[(rawBufferIndex - j) + 4];
                }
                if(hasColor)
                {
                    rawBuffer[rawBufferIndex + 5] = rawBuffer[(rawBufferIndex - j) + 5];
                }
                rawBuffer[rawBufferIndex + 0] = rawBuffer[(rawBufferIndex - j) + 0];
                rawBuffer[rawBufferIndex + 1] = rawBuffer[(rawBufferIndex - j) + 1];
                rawBuffer[rawBufferIndex + 2] = rawBuffer[(rawBufferIndex - j) + 2];
                vertexCount++;
                rawBufferIndex += 8;
            }

        }
        if(hasTexture)
        {
            rawBuffer[rawBufferIndex + 3] = Float.floatToRawIntBits((float)textureU);
            rawBuffer[rawBufferIndex + 4] = Float.floatToRawIntBits((float)textureV);
        }
        if(hasColor)
        {
            rawBuffer[rawBufferIndex + 5] = color;
        }
        rawBuffer[rawBufferIndex + 0] = Float.floatToRawIntBits((float)(d));
        rawBuffer[rawBufferIndex + 1] = Float.floatToRawIntBits((float)(d1));
        rawBuffer[rawBufferIndex + 2] = Float.floatToRawIntBits((float)(d2));
        rawBufferIndex += 8;
        vertexCount++;
        if(vertexCount % 4 == 0 && rawBufferIndex >= bufferSize - 32)
        {
            draw();
        }
    }

    private static boolean convertQuadsToTriangles = true;
    private ByteBuffer byteBuffer;
    private IntBuffer intBuffer;
    private FloatBuffer floatBuffer;
    private int rawBuffer[];
    private int vertexCount;
    private double textureU;
    private double textureV;
    private int color;
    private boolean hasColor;
    private boolean hasTexture;
    private int rawBufferIndex;
    private int addedVertices;
    private int drawMode;
    public static final Renderer singleton = new Renderer(0x200000);
    private int bufferSize;

}
