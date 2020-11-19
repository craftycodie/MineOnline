package gg.codie.mineonline.gui.screens;

import gg.codie.minecraft.client.gui.Tessellator;
import gg.codie.mineonline.gui.input.MouseHandler;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.textures.EGUITexture;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public abstract class GuiSlot
{

    public GuiSlot(int width, int height, int top, int bottom, int i1)
    {
        initialClickY = -2F;
        selectedElement = -1;
        lastClicked = 0L;
        field_25123_p = true;
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        posZ = i1;
        right = width;

        for (int i = 0; i < getSize();  i++) {
            if (isSelected(i)) {
                selectedElement = i;
                amountScrolled = 36 * (i - 1);
                break;
            }
        }
    }

    public void resize(int width, int height, int top, int bottom) {
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.right = width;
    }

    public void update() {
        amountScrolled -= (float)Mouse.getDWheel() / 3.3225;
    }

    protected abstract int getSize();

    protected abstract void elementClicked(int i, boolean flag);

    protected abstract boolean isSelected(int i);

    protected int getContentHeight()
    {
        return getSize() * posZ;
    }

    protected abstract void drawBackground();

    protected abstract void drawSlot(int i, int j, int k, int l, Tessellator tessellator);

    private void bindAmountScrolled()
    {
        int i = getContentHeight() - (bottom - top - 4);
        if(i < 0)
        {
            i /= 2;
        }
        if(amountScrolled < 0.0F)
        {
            amountScrolled = 0.0F;
        }
        if(amountScrolled > (float)i)
        {
            amountScrolled = i;
        }
    }

    public void drawScreen(int i, int j)
    {
        field_35409_k = i;
        field_35408_l = j;
        drawBackground();
        int k = getSize();
        int l = width / 2 + 124;
        int i1 = l + 6;
        if(MouseHandler.isMouseLeftDown())
        {
            if(initialClickY == -1F)
            {
                boolean flag = true;
                if(j >= top && j <= bottom)
                {
                    int j1 = width / 2 - 110;
                    int k1 = width / 2 + 110;
                    int i2 = ((j - top) + (int)amountScrolled) - 4;
                    int k2 = i2 / posZ;
                    if(i >= j1 && i <= k1 && k2 >= 0 && i2 >= 0 && k2 < k)
                    {
                        boolean flag1 = k2 == selectedElement && System.currentTimeMillis() - lastClicked < 250L;
                        elementClicked(k2, flag1);
                        selectedElement = k2;
                        lastClicked = System.currentTimeMillis();
                    } else
                    if(i >= j1 && i <= k1 && i2 < 0)
                    {
                        flag = false;
                    }
                    if(i >= l && i <= i1)
                    {
                        scrollMultiplier = -1F;
                        int i3 = getContentHeight() - (bottom - top - 4);
                        if(i3 < 1)
                        {
                            i3 = 1;
                        }
                        int l3 = (int)((float)((bottom - top) * (bottom - top)) / (float)getContentHeight());
                        if(l3 < 32)
                        {
                            l3 = 32;
                        }
                        if(l3 > bottom - top - 8)
                        {
                            l3 = bottom - top - 8;
                        }
                        scrollMultiplier /= (float)(bottom - top - l3) / (float)i3;
                    } else
                    {
                        scrollMultiplier = 1.0F;
                    }
                    if(flag)
                    {
                        initialClickY = j;
                    } else
                    {
                        initialClickY = -2F;
                    }
                } else
                {
                    initialClickY = -2F;
                }
            } else
            if(initialClickY >= 0.0F)
            {
                amountScrolled -= ((float)j - initialClickY) * scrollMultiplier;
                initialClickY = j;
            }
        } else
        {
            initialClickY = -1F;
        }
        bindAmountScrolled();
        Tessellator tessellator = Tessellator.instance;
        int l1 = width / 2 - 92 - 16;
        int j2 = (top + 4) - (int)amountScrolled;
        for(int l2 = 0; l2 < k; l2++)
        {
            int j3 = j2 + l2 * posZ;
            int i4 = posZ - 4;
            if(j3 > bottom || j3 + i4 < top)
            {
                continue;
            }
            if(field_25123_p && isSelected(l2))
            {
                int k4 = width / 2 - 110;
                int i5 = width / 2 + 110;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                tessellator.startDrawingQuads();
                tessellator.setColorOpaque_I(0x808080);
                tessellator.addVertexWithUV(k4, j3 + i4 + 2, 0.0D, 0.0D, 1.0D);
                tessellator.addVertexWithUV(i5, j3 + i4 + 2, 0.0D, 1.0D, 1.0D);
                tessellator.addVertexWithUV(i5, j3 - 2, 0.0D, 1.0D, 0.0D);
                tessellator.addVertexWithUV(k4, j3 - 2, 0.0D, 0.0D, 0.0D);
                tessellator.setColorOpaque_I(0);
                tessellator.addVertexWithUV(k4 + 1, j3 + i4 + 1, 0.0D, 0.0D, 1.0D);
                tessellator.addVertexWithUV(i5 - 1, j3 + i4 + 1, 0.0D, 1.0D, 1.0D);
                tessellator.addVertexWithUV(i5 - 1, j3 - 1, 0.0D, 1.0D, 0.0D);
                tessellator.addVertexWithUV(k4 + 1, j3 - 1, 0.0D, 0.0D, 0.0D);
                tessellator.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
            drawSlot(l2, l1, j3, i4, tessellator);
        }

        //GL11.glDisable(GL11.GL_DEPTH_TEST);
        byte byte0 = 4;
        overlayBackground(0, top, 255, 255);
        overlayBackground(bottom, height, 255, 255);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        //GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        // Draw Shadow
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(0, 0);
        tessellator.addVertexWithUV(left, top + byte0, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(right, top + byte0, 0.0D, 1.0D, 1.0D);
        tessellator.setColorRGBA_I(0, 255);
        tessellator.addVertexWithUV(right, top, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(left, top, 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(0, 255);
        tessellator.addVertexWithUV(left, bottom, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(right, bottom, 0.0D, 1.0D, 1.0D);
        tessellator.setColorRGBA_I(0, 0);
        tessellator.addVertexWithUV(right, bottom - byte0, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(left, bottom - byte0, 0.0D, 0.0D, 0.0D);
        tessellator.draw();

        int k3 = getContentHeight() - (bottom - top - 4);
        if(k3 > 0)
        {
            int j4 = ((bottom - top) * (bottom - top)) / getContentHeight();
            if(j4 < 32)
            {
                j4 = 32;
            }
            if(j4 > bottom - top - 8)
            {
                j4 = bottom - top - 8;
            }
            int l4 = ((int)amountScrolled * (bottom - top - j4)) / k3 + top;
            if(l4 < top)
            {
                l4 = top;
            }
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(0, 255);
            tessellator.addVertexWithUV(l, bottom, 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV(i1, bottom, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV(i1, top, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV(l, top, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(0x808080, 255);
            tessellator.addVertexWithUV(l, l4 + j4, 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV(i1, l4 + j4, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV(i1, l4, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV(l, l4, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_I(0xc0c0c0, 255);
            tessellator.addVertexWithUV(l, (l4 + j4) - 1, 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV(i1 - 1, (l4 + j4) - 1, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV(i1 - 1, l4, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV(l, l4, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void overlayBackground(int i, int j, int k, int l)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.BACKGROUND));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32F;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_I(0x404040, l);
        tessellator.addVertexWithUV(0.0D, j, 0.0D, 0.0D, (float)j / f);
        tessellator.addVertexWithUV(width, j, 0.0D, (float)width / f, (float)j / f);
        tessellator.setColorRGBA_I(0x404040, k);
        tessellator.addVertexWithUV(width, i, 0.0D, (float)width / f, (float)i / f);
        tessellator.addVertexWithUV(0.0D, i, 0.0D, 0.0D, (float)i / f);
        tessellator.draw();
    }

    private int width;
    private int height;
    protected int top;
    protected int bottom;
    private int right;
    private final int left = 0;
    protected final int posZ;
    protected int field_35409_k;
    protected int field_35408_l;
    private float initialClickY;
    private float scrollMultiplier;
    protected float amountScrolled;
    private int selectedElement;
    private long lastClicked;
    private boolean field_25123_p;
}
