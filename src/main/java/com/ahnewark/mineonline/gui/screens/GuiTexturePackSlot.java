package com.ahnewark.mineonline.gui.screens;

import com.ahnewark.mineonline.Settings;
import com.ahnewark.mineonline.client.LegacyGameManager;
import com.ahnewark.mineonline.client.MinecraftTexturePackRepository;
import com.ahnewark.mineonline.gui.rendering.Font;
import com.ahnewark.mineonline.gui.rendering.Loader;
import com.ahnewark.mineonline.gui.rendering.Renderer;
import com.ahnewark.mineonline.gui.textures.EGUITexture;
import com.ahnewark.mineonline.gui.textures.TexturePackBase;
import org.lwjgl.opengl.GL11;

import java.util.List;

class GuiTexturePackSlot extends GuiSlot
{

    public GuiTexturePackSlot(GuiTexturePacks guitexturepacks)
    {
        super(guitexturepacks.getWidth(), guitexturepacks.getHeight(), 32, (guitexturepacks.getHeight() - 55) + 4, 36, 220);
        parentTexturePackGui = guitexturepacks;
    }

    protected int getSize()
    {
        List list = MinecraftTexturePackRepository.singleton.getTexturePacks();
        return list.size();
    }

    protected void elementClicked(int slotIndex, boolean doubleClicked)
    {
        List list = MinecraftTexturePackRepository.singleton.getTexturePacks();

        if (LegacyGameManager.isInGame() && !Settings.singleton.getTexturePack().equals(((TexturePackBase)list.get(slotIndex)).texturePackFileName)) {
            LegacyGameManager.setTexturePack(((TexturePackBase)list.get(slotIndex)).texturePackFileName);
        } else {
            for(EGUITexture texture : EGUITexture.values()) {
                if(texture.useTexturePack) {
                    Loader.singleton.unloadTexture(texture);
                }
            }
        }

        Settings.singleton.setTexturePack(((TexturePackBase) list.get(slotIndex)).texturePackFileName);
        Settings.singleton.saveSettings();

        Font.reloadFont();
    }

    protected boolean isSelected(int slotIndex)
    {
        List list = MinecraftTexturePackRepository.singleton.getTexturePacks();
        return Settings.singleton.getTexturePack().equals(((TexturePackBase)list.get(slotIndex)).texturePackFileName);
    }

    protected int getContentHeight()
    {
        return getSize() * 36;
    }

    protected void drawBackground()
    {
        parentTexturePackGui.drawDefaultBackground();
    }

    protected void drawSlot(int slotIndex, int xPos, int yPos, int zPos)
    {
        resize(parentTexturePackGui.getWidth(), parentTexturePackGui.getHeight(), 32, (parentTexturePackGui.getHeight() - 55) + 4);

        TexturePackBase texturepackbase = MinecraftTexturePackRepository.singleton.getTexturePacks().get(slotIndex);
        texturepackbase.bindThumbnailTexture();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Renderer.singleton.startDrawingQuads();
        Renderer.singleton.setColorRGBA(255, 255, 255, 255);
        Renderer.singleton.addVertexWithUV(xPos, yPos + zPos, 0.0D, 0.0D, 1.0D);
        Renderer.singleton.addVertexWithUV(xPos + 32, yPos + zPos, 0.0D, 1.0D, 1.0D);
        Renderer.singleton.addVertexWithUV(xPos + 32, yPos, 0.0D, 1.0D, 0.0D);
        Renderer.singleton.addVertexWithUV(xPos, yPos, 0.0D, 0.0D, 0.0D);
        Renderer.singleton.draw();
        Font.minecraftFont.drawString(texturepackbase.texturePackFileName, xPos + 32 + 2, yPos + 1, 0xffffff);
        Font.minecraftFont.drawString(texturepackbase.firstDescriptionLine, xPos + 32 + 2, yPos + 12, 0x808080);
        Font.minecraftFont.drawString(texturepackbase.secondDescriptionLine, xPos + 32 + 2, yPos + 12 + 10, 0x808080);
    }

    final GuiTexturePacks parentTexturePackGui;
}
