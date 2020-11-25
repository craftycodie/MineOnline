package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.Settings;
import gg.codie.mineonline.gui.input.InputSanitization;
import gg.codie.mineonline.gui.textures.EGUITexture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;

public class FontRenderer
{
    public static void reloadFont() {
        Settings.singleton.loadSettings();
        minecraftFontRenderer = new FontRenderer();
    }

    public static FontRenderer minecraftFontRenderer = new FontRenderer();

    private FontRenderer()
    {
        Settings.singleton.loadSettings();

        charWidth = new int[256];
        fontTextureName = 0;
        buffer = BufferUtils.createIntBuffer(1024);
        BufferedImage bufferedimage;
        try
        {
            // TODO: Add texture pack support (needs an in game patch)
//            if (Settings.singleton.getTexturePack().isEmpty()) {
                bufferedimage = ImageIO.read(FontRenderer.class.getResourceAsStream(EGUITexture.FONT.textureName));
//            } else {
//                try {
//                    ZipFile texturesZip = new ZipFile(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH + Settings.singleton.getTexturePack());
//                    ZipEntry texture = texturesZip.getEntry(EGUITexture.FONT.textureName.substring(1));
//                    if (texture != null) {
//                        bufferedimage = ImageIO.read(texturesZip.getInputStream(texture));
//                    } else {
//                        bufferedimage = ImageIO.read(FontRenderer.class.getResourceAsStream(EGUITexture.FONT.textureName));
//                    }
//                } catch (Exception ex) {
//                    bufferedimage = ImageIO.read(FontRenderer.class.getResourceAsStream(EGUITexture.FONT.textureName));
//                }
//            }
        }
        catch(IOException ioexception)
        {
            throw new RuntimeException(ioexception);
        }
        int fontFileWidth = bufferedimage.getWidth();
        int fontFileHeight = bufferedimage.getHeight();
        int[] fontPixels = new int[fontFileWidth * fontFileHeight];
        bufferedimage.getRGB(0, 0, fontFileWidth, fontFileHeight, fontPixels, 0, fontFileWidth);
        for(int i = 0; i < 256; i++)
        {
            int row = i % 16;
            int column = i / 16;
            int space = 7;
            do
            {
                if(space < 0)
                {
                    break;
                }
                int i3 = row * 8 + space;
                boolean flag = true;
                for(int l3 = 0; l3 < 8 && flag; l3++)
                {
                    int i4 = (column * 8 + l3) * fontFileWidth;
                    int k4 = fontPixels[i3 + i4] & 0xff;
                    if(k4 > 0)
                    {
                        flag = false;
                    }
                }

                if(!flag)
                {
                    break;
                }
                space--;
            } while(true);
            if(i == 32)
            {
                space = 2;
            }
            charWidth[i] = space + 2;
        }

        fontTextureName = Loader.singleton.getGuiTexture(EGUITexture.FONT);
        fontDisplayLists = GL11.glGenLists(288);
        Renderer tessellator = Renderer.singleton;
        for(int i = 0; i < 256; i++)
        {
            GL11.glNewList(fontDisplayLists + i, GL11.GL_COMPILE);
            tessellator.startDrawingQuads();
            int l1 = (i % 16) * 8;
            int k2 = (i / 16) * 8;
            float f = 7.99F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            tessellator.addVertexWithUV(0.0D, 0.0F + f, 0.0D, (float)l1 / 128F + f1, ((float)k2 + f) / 128F + f2);
            tessellator.addVertexWithUV(0.0F + f, 0.0F + f, 0.0D, ((float)l1 + f) / 128F + f1, ((float)k2 + f) / 128F + f2);
            tessellator.addVertexWithUV(0.0F + f, 0.0D, 0.0D, ((float)l1 + f) / 128F + f1, (float)k2 / 128F + f2);
            tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, (float)l1 / 128F + f1, (float)k2 / 128F + f2);
            tessellator.draw();
            GL11.glTranslatef(charWidth[i], 0.0F, 0.0F);
            GL11.glEndList();
        }

        for(int i = 0; i < 32; i++)
        {
            int i2 = (i >> 3 & 1) * 85;
            int l2 = (i >> 2 & 1) * 170 + i2;
            int j3 = (i >> 1 & 1) * 170 + i2;
            int k3 = (i >> 0 & 1) * 170 + i2;
            if(i == 6)
            {
                l2 += 85;
            }
            boolean flag1 = i >= 16;
            if(Settings.singleton.get3DAnaglyph())
            {
                int j4 = (l2 * 30 + j3 * 59 + k3 * 11) / 100;
                int l4 = (l2 * 30 + j3 * 70) / 100;
                int i5 = (l2 * 30 + k3 * 70) / 100;
                l2 = j4;
                j3 = l4;
                k3 = i5;
            }
            if(flag1)
            {
                l2 /= 4;
                j3 /= 4;
                k3 /= 4;
            }
            GL11.glNewList(fontDisplayLists + 256 + i, GL11.GL_COMPILE);
            GL11.glColor3f((float)l2 / 255F, (float)j3 / 255F, (float)k3 / 255F);
            GL11.glEndList();
        }

    }

    public void drawStringWithShadow(String s, int xPos, int yPos, int color)
    {
        renderString(s, xPos + 1, yPos + 1, color, true);
        drawString(s, xPos, yPos, color);
    }

    public void drawCenteredString(String s, int xPos, int yPos, int color) {
        drawStringWithShadow(s, xPos - FontRenderer.minecraftFontRenderer.getStringWidth(s) / 2, yPos, color);
    }

    public void drawString(String string, int x, int y, int color)
    {
        renderString(string, x, y, color, false);
    }

    public void renderString(String string, int x, int y, int color, boolean shadow)
    {
        if(string == null)
        {
            return;
        }
        if(shadow)
        {
            int l = color & 0xff000000;
            color = (color & 0xfcfcfc) >> 2;
            color += l;
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTextureName);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        float f = (float)(color >> 16 & 0xff) / 255F;
        float f1 = (float)(color >> 8 & 0xff) / 255F;
        float f2 = (float)(color & 0xff) / 255F;
        float f3 = (float)(color >> 24 & 0xff) / 255F;
        if(f3 == 0.0F)
        {
            f3 = 1.0F;
        }
        GL11.glColor4f(f, f1, f2, f3);
        buffer.clear();
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0.0F);
        for(int i1 = 0; i1 < string.length(); i1++)
        {
            for(; string.length() > i1 + 1 && string.charAt(i1) == '\247'; i1 += 2)
            {
                int j1 = "0123456789abcdef".indexOf(string.toLowerCase().charAt(i1 + 1));
                if(j1 < 0 || j1 > 15)
                {
                    j1 = 15;
                }
                buffer.put(fontDisplayLists + 256 + j1 + (shadow ? 16 : 0));
                if(buffer.remaining() == 0)
                {
                    buffer.flip();
                    GL11.glCallLists(buffer);
                    buffer.clear();
                }
            }

            if(i1 < string.length())
            {
                int k1 = InputSanitization.allowedCharacters.indexOf(string.charAt(i1));
                if(k1 >= 0)
                {
                    buffer.put(fontDisplayLists + k1);
                }
            }
            if(buffer.remaining() == 0)
            {
                buffer.flip();
                GL11.glCallLists(buffer);
                buffer.clear();
            }
        }

        buffer.flip();
        GL11.glCallLists(buffer);
        GL11.glPopMatrix();
    }

    public int getStringWidth(String string)
    {
        if(string == null)
        {
            return 0;
        }
        int width = 0;
        for(int i = 0; i < string.length(); i++)
        {
            if(string.charAt(i) == '\247')
            {
                i++;
                continue;
            }
            int k = InputSanitization.allowedCharacters.indexOf(string.charAt(i));
            if(k >= 0)
            {
                width += charWidth[k];
            }
        }

        return width;
    }

    private int charWidth[];
    public int fontTextureName;
    private int fontDisplayLists;
    private IntBuffer buffer;
}
