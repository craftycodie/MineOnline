package gg.codie.mineonline.gui.components;

import gg.codie.minecraft.client.gui.Tessellator;
import gg.codie.mineonline.gui.rendering.FontRenderer;
import org.lwjgl.opengl.GL11;

public class GuiComponent
{

    public GuiComponent()
    {
        zLevel = 0.0F;
    }

    protected void func_27100_a(int i, int j, int k, int l)
    {
        if(j < i)
        {
            int i1 = i;
            i = j;
            j = i1;
        }
        drawRect(i, k, j + 1, k + 1, l);
    }

    protected void func_27099_b(int i, int j, int k, int l)
    {
        if(k < j)
        {
            int i1 = j;
            j = k;
            k = i1;
        }
        drawRect(i, j + 1, i + 1, k, l);
    }

    protected void drawRect(int i, int j, int k, int l, int i1)
    {
        if(i < k)
        {
            int j1 = i;
            i = k;
            k = j1;
        }
        if(j < l)
        {
            int k1 = j;
            j = l;
            l = k1;
        }
        float f = (float)(i1 >> 24 & 0xff) / 255F;
        float f1 = (float)(i1 >> 16 & 0xff) / 255F;
        float f2 = (float)(i1 >> 8 & 0xff) / 255F;
        float f3 = (float)(i1 & 0xff) / 255F;
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(f1, f2, f3, f);
        tessellator.startDrawingQuads();
        tessellator.addVertex(i, l, 0.0D);
        tessellator.addVertex(k, l, 0.0D);
        tessellator.addVertex(k, j, 0.0D);
        tessellator.addVertex(i, j, 0.0D);
        tessellator.draw();
        GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
        GL11.glDisable(3042 /*GL_BLEND*/);
    }

    protected void drawGradientRect(int x, int y, int width, int height, int color1, int color2)
    {
        float f = (float)(color1 >> 24 & 0xff) / 255F;
        float f1 = (float)(color1 >> 16 & 0xff) / 255F;
        float f2 = (float)(color1 >> 8 & 0xff) / 255F;
        float f3 = (float)(color1 & 0xff) / 255F;
        float f4 = (float)(color2 >> 24 & 0xff) / 255F;
        float f5 = (float)(color2 >> 16 & 0xff) / 255F;
        float f6 = (float)(color2 >> 8 & 0xff) / 255F;
        float f7 = (float)(color2 & 0xff) / 255F;
        GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425 /*GL_SMOOTH*/);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex(width, y, 0.0D);
        tessellator.addVertex(x, y, 0.0D);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex(x, height, 0.0D);
        tessellator.addVertex(width, height, 0.0D);
        tessellator.draw();
        GL11.glShadeModel(7424 /*GL_FLAT*/);
        GL11.glDisable(3042 /*GL_BLEND*/);
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
        GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
    }

    public void drawCenteredString(String s, int i, int j, int k)
    {
        FontRenderer.minecraftFontRenderer.drawStringWithShadow(s, i - FontRenderer.minecraftFontRenderer.getStringWidth(s) / 2, j, k);
    }

    public void drawString(String s, int i, int j, int k)
    {
        FontRenderer.minecraftFontRenderer.drawStringWithShadow(s, i, j, k);
    }

    public void drawTexturedModalRect(int i, int j, int k, int l, int i1, int j1)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(i + 0, j + j1, zLevel, (float)(k + 0) * f, (float)(l + j1) * f1);
        tessellator.addVertexWithUV(i + i1, j + j1, zLevel, (float)(k + i1) * f, (float)(l + j1) * f1);
        tessellator.addVertexWithUV(i + i1, j + 0, zLevel, (float)(k + i1) * f, (float)(l + 0) * f1);
        tessellator.addVertexWithUV(i + 0, j + 0, zLevel, (float)(k + 0) * f, (float)(l + 0) * f1);
        tessellator.draw();
    }

    protected float zLevel;
}
