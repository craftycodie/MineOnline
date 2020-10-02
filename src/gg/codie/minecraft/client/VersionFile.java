package gg.codie.minecraft.client;

import gg.codie.mineonline.LauncherFiles;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class VersionFile {
    public static String read() throws Exception {
        File versionFile = new File(LauncherFiles.MINECRAFT_VERSION_FILE);
        if (!versionFile.exists())
            return null;
        DataInputStream dis = new DataInputStream(new FileInputStream(versionFile));
        String version = dis.readUTF();
        dis.close();
        return version;
    }

    public static void delete() throws Exception {
        File versionFile = new File(LauncherFiles.MINECRAFT_VERSION_FILE);
        if (!versionFile.exists())
            return;
        versionFile.delete();
    }
}
