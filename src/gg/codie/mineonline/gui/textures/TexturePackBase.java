// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package gg.codie.mineonline.gui.textures;

import java.io.IOException;
import java.io.InputStream;

public abstract class TexturePackBase
{

    public TexturePackBase()
    {
    }

    public void func_6482_a()
    {
    }

    public void closeTexturePackFile()
    {
    }

    public void func_6485_a()
        throws IOException
    {
    }

    public void func_6484_b()
    {
    }

    public void bindThumbnailTexture()
    {
    }

    public InputStream getResourceAsStream(String s)
    {
        return (TexturePackBase.class).getResourceAsStream(s);
    }

    public String texturePackFileName;
    public String firstDescriptionLine;
    public String secondDescriptionLine;
    public String field_6488_d;
}
