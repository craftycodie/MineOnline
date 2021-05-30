package gg.codie.minecraft.api;

import gg.codie.mineonline.LauncherFiles;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;

public class MinecraftLibrariesService {
    final static String LIBRARIES_URL = "https://libraries.minecraft.net/";

    public void downloadLibrary(String libraryPath) throws IOException {
        URL downloadURL = new URL(LIBRARIES_URL + libraryPath);
        HttpURLConnection httpConnection = (java.net.HttpURLConnection) (downloadURL.openConnection());
        InputStream in = httpConnection.getInputStream();

        String path = Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_PATH + libraryPath).toString();

        File clientJar = new File(path);
        clientJar.getParentFile().mkdirs();
        OutputStream out = new java.io.FileOutputStream(path, false);

        final byte[] data = new byte[1024];
        int count;
        while ((count = in.read(data, 0, 1024)) != -1) {
            out.write(data, 0, count);
        }
    }
}
