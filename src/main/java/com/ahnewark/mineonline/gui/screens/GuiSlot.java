package com.ahnewark.mineonline.gui.screens;

import com.ahnewark.mineonline.gui.input.MouseHandler;
import com.ahnewark.mineonline.gui.rendering.Loader;
import com.ahnewark.mineonline.gui.rendering.Renderer;
import com.ahnewark.mineonline.gui.textures.EGUITexture;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiSlot
{
    protected final int slotWidth;


    public GuiSlot(int width, int height, int top, int bottom, int i1, int slotWidth)
    {
        initialClickY = -2F;
        selectedElement = -1;
        lastClicked = 0L;
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        posZ = i1;
        right = width;
        this.slotWidth = slotWidth;

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

    public void keyTyped(char c, int i)
    {
        if (i == Keyboard.KEY_UP && selectedElement > 0)
        {
            selectedElement -= 1;
            elementClicked(selectedElement, false);
            amountScrolled = 36 * (selectedElement - 1);

        }
        else if (i == Keyboard.KEY_DOWN && selectedElement < getSize() - 1) {
            selectedElement += 1;
            elementClicked(selectedElement, false);
            amountScrolled = 36 * (selectedElement - 1);
        }
    }

    protected abstract int getSize();

    protected abstract void elementClicked(int slotIndex, boolean doubleClicked);

    protected abstract boolean isSelected(int slotIndex);

    protected int getContentHeight()
    {
        return getSize() * posZ;
    }

    protected abstract void drawBackground();

    protected abstract void drawSlot(int slotIndex, int xPos, int yPos, int zPos);

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

    public void drawScreen(int mouseX, int mouseY)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        drawBackground();
        int slotCount = getSize();
        int scrollBarPos = width / 2 + (slotWidth / 2) + 2;
        int scrollBarEnd = scrollBarPos + 6;
        if(MouseHandler.isMouseLeftDown())
        {
            if(initialClickY == -1F)
            {
                boolean flag = true;
                if(mouseY >= top && mouseY <= bottom)
                {
                    int j1 = width / 2 - (slotWidth / 2);
                    int k1 = width / 2 + (slotWidth / 2);
                    int i2 = ((mouseY - top) + (int)amountScrolled) - 4;
                    int k2 = i2 / posZ;
                    if(mouseX >= j1 && mouseX <= k1 && k2 >= 0 && i2 >= 0 && k2 < slotCount)
                    {
                        boolean doubleClicked = k2 == selectedElement && System.currentTimeMillis() - lastClicked < 250L;
                        elementClicked(k2, doubleClicked);
                        selectedElement = k2;
                        lastClicked = System.currentTimeMillis();
                    } else
                    if(mouseX >= j1 && mouseX <= k1 && i2 < 0)
                    {
                        flag = false;
                    }
                    if(mouseX >= scrollBarPos && mouseX <= scrollBarEnd)
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
                        initialClickY = mouseY;
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
                amountScrolled -= ((float)mouseY - initialClickY) * scrollMultiplier;
                initialClickY = mouseY;
            }
        } else
        {
            initialClickY = -1F;
        }
        bindAmountScrolled();
        Renderer tessellator = Renderer.singleton;
        int slotX = width / 2 - (slotWidth / 2) + 2;
        int slotY = (top + 4) - (int)amountScrolled;
        for(int i = 0; i < slotCount; i++)
        {
            int slotScreenY = slotY + i * posZ;
            int zPos = posZ - 4;
            if(slotScreenY > bottom || slotScreenY + zPos < top)
            {
                continue;
            }
            if(isSelected(i))
            {
                int k4 = width / 2 - (slotWidth / 2);
                int i5 = width / 2 + (slotWidth / 2);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA(0x80, 0x80, 0x80, 255);
                tessellator.addVertexWithUV(k4, slotScreenY + zPos + 2, 0.0D, 0.0D, 1.0D);
                tessellator.addVertexWithUV(i5, slotScreenY + zPos + 2, 0.0D, 1.0D, 1.0D);
                tessellator.addVertexWithUV(i5, slotScreenY - 2, 0.0D, 1.0D, 0.0D);
                tessellator.addVertexWithUV(k4, slotScreenY - 2, 0.0D, 0.0D, 0.0D);
                tessellator.setColorRGBA(0, 0, 0, 255);
                tessellator.addVertexWithUV(k4 + 1, slotScreenY + zPos + 1, 0.0D, 0.0D, 1.0D);
                tessellator.addVertexWithUV(i5 - 1, slotScreenY + zPos + 1, 0.0D, 1.0D, 1.0D);
                tessellator.addVertexWithUV(i5 - 1, slotScreenY - 1, 0.0D, 1.0D, 0.0D);
                tessellator.addVertexWithUV(k4 + 1, slotScreenY - 1, 0.0D, 0.0D, 0.0D);
                tessellator.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
            drawSlot(i, slotX, slotScreenY, zPos);
        }

        //GL11.glDisable(GL11.GL_DEPTH_TEST);
        overlayBackground(0, top);
        overlayBackground(bottom, height);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 771);
        //GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        // Draw Shadow
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(0, 0, 0, 0);
        tessellator.addVertexWithUV(0, top + 4, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(right, top + 4, 0.0D, 1.0D, 1.0D);
        tessellator.setColorRGBA(0, 0, 0, 255);
        tessellator.addVertexWithUV(right, top, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0, top, 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(0, 0, 0, 255);
        tessellator.addVertexWithUV(0, bottom, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(right, bottom, 0.0D, 1.0D, 1.0D);
        tessellator.setColorRGBA(0, 0, 0, 0);
        tessellator.addVertexWithUV(right, bottom - 4, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0, bottom - 4, 0.0D, 0.0D, 0.0D);
        tessellator.draw();

        int scrollStart = getContentHeight() - (bottom - top - 4);
        if(scrollStart > 0)
        {
            int scrollEnd = ((bottom - top) * (bottom - top)) / getContentHeight();
            if(scrollEnd < 32)
            {
                scrollEnd = 32;
            }
            if(scrollEnd > bottom - top - 8)
            {
                scrollEnd = bottom - top - 8;
            }
            int l4 = ((int)amountScrolled * (bottom - top - scrollEnd)) / scrollStart + top;
            if(l4 < top)
            {
                l4 = top;
            }
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA(0, 0, 0, 255);
            tessellator.addVertexWithUV(scrollBarPos, bottom, 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV(scrollBarEnd, bottom, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV(scrollBarEnd, top, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV(scrollBarPos, top, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA(0x80, 0x80, 0x80, 255);
            tessellator.addVertexWithUV(scrollBarPos, l4 + scrollEnd, 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV(scrollBarEnd, l4 + scrollEnd, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV(scrollBarEnd, l4, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV(scrollBarPos, l4, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA(0xc0, 0xc0, 0xc0, 255);
            tessellator.addVertexWithUV(scrollBarPos, (l4 + scrollEnd) - 1, 0.0D, 0.0D, 1.0D);
            tessellator.addVertexWithUV(scrollBarEnd - 1, (l4 + scrollEnd) - 1, 0.0D, 1.0D, 1.0D);
            tessellator.addVertexWithUV(scrollBarEnd - 1, l4, 0.0D, 1.0D, 0.0D);
            tessellator.addVertexWithUV(scrollBarPos, l4, 0.0D, 0.0D, 0.0D);
            tessellator.draw();
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void overlayBackground(int x, int y)
    {
        Renderer tessellator = Renderer.singleton;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.BACKGROUND));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32F;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(0x40, 0x40, 0x40, 255);
        tessellator.addVertexWithUV(0.0D, y, 0.0D, 0.0D, (float)y / f);
        tessellator.addVertexWithUV(width, y, 0.0D, (float)width / f, (float)y / f);
        tessellator.setColorRGBA(0x40, 0x40, 0x40, 255);
        tessellator.addVertexWithUV(width, x, 0.0D, (float)width / f, (float)x / f);
        tessellator.addVertexWithUV(0.0D, x, 0.0D, 0.0D, (float)x / f);
        tessellator.draw();
    }

    private int width;
    private int height;
    protected int top;
    protected int bottom;
    private int right;
    private final int posZ;
    protected int mouseX;
    protected int mouseY;
    private float initialClickY;
    private float scrollMultiplier;
    float amountScrolled;
    private int selectedElement;
    private long lastClicked;
}
