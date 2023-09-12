package com.ahnewark.mineonline.protocol;

import com.ahnewark.mineonline.LauncherFiles;
import com.ahnewark.mineonline.client.LegacyGameManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ResourceDownloadURLConnection extends HttpURLConnection {
    public ResourceDownloadURLConnection(URL url) {
        super(url);
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    @Override
    public void connect() throws IOException {

    }

    @Override
    public int getResponseCode() throws IOException {
        return responseCode;
    }

    int responseCode = 200;
    @Override
    public InputStream getInputStream() {
        String resourcesVersion = LegacyGameManager.getVersion() != null ? LegacyGameManager.getVersion().resourcesVersion : "default";
        File resource = new File(LauncherFiles.MINEONLINE_RESOURCES_PATH + resourcesVersion + File.separator + url.toString().replace("http://s3.amazonaws.com/MinecraftResources/", "").replace("/", File.separator));

        try {
            return new FileInputStream(resource);
        } catch (FileNotFoundException e) {
            responseCode = 404;
            return null;
        }
    }
}
