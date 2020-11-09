package gg.codie.mineonline.client;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.api.MineOnlineServerRepository;
import gg.codie.mineonline.gui.textures.TexturePackBase;
import gg.codie.mineonline.gui.textures.TexturePackCustom;
import gg.codie.mineonline.gui.textures.TexturePackDefault;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public class MinecraftTexturePackRepository {
    public static final MinecraftTexturePackRepository singleton = new MinecraftTexturePackRepository();
    private LinkedList<TexturePackBase> texturePacks;
    private HashMap<String, TexturePackCustom> customTexturePacks = new HashMap<>();

    public LinkedList<TexturePackBase> getTexturePacks() {
        return texturePacks;
    }

    public LinkedList<TexturePackBase> loadTexturePacks() {
        texturePacks = new LinkedList<>();

        texturePacks.add(new TexturePackDefault());
        File texturePacksDir = new File(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH);
        if(texturePacksDir.exists() && texturePacksDir.isDirectory())
        {
            File afile[] = texturePacksDir.listFiles();
            File afile1[] = afile;
            int i = afile1.length;
            for(int j = 0; j < i; j++)
            {
                File file = afile1[j];
                if(!file.isFile() || !file.getName().toLowerCase().endsWith(".zip"))
                {
                    continue;
                }
                String s = (new StringBuilder()).append(file.getName()).append(":").append(file.length()).append(":").append(file.lastModified()).toString();
                try
                {
                    if(!customTexturePacks.containsKey(s))
                    {
                        TexturePackCustom texturepackcustom = new TexturePackCustom(file);
                        texturepackcustom.field_6488_d = s;
                        customTexturePacks.put(s, texturepackcustom);
                        texturepackcustom.func_6485_a();
                    }
                    TexturePackBase texturepackbase1 = customTexturePacks.get(s);
                    if(texturepackbase1.texturePackFileName.equals(Settings.singleton.getTexturePack()))
                    {
                        Settings.singleton.setTexturePack(texturepackbase1.texturePackFileName);
                    }
                    texturePacks.add(texturepackbase1);
                }
                catch(IOException ioexception)
                {
                    ioexception.printStackTrace();
                }
            }

        }
//        texturePacks.removeAll(texturePacks);
        TexturePackBase texturepackbase;
        for(Iterator iterator = texturePacks.iterator(); iterator.hasNext(); customTexturePacks.remove(texturepackbase.field_6488_d))
        {
            texturepackbase = (TexturePackBase)iterator.next();
            texturepackbase.func_6484_b();
        }

        return texturePacks;
    }
}
