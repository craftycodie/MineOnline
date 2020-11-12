package gg.codie.mineonline.gui.textures;

import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.textures.EGUITexture;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TexturePackDefault extends TexturePackBase
{

    public TexturePackDefault()
    {
        texturePackName = -1;
        texturePackFileName = "Default";
        firstDescriptionLine = "The default look of Minecraft";
        try
        {
            texturePackThumbnail = ImageIO.read((TexturePackDefault.class).getResource(EGUITexture.PACK.textureName));
        }
        catch(IOException ioexception)
        {
            ioexception.printStackTrace();
        }
    }

    public void func_6484_b()
    {
        if(texturePackThumbnail != null)
        {
            //Loader.singleton.unloadTexture(EGUITexture.PACK.textureName);
        }
    }

    public void bindThumbnailTexture()
    {
        if(texturePackThumbnail != null && texturePackName < 0)
        {
            texturePackName = Loader.singleton.getGuiTexture(EGUITexture.PACK);
        }
        if(texturePackThumbnail != null)
        {
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, Loader.singleton.getGuiTexture(EGUITexture.PACK));
        } else
        {
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, Loader.singleton.getGuiTexture(EGUITexture.UNKNOWN_PACK));
        }

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    }

    private int texturePackName;
    private BufferedImage texturePackThumbnail;
}
