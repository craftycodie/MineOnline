package gg.codie.mineonline.gui.textures;

import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.textures.EGUITexture;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.ZipFile;

public class TexturePackCustom extends TexturePackBase
{

    public TexturePackCustom(File file)
    {
        texturePackName = -1;
        texturePackFileName = file.getName();
        texturePackFile = file;
    }

    private String truncateString(String s)
    {
        if(s != null && s.length() > 34)
        {
            s = s.substring(0, 34);
        }
        return s;
    }

    public void func_6485_a()
        throws IOException
    {
        ZipFile zipfile = null;
        InputStream inputstream = null;
        try
        {
            zipfile = new ZipFile(texturePackFile);
            try
            {
                inputstream = zipfile.getInputStream(zipfile.getEntry("pack.txt"));
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
                firstDescriptionLine = truncateString(bufferedreader.readLine());
                secondDescriptionLine = truncateString(bufferedreader.readLine());
                bufferedreader.close();
                inputstream.close();
            }
            catch(Exception exception) { }
            try
            {
                inputstream = zipfile.getInputStream(zipfile.getEntry("pack.png"));
                texturePackThumbnail = ImageIO.read(inputstream);
                inputstream.close();
            }
            catch(Exception exception1) { }
            zipfile.close();
        }
        catch(Exception exception2)
        {
            exception2.printStackTrace();
        }
        finally
        {
            try
            {
                inputstream.close();
            }
            catch(Exception exception4) { }
            try
            {
                zipfile.close();
            }
            catch(Exception exception5) { }
        }
    }

    public void func_6484_b()
    {
        if(texturePackThumbnail != null)
        {
            Loader.singleton.unloadTexture("/texturepacks/" + texturePackFileName + "pack.png");
        }
        closeTexturePackFile();
    }

    public void bindThumbnailTexture()
    {
        if(texturePackThumbnail != null && texturePackName < 0)
        {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(texturePackThumbnail, "png", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());
                texturePackName = Loader.singleton.loadTexture("/texturepacks/" + texturePackFileName + "pack.png", is);
            } catch (Exception ex) {
                GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, Loader.singleton.getGuiTexture(EGUITexture.UNKNOWN_PACK));
            }
        }
        if(texturePackThumbnail != null)
        {
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, texturePackName);
        } else
        {
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, Loader.singleton.getGuiTexture(EGUITexture.UNKNOWN_PACK));
        }

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    }

    public void func_6482_a()
    {
        try
        {
            texturePackZipFile = new ZipFile(texturePackFile);
        }
        catch(Exception exception) { }
    }

    public void closeTexturePackFile()
    {
        try
        {
            texturePackZipFile.close();
        }
        catch(Exception exception) { }
        texturePackZipFile = null;
    }

    public InputStream getResourceAsStream(String s)
    {
        try
        {
            java.util.zip.ZipEntry zipentry = texturePackZipFile.getEntry(s.substring(1));
            if(zipentry != null)
            {
                return texturePackZipFile.getInputStream(zipentry);
            }
        }
        catch(Exception exception) { }
        return (TexturePackBase.class).getResourceAsStream(s);
    }

    private ZipFile texturePackZipFile;
    private int texturePackName;
    private BufferedImage texturePackThumbnail;
    private File texturePackFile;
}
