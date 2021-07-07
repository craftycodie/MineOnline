package gg.codie.mineonline.levels;

import gg.codie.mineonline.LauncherFiles;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class LoadLevelURLConnection extends HttpURLConnection {
    public LoadLevelURLConnection(URL url) {
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
    public InputStream getInputStream() throws IOException {
        int mapID = url.toString().charAt(url.toString().indexOf("id=") + 3) - 47;
        ClassicLevel classicLevel = ClassicLevel.fromFile(mapID);

        return classicLevel.toLoadResponse();
    }


}
