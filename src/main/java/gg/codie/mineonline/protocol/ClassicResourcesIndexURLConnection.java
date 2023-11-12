package gg.codie.mineonline.protocol;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.client.LegacyGameManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class ClassicResourcesIndexURLConnection extends HttpURLConnection {
    public ClassicResourcesIndexURLConnection(URL url) {
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
        return 200;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        String resourcesVersion = LegacyGameManager.getVersion() != null ? LegacyGameManager.getVersion().resourcesVersion : "default";

        StringBuilder response = new StringBuilder();

        File resourcesFolder = new File(LauncherFiles.MINEONLINE_RESOURCES_PATH + resourcesVersion);
        Files.find(resourcesFolder.toPath(),
                Integer.MAX_VALUE,
                (filePath, fileAttr) -> fileAttr.isRegularFile())
                .forEach((path) -> {
                    try {
                        if (path.toString().endsWith(".ogg") || path.toString().endsWith(".mus"))
                            response.append(path.toString().replace(resourcesFolder.getPath(), "").substring(1).replace("\\", "/") + "," + Files.size(path) + ",0\n");
                    } catch (Exception ex) {

                    }
                });

        return new ByteArrayInputStream(response.toString().getBytes());
    }


}
